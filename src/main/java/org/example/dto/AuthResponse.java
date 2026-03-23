package org.example.dto;

public record AuthResponse(String token, String username, Long userId) {
}
