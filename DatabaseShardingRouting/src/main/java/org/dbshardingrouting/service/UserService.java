package org.dbshardingrouting.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveUser(String username, String email) {
        String sql = "INSERT INTO user (username, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, email);
    }
}
