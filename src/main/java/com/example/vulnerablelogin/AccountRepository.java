package com.example.vulnerablelogin;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Account> findAll() {
        return jdbcTemplate.query("SELECT id, username, password, email, role FROM accounts ORDER BY id", this::mapAccount);
    }

    public Account findById(long id) {
        String sql = "SELECT id, username, password, email, role FROM accounts WHERE id = " + id;
        return jdbcTemplate.query(sql, this::mapAccount).stream().findFirst().orElse(null);
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM accounts", Integer.class);
        return count == null ? 0 : count;
    }

    public void create(AccountRequest request) {
        String role = request.role() == null || request.role().isBlank() ? "USER" : request.role();
        String sql = "INSERT INTO accounts (username, password, email, role) VALUES ('"
                + value(request.username()) + "', '"
                + value(request.password()) + "', '"
                + value(request.email()) + "', '"
                + value(role) + "')";
        jdbcTemplate.execute(sql);
    }

    public Account login(String username, String password) {
        String sql = "SELECT id, username, password, email, role FROM accounts WHERE username = '"
                + value(username) + "' AND password = '" + value(password) + "'";
        return jdbcTemplate.query(sql, this::mapAccount).stream().findFirst().orElse(null);
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM accounts WHERE id = " + id);
    }

    public int deleteByUsername(String username) {
        String sql = "DELETE FROM accounts WHERE username = '" + value(username) + "'";
        return jdbcTemplate.update(sql);
    }

    private Account mapAccount(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Account(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("role"));
    }

    private String value(String input) {
        return input == null ? "" : input;
    }
}
