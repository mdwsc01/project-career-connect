package com.minhduc.careerconnect.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.MyProfileForm;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;

/**
 * Xem và sửa hồ sơ của chính người đang đăng nhập (task T4E7968).
 * Hồ sơ luôn được tìm theo email người đăng nhập, không nhận id từ URL,
 * nên người dùng không thể xem/sửa hồ sơ của người khác.
 */
@Service
public class MyProfileService {

    private final ProfileRepository profileRepository;

    public MyProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public MyProfile getMyProfile(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"));
    }

    /** Đổ dữ liệu hiện tại vào form sửa. */
    public MyProfileForm toForm(MyProfile p) {
        MyProfileForm form = new MyProfileForm();
        form.setFullName(p.fullName());
        form.setDateOfBirth(p.dateOfBirth());
        form.setPhone(p.phone());
        form.setSummary(p.summary());
        form.setDesiredPosition(p.desiredPosition());
        form.setDesiredWorkLocation(p.desiredWorkLocation());
        return form;
    }

    @Transactional
    public void update(String ownerEmail, MyProfileForm form) {
        MyProfile current = getMyProfile(ownerEmail);
        profileRepository.updateOwnerFullName(current.userId(), form.getFullName().trim());
        profileRepository.updateBasicInfo(current.profileId(), form.getDateOfBirth(),
                blankToNull(form.getPhone()), blankToNull(form.getSummary()),
                form.getDesiredPosition().trim(), blankToNull(form.getDesiredWorkLocation()));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
