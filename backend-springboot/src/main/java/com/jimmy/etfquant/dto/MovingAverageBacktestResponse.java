package com.jimmy.etfquant.dto;

import java.util.List;

public record MovingAverageBacktestResponse(
        Long run_id,
        String symbol,
        String strategy,
        double total_return,
        double annualized_return,
        double annualized_volatility,
        double sharpe_ratio,
        double max_drawdown,
        int number_of_trades,
        String ai_summary_zh,
        String ai_summary_en,
        List<EquityCurvePoint> equity_curve
) {
    public MovingAverageBacktestResponse withRunId(Long runId) {
        return new MovingAverageBacktestResponse(
                runId,
                symbol,
                strategy,
                total_return,
                annualized_return,
                annualized_volatility,
                sharpe_ratio,
                max_drawdown,
                number_of_trades,
                ai_summary_zh,
                ai_summary_en,
                equity_curve
        );
    }
}