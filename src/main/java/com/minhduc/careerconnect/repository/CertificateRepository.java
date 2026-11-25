package com.minhduc.careerconnect.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CertificateRepository {

    /**
     * Một chứng chỉ kèm thông tin file và email chủ hồ sơ (để kiểm tra quyền).
     */
    public record Certificate(long id, long profileId, String ownerEmail, String name, String issuer,
                              LocalDate issuedDate, String fileStoredName, String fileOriginalName,
                              String fileContentType, Long fileSize) {

        public boolean hasFile() {
            return fileStoredName != null;
        }
    }

    private static final String SELECT =
            "SELECT c.id, c.profile_id, u.email AS owner_email, c.name, c.issuer, c.issued_date, "
                    + "c.file_stored_name, c.file_original_name, c.file_content_type, c.file_size "
                    + "FROM certificates c JOIN profiles p ON p.id = c.profile_id JOIN users u ON u.id = p.user_id";

    private final JdbcTemplate jdbcTemplate;

    public CertificateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Certificate> findByProfileId(long profileId) {
        return jdbcTemplate.query(SELECT + " WHERE c.profile_id = ? ORDER BY c.issued_date DESC, c.id DESC",
                this::map, profileId);
    }

    public Optional<Certificate> findById(long id) {
        return jdbcTemplate.query(SELECT + " WHERE c.id = ?", this::map, id).stream().findFirst();
    }

    public void insert(long profileId, String name, String issuer, LocalDate issuedDate, String storedName,
                       String originalName, String contentType, long size) {
        jdbcTemplate.update(
                "INSERT INTO certificates (profile_id, name, issuer, issued_date, file_stored_name, "
                        + "file_original_name, file_content_type, file_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                profileId, name, issuer, Date.valueOf(issuedDate), storedName, originalName, contentType, size);
    }

    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM certificates WHERE id = ?", id);
    }

    private Certificate map(ResultSet rs, int rowNum) throws SQLException {
        Date issued = rs.getDate("issued_date");
        long size = rs.getLong("file_size");
        boolean sizeIsNull = rs.wasNull(); // phải gọi ngay sau getLong
        return new Certificate(rs.getLong("id"), rs.getLong("profile_id"), rs.getString("owner_email"),
                rs.getString("name"), rs.getString("issuer"), issued == null ? null : issued.toLocalDate(),
                rs.getString("file_stored_name"), rs.getString("file_original_name"),
                rs.getString("file_content_type"), sizeIsNull ? null : size);
    }
}
