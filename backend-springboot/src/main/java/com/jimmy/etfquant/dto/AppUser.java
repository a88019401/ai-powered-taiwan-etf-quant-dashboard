package com.jimmy.etfquant.dto;

public record AppUser(
        Long user_id,
        String phone_number,
        String password_hash,
        String user_name
) {
}