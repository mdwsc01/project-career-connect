package com.minhduc.careerconnect.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EducationRepository {

    /** Một mục học vấn, kèm email chủ hồ sơ để kiểm tra quyền. */
    public record Education(long id, long profileId, String ownerEmail, String school, String major,
                            LocalDate startDate, LocalDate endDate, BigDecimal gpa) {
    }

    private static final String SELECT =
            "SELECT e.id, e.profile_id, u.email AS owner_email, e.school, e.major, e.start_date, e.end_date, e.gpa "
                    + "FROM educations e JOIN profiles p ON p.id = e.profile_id JOIN users u ON u.id = p.user_id";

    private final JdbcTemplate jdbcTemplate;

    public EducationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Học vấn của một hồ sơ, mới nhất trước. */
    public List<Education> findByProfileId(long profileId) {
        return jdbcTemplate.query(SELECT + " WHERE e.profile_id = ? ORDER BY e.start_date DESC, e.id DESC",
                this::map, profileId);
    }

    public Optional<Education> findById(long id) {
        return jdbcTemplate.query(SELECT + " WHERE e.id = ?", this::map, id).stream().findFirst();
    }

    public void insert(long profileId, String school, String major, LocalDate start, LocalDate end, BigDecimal gpa) {
        jdbcTemplate.update(
                "INSERT INTO educations (profile_id, school, major, start_date, end_date, gpa) VALUES (?, ?, ?, ?, ?, ?)",
                profileId, school, major, toSql(start), toSql(end), gpa);
    }

    public void update(long id, String school, String major, LocalDate start, LocalDate end, BigDecimal gpa) {
        jdbcTemplate.update(
                "UPDATE educations SET school = ?, major = ?, start_date = ?, end_date = ?, gpa = ? WHERE id = ?",
                school, major, toSql(start), toSql(end), gpa, id);
    }

    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM educations WHERE id = ?", id);
    }

    private Education map(ResultSet rs, int rowNum) throws SQLException {
        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");
        return new Education(rs.getLong("id"), rs.getLong("profile_id"), rs.getString("owner_email"),
                rs.getString("school"), rs.getString("major"),
                start == null ? null : start.toLocalDate(), end == null ? null : end.toLocalDate(),
                rs.getBigDecimal("gpa"));
    }

    private static Date toSql(LocalDate d) {
        return d == null ? null : Date.valueOf(d);
    }
}
