package com.jimmy.etfquant.repository;

import com.jimmy.etfquant.dto.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AppUser> findByPhoneNumber(String phoneNumber) {
        String sql = """
                SELECT user_id, phone_number, password_hash, user_name
                FROM app_users
                WHERE phone_number = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new AppUser(
                        rs.getLong("user_id"),
                        rs.getString("phone_number"),
                        rs.getString("password_hash"),
                        rs.getString("user_name")
                ));
            }
            return Optional.empty();
        }, phoneNumber);
    }

    public Long createUser(String phoneNumber, String passwordHash, String userName) {
        String sql = """
                INSERT INTO app_users (
                    phone_number,
                    password_hash,
                    user_name
                )
                VALUES (?, ?, ?)
                RETURNING user_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                phoneNumber,
                passwordHash,
                userName
        );
    }

    public void updateLastLoginTime(Long userId) {
        String sql = """
                UPDATE app_users
                SET last_login_time = CURRENT_TIMESTAMP
                WHERE user_id = ?
                """;

        jdbcTemplate.update(sql, userId);
    }
}