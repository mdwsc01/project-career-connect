package com.minhduc.careerconnect.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.minhduc.careerconnect.dto.SkillCategory;

@Repository
public class SkillRepository {

    /**
     * Một kỹ năng kèm số hồ sơ đang dùng.
     */
    public record Skill(long id, String name, SkillCategory category, boolean hidden, int usageCount) {
    }

    private static final String SELECT =
            "SELECT s.id, s.name, s.category, s.is_hidden, "
                    + "(SELECT COUNT(*) FROM profile_skills ps WHERE ps.skill_id = s.id) AS usage_count "
                    + "FROM skills s";

    private final JdbcTemplate jdbcTemplate;

    public SkillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Danh sách theo bộ lọc; category null = mọi nhóm; hiddenFilter: "SHOWN", "HIDDEN" hoặc null = tất cả.
     */
    public List<Skill> search(String q, SkillCategory category, String hiddenFilter) {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            conditions.add("s.name LIKE ?");
            params.add("%" + q.trim() + "%");
        }
        if (category != null) {
            conditions.add("s.category = ?");
            params.add(category.name());
        }
        if ("SHOWN".equals(hiddenFilter)) {
            conditions.add("s.is_hidden = FALSE");
        } else if ("HIDDEN".equals(hiddenFilter)) {
            conditions.add("s.is_hidden = TRUE");
        }
        String where = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
        return jdbcTemplate.query(SELECT + where + " ORDER BY s.category, s.name", this::map, params.toArray());
    }

    public Optional<Skill> findById(long id) {
        return jdbcTemplate.query(SELECT + " WHERE s.id = ?", this::map, id).stream().findFirst();
    }

    /**
     * Có kỹ năng khác trùng tên không. Cột name dùng collation utf8mb4_unicode_ci
     * nên so sánh không phân biệt hoa thường ("java" = "Java").
     */
    public boolean existsByNameExcludingId(String name, Long excludeId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM skills WHERE name = ? AND id <> ?",
                Integer.class, name, excludeId == null ? -1L : excludeId);
        return count != null && count > 0;
    }

    /**
     * Tìm kỹ năng đang hiện theo tên (không phân biệt hoa thường).
     */
    public Optional<Skill> findVisibleByName(String name) {
        return jdbcTemplate.query(SELECT + " WHERE s.name = ? AND s.is_hidden = FALSE", this::map, name)
                .stream().findFirst();
    }

    /**
     * Kỹ năng đang hiện mà hồ sơ chưa chọn (cho ô tìm nhanh).
     */
    public List<Skill> findVisibleNotInProfile(long profileId) {
        return jdbcTemplate.query(SELECT + " WHERE s.is_hidden = FALSE AND s.id NOT IN "
                        + "(SELECT ps.skill_id FROM profile_skills ps WHERE ps.profile_id = ?) ORDER BY s.name",
                this::map, profileId);
    }

    public void insert(String name, SkillCategory category) {
        jdbcTemplate.update("INSERT INTO skills (name, category) VALUES (?, ?)", name, category.name());
    }

    public void update(long id, String name, SkillCategory category) {
        jdbcTemplate.update("UPDATE skills SET name = ?, category = ? WHERE id = ?", name, category.name(), id);
    }

    public void setHidden(long id, boolean hidden) {
        jdbcTemplate.update("UPDATE skills SET is_hidden = ? WHERE id = ?", hidden, id);
    }

    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM skills WHERE id = ?", id);
    }

    private Skill map(ResultSet rs, int rowNum) throws SQLException {
        return new Skill(rs.getLong("id"), rs.getString("name"), SkillCategory.valueOf(rs.getString("category").toUpperCase()),
                rs.getBoolean("is_hidden"), rs.getInt("usage_count"));
    }
}
