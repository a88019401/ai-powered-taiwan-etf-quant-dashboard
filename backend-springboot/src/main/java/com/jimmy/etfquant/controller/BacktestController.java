package com.jimmy.etfquant.controller;

import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import com.jimmy.etfquant.service.BacktestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtests")
public class BacktestController {

    private final BacktestService backtestService;

    public BacktestController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }

    @PostMapping("/moving-average")
    public MovingAverageBacktestResponse runMovingAverageBacktest(
            @Valid @RequestBody MovingAverageBacktestRequest request
    ) {
        return backtestService.runAndSaveMovingAverageBacktest(request);
    }
}