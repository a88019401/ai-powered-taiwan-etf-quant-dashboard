package com.jimmy.etfquant.repository;

import com.jimmy.etfquant.dto.EquityCurvePoint;
import com.jimmy.etfquant.dto.MovingAverageBacktestRequest;
import com.jimmy.etfquant.dto.MovingAverageBacktestResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class BacktestPersistenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public BacktestPersistenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long saveBacktestRun(MovingAverageBacktestRequest request) {
        String sql = """
                INSERT INTO backtest_runs (
                    user_id,
                    strategy_type,
                    symbol,
                    start_date,
                    end_date,
                    transaction_cost
                )
                VALUES (
                    NULL,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                RETURNING run_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                "moving_average",
                request.symbol(),
                LocalDate.parse(request.start_date()),
                LocalDate.parse(request.end_date()),
                BigDecimal.valueOf(request.transaction_cost())
        );
    }

    public void saveBacktestMetrics(Long runId, MovingAverageBacktestResponse response) {
        String sql = """
                INSERT INTO backtest_metrics (
                    run_id,
                    total_return,
                    annualized_return,
                    annualized_volatility,
                    sharpe_ratio,
                    max_drawdown,
                    number_of_trades
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                runId,
                BigDecimal.valueOf(response.total_return()),
                BigDecimal.valueOf(response.annualized_return()),
                BigDecimal.valueOf(response.annualized_volatility()),
                BigDecimal.valueOf(response.sharpe_ratio()),
                BigDecimal.valueOf(response.max_drawdown()),
                response.number_of_trades()
        );
    }

    public void saveAiReport(Long runId, String language, String content) {
        String sql = """
                INSERT INTO ai_reports (
                    run_id,
                    language,
                    report_content
                )
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, runId, language, content);
    }

    public void saveEquityCurve(Long runId, List<EquityCurvePoint> equityCurve) {
        String sql = """
                INSERT INTO backtest_equity_curve (
                    run_id,
                    trade_date,
                    portfolio_value,
                    drawdown
                )
                VALUES (?, ?, ?, ?)
                """;

        List<Object[]> batchArgs = equityCurve.stream()
                .map(point -> new Object[]{
                        runId,
                        LocalDate.parse(point.date()),
                        BigDecimal.valueOf(point.portfolio_value()),
                        BigDecimal.valueOf(point.drawdown())
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}