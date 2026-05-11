package com.jimmy.etfquant.dto;

public record EquityCurvePoint(
        String date,
        double portfolio_value,
        double drawdown
) {
}