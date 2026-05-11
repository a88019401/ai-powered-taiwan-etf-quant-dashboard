package com.jimmy.etfquant.dto;

public record EtfResponse(
        String symbol,
        String name,
        String category,
        String description
) {
}