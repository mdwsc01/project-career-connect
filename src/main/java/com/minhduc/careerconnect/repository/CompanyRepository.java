package com.minhduc.careerconnect.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Long> findIdByTaxCode(String taxCode) {
        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM companies WHERE tax_code = ?", Long.class, taxCode);
        return ids.stream().findFirst();
    }

    /**
     * Thêm công ty, trả về id. taxCode có thể null.
     */
    public long insert(String name, String taxCode) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO companies (name, tax_code) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, taxCode);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
