package com.jimmy.etfquant.service;

import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class QuantService {

    private final RestClient restClient;

    public QuantService(
            RestClient.Builder restClientBuilder,
            @Value("${quant.service.base-url}") String quantServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(quantServiceBaseUrl)
                .build();
    }

    public MovingAverageBacktestResponse runMovingAverageBacktest(
            MovingAverageBacktestRequest request
    ) {
        return restClient.post()
                .uri("/backtest/moving-average")
                .body(request)
                .retrieve()
                .body(MovingAverageBacktestResponse.class);
    }
}