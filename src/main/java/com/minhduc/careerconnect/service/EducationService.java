package com.minhduc.careerconnect.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.EducationForm;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.EducationRepository;
import com.minhduc.careerconnect.repository.EducationRepository.Education;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProfileRepository.MyProfile;

/**
 * Học vấn trong hồ sơ (task T4E7969).
 * Mọi thao tác sửa/xoá đều kiểm tra mục học vấn có thuộc hồ sơ của người đang đăng nhập không;
 * nếu là của người khác (đổi id trên URL) thì ném AccessDeniedException -> trang 403.
 */
@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;

    public EducationService(EducationRepository educationRepository, ProfileRepository profileRepository) {
        this.educationRepository = educationRepository;
        this.profileRepository = profileRepository;
    }

    public List<Education> getMyEducations(String ownerEmail) {
        return educationRepository.findByProfileId(myProfile(ownerEmail).profileId());
    }

    /** Lấy một mục học vấn của chính mình (để sửa). */
    public Education getOwned(long id, String ownerEmail) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Học vấn", id));
        if (!education.ownerEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Học vấn " + id + " không thuộc hồ sơ của " + ownerEmail);
        }
        return education;
    }

    public EducationForm toForm(Education e) {
        EducationForm form = new EducationForm();
        form.setSchool(e.school());
        form.setMajor(e.major());
        form.setStartDate(e.startDate());
        form.setEndDate(e.endDate());
        form.setGpa(e.gpa());
        return form;
    }

    @Transactional
    public void create(String ownerEmail, EducationForm form) {
        educationRepository.insert(myProfile(ownerEmail).profileId(), form.getSchool().trim(), form.getMajor().trim(),
                form.getStartDate(), form.getEndDate(), form.getGpa());
    }

    @Transactional
    public void update(long id, String ownerEmail, EducationForm form) {
        getOwned(id, ownerEmail);
        educationRepository.update(id, form.getSchool().trim(), form.getMajor().trim(),
                form.getStartDate(), form.getEndDate(), form.getGpa());
    }

    @Transactional
    public Education delete(long id, String ownerEmail) {
        Education education = getOwned(id, ownerEmail);
        educationRepository.delete(id);
        return education;
    }

    private MyProfile myProfile(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"));
    }
}
