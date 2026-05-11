package com.jimmy.etfquant.dto;

import java.util.List;

public record MovingAverageBacktestResponse(
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
}