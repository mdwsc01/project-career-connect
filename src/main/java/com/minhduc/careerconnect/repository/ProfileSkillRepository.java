package com.minhduc.careerconnect.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.minhduc.careerconnect.dto.SkillCategory;

@Repository
public class ProfileSkillRepository {

    /**
     * Một kỹ năng trong hồ sơ kèm mức tự đánh giá.
     */
    public record MySkill(long skillId, String name, SkillCategory category, int level, boolean hidden) {
    }

    private final JdbcTemplate jdbcTemplate;

    public ProfileSkillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Kỹ năng của hồ sơ, sắp theo nhóm rồi mức giảm dần.
     */
    public List<MySkill> findByProfileId(long profileId) {
        return jdbcTemplate.query(
                "SELECT s.id, s.name, s.category, ps.level, s.is_hidden FROM profile_skills ps "
                        + "JOIN skills s ON s.id = ps.skill_id WHERE ps.profile_id = ? "
                        + "ORDER BY s.category, ps.level DESC, s.name",
                (rs, i) -> new MySkill(rs.getLong("id"), rs.getString("name"),
                        SkillCategory.valueOf(rs.getString("category").toUpperCase()),
                        rs.getInt("level"), rs.getBoolean("is_hidden")),
                profileId);
    }

    public boolean exists(long profileId, long skillId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM profile_skills WHERE profile_id = ? AND skill_id = ?",
                Integer.class, profileId, skillId);
        return count != null && count > 0;
    }

    public void insert(long profileId, long skillId, int level) {
        jdbcTemplate.update("INSERT INTO profile_skills (profile_id, skill_id, level) VALUES (?, ?, ?)",
                profileId, skillId, level);
    }

    /**
     * @return số dòng được cập nhật (0 nếu hồ sơ không có kỹ năng này)
     */
    public int updateLevel(long profileId, long skillId, int level) {
        return jdbcTemplate.update("UPDATE profile_skills SET level = ? WHERE profile_id = ? AND skill_id = ?",
                level, profileId, skillId);
    }

    /**
     * @return số dòng bị xoá
     */
    public int delete(long profileId, long skillId) {
        return jdbcTemplate.update("DELETE FROM profile_skills WHERE profile_id = ? AND skill_id = ?",
                profileId, skillId);
    }
}
