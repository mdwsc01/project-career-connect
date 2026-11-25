package com.minhduc.careerconnect.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minhduc.careerconnect.exception.InvalidFileException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;

/**
 * Lưu file minh chứng lên ổ đĩa (task T4E796D).
 * - Chỉ nhận PDF/JPG/PNG, tối đa app.upload.max-size-mb (mặc định 5MB).
 * - Kiểm tra cả phần mở rộng, content type VÀ vài byte đầu file (chữ ký file),
 * để file .exe đổi tên thành .pdf cũng bị chặn.
 * - Tên file lưu là UUID ngẫu nhiên; không dùng tên người dùng gửi lên.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    /**
     * Phần mở rộng -> content type hợp lệ.
     */
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "pdf", Set.of("application/pdf"),
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png"));

    /**
     * Thông tin file đã lưu.
     */
    public record StoredFile(String storedName, String originalName, String contentType, long size) {
    }

    private final Path root;
    private final long maxBytes;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir,
                              @Value("${app.upload.max-size-mb:5}") long maxSizeMb) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxBytes = maxSizeMb * 1024 * 1024;
        Files.createDirectories(root);
        log.info("Thư mục lưu file tải lên: {}", root);
    }

    public long getMaxSizeMb() {
        return maxBytes / 1024 / 1024;
    }

    /**
     * Kiểm tra và lưu file vào thư mục con (VD: "certificates").
     */
    public StoredFile store(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Vui lòng chọn file minh chứng");
        }
        if (file.getSize() > maxBytes) {
            throw new InvalidFileException("File vượt quá " + getMaxSizeMb() + "MB");
        }
        String originalName = Paths.get(file.getOriginalFilename() == null ? "" : file.getOriginalFilename())
                .getFileName().toString();
        String ext = extensionOf(originalName);
        if (!ALLOWED.containsKey(ext)) {
            throw new InvalidFileException("Chỉ nhận file PDF, JPG hoặc PNG");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.get(ext).contains(contentType)) {
            throw new InvalidFileException("Loại file không khớp với phần mở rộng ." + ext);
        }
        if (!hasValidSignature(file, ext)) {
            throw new InvalidFileException("Nội dung file không phải PDF/JPG/PNG hợp lệ");
        }

        String storedName = UUID.randomUUID() + "." + (ext.equals("jpeg") ? "jpg" : ext);
        Path target = resolve(subDir, storedName);
        try (InputStream in = file.getInputStream()) {
            Files.createDirectories(target.getParent());
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Không lưu được file tải lên", ex);
        }
        return new StoredFile(storedName, originalName, contentType, file.getSize());
    }

    public Resource load(String subDir, String storedName) {
        Path path = resolve(subDir, storedName);
        if (!Files.isRegularFile(path)) {
            throw new ResourceNotFoundException("Không tìm thấy file minh chứng");
        }
        return new FileSystemResource(path);
    }

    /**
     * Xoá file; lỗi chỉ ghi log, không làm hỏng thao tác chính.
     */
    public void delete(String subDir, String storedName) {
        if (storedName == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(subDir, storedName));
        } catch (IOException | RuntimeException ex) {
            log.warn("Không xoá được file {}/{}: {}", subDir, storedName, ex.getMessage());
        }
    }

    /**
     * Ghép đường dẫn và chặn kiểu "../../" thoát ra ngoài thư mục upload.
     */
    private Path resolve(String subDir, String storedName) {
        Path path = root.resolve(subDir).resolve(storedName).normalize();
        if (!path.startsWith(root)) {
            throw new InvalidFileException("Đường dẫn file không hợp lệ");
        }
        return path;
    }

    private static String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * So vài byte đầu file với chữ ký chuẩn của PDF/PNG/JPEG.
     */
    private static boolean hasValidSignature(MultipartFile file, String ext) {
        byte[] head = new byte[8];
        int read;
        try (InputStream in = file.getInputStream()) {
            read = in.readNBytes(head, 0, head.length);
        } catch (IOException ex) {
            return false;
        }
        return switch (ext) {
            case "pdf" -> read >= 4 && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F';
            case "png" -> read >= 8 && (head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G';
            case "jpg", "jpeg" -> read >= 3 && (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8
                    && (head[2] & 0xFF) == 0xFF;
            default -> false;
        };
    }
}
