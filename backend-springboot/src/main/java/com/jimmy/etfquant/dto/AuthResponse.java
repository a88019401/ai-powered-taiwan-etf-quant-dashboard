package com.jimmy.etfquant.dto;

public record AuthResponse(
        Long user_id,
        String phone_number,
        String user_name,
        String token
) {
}