package com.minhduc.careerconnect.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

    /**
     * Một dự án, kèm email chủ hồ sơ để kiểm tra quyền.
     */
    public record Project(long id, long profileId, String ownerEmail, String name, String role, String techStack,
                          String githubUrl, String demoUrl, String description) {
    }

    private static final String SELECT =
            "SELECT pr.id, pr.profile_id, u.email AS owner_email, pr.name, pr.role, pr.tech_stack, "
                    + "pr.github_url, pr.demo_url, pr.description "
                    + "FROM projects pr JOIN profiles p ON p.id = pr.profile_id JOIN users u ON u.id = p.user_id";

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Dự án của hồ sơ, mới thêm trước.
     */
    public List<Project> findByProfileId(long profileId) {
        return jdbcTemplate.query(SELECT + " WHERE pr.profile_id = ? ORDER BY pr.id DESC", this::map, profileId);
    }

    public Optional<Project> findById(long id) {
        return jdbcTemplate.query(SELECT + " WHERE pr.id = ?", this::map, id).stream().findFirst();
    }

    public int countByProfileId(long profileId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM projects WHERE profile_id = ?", Integer.class, profileId);
        return count == null ? 0 : count;
    }

    public void insert(long profileId, String name, String role, String techStack, String githubUrl,
                       String demoUrl, String description) {
        jdbcTemplate.update(
                "INSERT INTO projects (profile_id, name, role, tech_stack, github_url, demo_url, description) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                profileId, name, role, techStack, githubUrl, demoUrl, description);
    }

    public void update(long id, String name, String role, String techStack, String githubUrl,
                       String demoUrl, String description) {
        jdbcTemplate.update(
                "UPDATE projects SET name = ?, role = ?, tech_stack = ?, github_url = ?, demo_url = ?, description = ? "
                        + "WHERE id = ?",
                name, role, techStack, githubUrl, demoUrl, description, id);
    }

    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM projects WHERE id = ?", id);
    }

    private Project map(ResultSet rs, int rowNum) throws SQLException {
        return new Project(rs.getLong("id"), rs.getLong("profile_id"), rs.getString("owner_email"),
                rs.getString("name"), rs.getString("role"), rs.getString("tech_stack"),
                rs.getString("github_url"), rs.getString("demo_url"), rs.getString("description"));
    }
}
