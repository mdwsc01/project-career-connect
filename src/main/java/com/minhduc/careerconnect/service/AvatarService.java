package com.minhduc.careerconnect.service;

import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;
import com.minhduc.careerconnect.service.FileStorageService.StoredFile;

/**
 * Ảnh đại diện của hồ sơ (task T4E796E).
 */
@Service
public class AvatarService {

    public static final String SUB_DIR = "avatars";
    public static final long MAX_BYTES = 2L * 1024 * 1024; // 2MB
    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png");

    /**
     * Bảng màu nền cho ảnh mặc định (chữ cái đầu tên).
     */
    private static final String[] COLORS = {
            "#0d6efd", "#6610f2", "#6f42c1", "#d63384", "#dc3545", "#fd7e14", "#198754", "#20c997", "#0dcaf0"
    };

    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public AvatarService(ProfileRepository profileRepository, FileStorageService fileStorageService) {
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Lưu ảnh mới, cập nhật hồ sơ rồi xoá ảnh cũ. File không hợp lệ -> InvalidFileException.
     */
    @Transactional
    public void upload(String ownerEmail, MultipartFile file) {
        MyProfile profile = myProfile(ownerEmail);
        StoredFile stored = fileStorageService.store(file, SUB_DIR, ALLOWED, MAX_BYTES,
                "Ảnh đại diện chỉ nhận file JPG hoặc PNG");
        try {
            profileRepository.updateAvatar(profile.profileId(), stored.storedName());
        } catch (RuntimeException ex) {
            fileStorageService.delete(SUB_DIR, stored.storedName());
            throw ex;
        }
        if (profile.hasAvatar()) {
            fileStorageService.delete(SUB_DIR, profile.avatarFile()); // thay ảnh thì xoá ảnh cũ
        }
    }

    /**
     * Bỏ ảnh, quay về ảnh mặc định theo chữ cái đầu tên.
     */
    @Transactional
    public void remove(String ownerEmail) {
        MyProfile profile = myProfile(ownerEmail);
        if (profile.hasAvatar()) {
            profileRepository.updateAvatar(profile.profileId(), null);
            fileStorageService.delete(SUB_DIR, profile.avatarFile());
        }
    }

    public Resource load(long profileId) {
        String file = profileRepository.findAvatarFile(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Hồ sơ này chưa có ảnh đại diện"));
        return fileStorageService.load(SUB_DIR, file);
    }

    public String contentTypeOf(long profileId) {
        String file = profileRepository.findAvatarFile(profileId).orElse("");
        return file.endsWith(".png") ? "image/png" : "image/jpeg";
    }

    /**
     * Chữ cái đầu của TÊN (từ cuối cùng trong họ tên): "Nguyễn Văn An" -> "A".
     */
    public static String initialOf(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] words = fullName.trim().split("\\s+");
        String lastWord = words[words.length - 1];
        return lastWord.substring(0, lastWord.offsetByCodePoints(0, 1)).toUpperCase();
    }

    /**
     * Màu nền cố định theo họ tên (cùng tên luôn cùng màu).
     */
    public static String colorOf(String fullName) {
        int index = Math.floorMod(fullName == null ? 0 : fullName.hashCode(), COLORS.length);
        return COLORS[index];
    }

    private MyProfile myProfile(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"));
    }
}
