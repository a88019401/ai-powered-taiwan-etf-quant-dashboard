package com.jimmy.etfquant.service;

import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import com.jimmy.etfquant.repository.BacktestPersistenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BacktestPersistenceService {

    private final BacktestPersistenceRepository repository;

    public BacktestPersistenceService(BacktestPersistenceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Long saveMovingAverageBacktest(
            MovingAverageBacktestRequest request,
            MovingAverageBacktestResponse response
    ) {
        Long runId = repository.saveBacktestRun(request);

        repository.saveBacktestMetrics(runId, response);

        repository.saveAiReport(runId, "zh", response.ai_summary_zh());
        repository.saveAiReport(runId, "en", response.ai_summary_en());

        repository.saveEquityCurve(runId, response.equity_curve());

        return runId;
    }
}