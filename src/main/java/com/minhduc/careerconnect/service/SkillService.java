package com.minhduc.careerconnect.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhduc.careerconnect.dto.SkillCategory;
import com.minhduc.careerconnect.dto.SkillForm;
import com.minhduc.careerconnect.exception.BusinessException;
import com.minhduc.careerconnect.exception.ResourceNotFoundException;
import com.minhduc.careerconnect.repository.SkillRepository;
import com.minhduc.careerconnect.repository.SkillRepository.Skill;

/**
 * Danh mục kỹ năng (task T4E796A).
 */
@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);

    /**
     * Kết quả lưu: OK hoặc trùng tên (để controller báo lỗi ngay trên form).
     */
    public enum SaveResult {OK, DUPLICATE_NAME}

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> search(String q, SkillCategory category, String hiddenFilter) {
        return skillRepository.search(q, category, hiddenFilter);
    }

    public Skill get(long id) {
        return skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kỹ năng", id));
    }

    public SkillForm toForm(Skill skill) {
        SkillForm form = new SkillForm();
        form.setName(skill.name());
        form.setCategory(skill.category());
        return form;
    }

    @Transactional
    public SaveResult create(SkillForm form) {
        String name = normalize(form.getName());
        if (skillRepository.existsByNameExcludingId(name, null)) {
            return SaveResult.DUPLICATE_NAME;
        }
        try {
            skillRepository.insert(name, form.getCategory());
        } catch (DuplicateKeyException ex) { // phòng khi hai người thêm cùng lúc
            return SaveResult.DUPLICATE_NAME;
        }
        log.info("Thêm kỹ năng {}", name);
        return SaveResult.OK;
    }

    @Transactional
    public SaveResult update(long id, SkillForm form) {
        get(id);
        String name = normalize(form.getName());
        if (skillRepository.existsByNameExcludingId(name, id)) {
            return SaveResult.DUPLICATE_NAME;
        }
        try {
            skillRepository.update(id, name, form.getCategory());
        } catch (DuplicateKeyException ex) {
            return SaveResult.DUPLICATE_NAME;
        }
        return SaveResult.OK;
    }

    @Transactional
    public Skill setHidden(long id, boolean hidden) {
        Skill skill = get(id);
        skillRepository.setHidden(id, hidden);
        return skill;
    }

    /**
     * Chỉ xoá được kỹ năng chưa có sinh viên nào dùng; đã dùng thì chỉ được ẩn.
     */
    @Transactional
    public Skill delete(long id) {
        Skill skill = get(id);
        if (skill.usageCount() > 0) {
            throw new BusinessException("Kỹ năng \"" + skill.name() + "\" đang được " + skill.usageCount()
                    + " sinh viên sử dụng nên không thể xoá. Bạn có thể ẩn kỹ năng này.");
        }
        skillRepository.delete(id);
        log.info("Xoá kỹ năng {}", skill.name());
        return skill;
    }

    /**
     * Bỏ khoảng trắng thừa: "  Spring   Boot " -> "Spring Boot".
     */
    private static String normalize(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }
}
