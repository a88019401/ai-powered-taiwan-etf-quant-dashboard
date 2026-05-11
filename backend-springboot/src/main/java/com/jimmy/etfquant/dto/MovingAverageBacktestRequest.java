package com.jimmy.etfquant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MovingAverageBacktestRequest(
        @NotBlank
        String symbol,

        @NotBlank
        String start_date,

        @NotBlank
        String end_date,

        @Min(1)
        int short_window,

        @Min(2)
        int long_window,

        @DecimalMin("0.0")
        double transaction_cost
) {
}