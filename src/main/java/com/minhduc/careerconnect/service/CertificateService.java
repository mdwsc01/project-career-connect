package com.minhduc.careerconnect.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.minhduc.careerconnect.dto.CertificateForm;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.CertificateRepository;
import com.minhduc.careerconnect.repository.CertificateRepository.Certificate;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.service.FileStorageService.StoredFile;

/**
 * Chứng chỉ và file minh chứng (task T4E796D).
 */
@Service
public class CertificateService {

    private static final String SUB_DIR = "certificates";

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;

    public CertificateService(CertificateRepository certificateRepository, ProfileRepository profileRepository,
                              FileStorageService fileStorageService) {
        this.certificateRepository = certificateRepository;
        this.profileRepository = profileRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Certificate> getMyCertificates(String ownerEmail) {
        return certificateRepository.findByProfileId(profileId(ownerEmail));
    }

    /**
     * Lưu file trước rồi mới ghi DB; nếu ghi DB lỗi thì xoá file vừa lưu để không để lại file rác.
     * File không hợp lệ -> InvalidFileException (controller báo lỗi trên form).
     */
    @Transactional
    public void create(String ownerEmail, CertificateForm form, MultipartFile file) {
        long profileId = profileId(ownerEmail);
        StoredFile stored = fileStorageService.store(file, SUB_DIR);
        try {
            certificateRepository.insert(profileId, form.getName().trim(), form.getIssuer().trim(),
                    form.getIssuedDate(), stored.storedName(), stored.originalName(), stored.contentType(),
                    stored.size());
        } catch (RuntimeException ex) {
            fileStorageService.delete(SUB_DIR, stored.storedName());
            throw ex;
        }
    }

    /**
     * Xoá chứng chỉ của chính mình và xoá luôn file minh chứng.
     */
    @Transactional
    public Certificate delete(long id, String ownerEmail) {
        Certificate certificate = get(id);
        if (!certificate.ownerEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Chứng chỉ " + id + " không thuộc hồ sơ của " + ownerEmail);
        }
        certificateRepository.delete(id);
        fileStorageService.delete(SUB_DIR, certificate.fileStoredName());
        return certificate;
    }

    /**
     * Lấy chứng chỉ để xem/tải file: chỉ chủ hồ sơ hoặc ADMIN, người khác -> 403.
     */
    public Certificate getForFileAccess(long id, Authentication auth) {
        Certificate certificate = get(id);
        boolean isOwner = certificate.ownerEmail().equalsIgnoreCase(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Không có quyền xem file chứng chỉ " + id);
        }
        if (!certificate.hasFile()) {
            throw new ResourceNotFoundException("Chứng chỉ này chưa có file minh chứng");
        }
        return certificate;
    }

    public Resource loadFile(Certificate certificate) {
        return fileStorageService.load(SUB_DIR, certificate.fileStoredName());
    }

    public long getMaxSizeMb() {
        return fileStorageService.getMaxSizeMb();
    }

    private Certificate get(long id) {
        return certificateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chứng chỉ", id));
    }

    private long profileId(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"))
                .profileId();
    }
}
