package com.minhduc.careerconnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.CreatePartnerForm;
import com.minhduc.careerconnect.dto.PageResult;
import com.minhduc.careerconnect.dto.UserSearchFilter;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.CompanyRepository;
import com.minhduc.careerconnect.repository.UserRepository;
import com.minhduc.careerconnect.repository.UserRepository.UserRow;

/**
 * Nghiệp vụ trang quản trị tài khoản (task T4E7967).
 */
@Service
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);
    public static final int PAGE_SIZE = 10;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, CompanyRepository companyRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<UserRow> search(UserSearchFilter filter) {
        long total = userRepository.count(filter);
        int totalPages = (int) Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
        if (filter.getPage() > totalPages) {
            filter.setPage(totalPages);
        }
        int offset = (filter.getPage() - 1) * PAGE_SIZE;
        return new PageResult<>(userRepository.search(filter, offset, PAGE_SIZE), filter.getPage(), PAGE_SIZE, total);
    }

    /**
     * Tạo tài khoản PARTNER kèm công ty. Nếu mã số thuế đã có trong hệ thống thì gắn vào công ty đó,
     * ngược lại tạo công ty mới.
     */
    @Transactional
    public long createPartner(CreatePartnerForm form, String adminEmail) {
        String email = form.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email này đã được sử dụng");
        }
        String taxCode = (form.getTaxCode() == null || form.getTaxCode().isBlank()) ? null : form.getTaxCode().trim();
        long companyId = (taxCode != null)
                ? companyRepository.findIdByTaxCode(taxCode)
                .orElseGet(() -> companyRepository.insert(form.getCompanyName().trim(), taxCode))
                : companyRepository.insert(form.getCompanyName().trim(), null);

        long userId = userRepository.insert(email, passwordEncoder.encode(form.getPassword()),
                form.getFullName().trim(), "PARTNER", companyId);
        log.info("Admin {} tạo tài khoản đối tác {} (công ty id {})", adminEmail, email, companyId);
        return userId;
    }

    @Transactional
    public UserRow lock(long userId, String reason, String adminEmail) {
        UserRow user = userRepository.findRowById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản", userId));
        if (user.email().equalsIgnoreCase(adminEmail)) {
            throw new BusinessException("Bạn không thể tự khoá tài khoản của chính mình.");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("Vui lòng nhập lý do khoá tài khoản.");
        }
        if (reason.length() > 255) {
            throw new BusinessException("Lý do khoá tối đa 255 ký tự.");
        }
        userRepository.lock(userId, reason.trim());
        log.info("Admin {} khoá tài khoản {}: {}", adminEmail, user.email(), reason);
        return user;
    }

    @Transactional
    public UserRow unlock(long userId, String adminEmail) {
        UserRow user = userRepository.findRowById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản", userId));
        userRepository.unlock(userId);
        log.info("Admin {} mở khoá tài khoản {}", adminEmail, user.email());
        return user;
    }
}
