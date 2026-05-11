package com.jimmy.etfquant.controller;

import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import com.jimmy.etfquant.service.QuantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtests")
public class BacktestController {

    private final QuantService quantService;

    public BacktestController(QuantService quantService) {
        this.quantService = quantService;
    }

    @PostMapping("/moving-average")
    public MovingAverageBacktestResponse runMovingAverageBacktest(
            @Valid @RequestBody MovingAverageBacktestRequest request
    ) {
        return quantService.runMovingAverageBacktest(request);
    }
}