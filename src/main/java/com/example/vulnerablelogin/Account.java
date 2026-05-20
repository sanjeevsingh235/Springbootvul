package com.example.vulnerablelogin;

public record Account(Long id, String username, String password, String email, String role) {
}
