package com.jimmy.etfquant.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String phone_number,
        @NotBlank String password,
        @NotBlank String user_name,
        @NotBlank String registration_code
) {
}