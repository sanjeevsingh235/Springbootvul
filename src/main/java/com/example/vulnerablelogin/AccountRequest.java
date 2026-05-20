package com.example.vulnerablelogin;

public record AccountRequest(String username, String password, String email, String role) {
}
