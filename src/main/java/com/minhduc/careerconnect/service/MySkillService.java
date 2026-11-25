package com.minhduc.careerconnect.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.SkillCategory;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.ProfileRepository;
import com.minhduc.careerconnect.repository.ProfileSkillRepository;
import com.minhduc.careerconnect.repository.ProfileSkillRepository.MySkill;
import com.minhduc.careerconnect.repository.SkillRepository;
import com.minhduc.careerconnect.repository.SkillRepository.Skill;

/**
 * Kỹ năng trong hồ sơ của sinh viên đang đăng nhập (task T4E796B).
 * Mọi thao tác đều gắn với hồ sơ của chính người đăng nhập.
 */
@Service
public class MySkillService {

    /**
     * Kết quả thêm kỹ năng, để controller báo lỗi ngay trên form.
     */
    public enum AddResult {OK, NOT_FOUND, DUPLICATE}

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final SkillRepository skillRepository;

    public MySkillService(ProfileRepository profileRepository, ProfileSkillRepository profileSkillRepository,
                          SkillRepository skillRepository) {
        this.profileRepository = profileRepository;
        this.profileSkillRepository = profileSkillRepository;
        this.skillRepository = skillRepository;
    }

    public List<MySkill> getMySkills(String ownerEmail) {
        return profileSkillRepository.findByProfileId(profileId(ownerEmail));
    }

    /**
     * Kỹ năng gom theo nhóm, đúng thứ tự 5 nhóm; nhóm trống bị bỏ qua.
     */
    public Map<SkillCategory, List<MySkill>> getMySkillsGrouped(String ownerEmail) {
        Map<SkillCategory, List<MySkill>> grouped = new LinkedHashMap<>();
        for (SkillCategory category : SkillCategory.values()) {
            grouped.put(category, new ArrayList<>());
        }
        for (MySkill skill : getMySkills(ownerEmail)) {
            grouped.get(skill.category()).add(skill);
        }
        grouped.values().removeIf(List::isEmpty);
        return grouped;
    }

    /**
     * Kỹ năng còn có thể chọn: đang hiện và chưa có trong hồ sơ.
     */
    public List<Skill> getAvailableSkills(String ownerEmail) {
        return skillRepository.findVisibleNotInProfile(profileId(ownerEmail));
    }

    @Transactional
    public AddResult add(String ownerEmail, String skillName, int level) {
        long profileId = profileId(ownerEmail);
        Skill skill = skillRepository.findVisibleByName(skillName.trim()).orElse(null);
        if (skill == null) {
            return AddResult.NOT_FOUND;
        }
        if (profileSkillRepository.exists(profileId, skill.id())) {
            return AddResult.DUPLICATE;
        }
        try {
            profileSkillRepository.insert(profileId, skill.id(), level);
        } catch (DuplicateKeyException ex) { // khoá chính (profile_id, skill_id) chặn trùng
            return AddResult.DUPLICATE;
        }
        return AddResult.OK;
    }

    @Transactional
    public void changeLevel(String ownerEmail, long skillId, int level) {
        if (level < 1 || level > 5) {
            throw new BusinessException("Mức kỹ năng phải từ 1 đến 5.");
        }
        if (profileSkillRepository.updateLevel(profileId(ownerEmail), skillId, level) == 0) {
            throw new ResourceNotFoundException("Kỹ năng này không có trong hồ sơ của bạn");
        }
    }

    @Transactional
    public void remove(String ownerEmail, long skillId) {
        if (profileSkillRepository.delete(profileId(ownerEmail), skillId) == 0) {
            throw new ResourceNotFoundException("Kỹ năng này không có trong hồ sơ của bạn");
        }
    }

    private long profileId(String ownerEmail) {
        return profileRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ của bạn"))
                .profileId();
    }
}
