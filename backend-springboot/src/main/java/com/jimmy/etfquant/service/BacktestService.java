package com.jimmy.etfquant.service;

import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import org.springframework.stereotype.Service;

@Service
public class BacktestService {

    private final QuantService quantService;
    private final BacktestPersistenceService persistenceService;

    public BacktestService(
            QuantService quantService,
            BacktestPersistenceService persistenceService
    ) {
        this.quantService = quantService;
        this.persistenceService = persistenceService;
    }

    public MovingAverageBacktestResponse runAndSaveMovingAverageBacktest(
            MovingAverageBacktestRequest request
    ) {
        MovingAverageBacktestResponse response =
                quantService.runMovingAverageBacktest(request);

        Long runId = persistenceService.saveMovingAverageBacktest(request, response);

        return response.withRunId(runId);
    }
}