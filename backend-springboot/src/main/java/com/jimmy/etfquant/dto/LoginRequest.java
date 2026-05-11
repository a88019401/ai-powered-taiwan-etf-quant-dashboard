package com.jimmy.etfquant.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String phone_number,
        @NotBlank String password
) {
}