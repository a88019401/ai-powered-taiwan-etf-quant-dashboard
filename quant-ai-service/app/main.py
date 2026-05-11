from fastapi import FastAPI, HTTPException

from app.ai_report import generate_ai_summaries
from app.data_loader import download_price_data
from app.schemas import MovingAverageBacktestRequest, MovingAverageBacktestResponse
from app.strategies import run_moving_average_strategy

app = FastAPI(title="AI-Powered Taiwan ETF Quant Service")


@app.get("/")
def root():
    return {
        "message": "AI-Powered Taiwan ETF Quant Service is running."
    }


@app.post("/backtest/moving-average", response_model=MovingAverageBacktestResponse)
def moving_average_backtest(request: MovingAverageBacktestRequest):
    try:
        price_df = download_price_data(
            symbol=request.symbol,
            start_date=request.start_date,
            end_date=request.end_date,
        )

        metrics, result_df = run_moving_average_strategy(
            price_df=price_df,
            short_window=request.short_window,
            long_window=request.long_window,
            transaction_cost=request.transaction_cost,
        )
        ai_summaries = generate_ai_summaries(
            symbol=request.symbol,
            metrics=metrics,
            request_context={
                "start_date": request.start_date,
                "end_date": request.end_date,
                "short_window": request.short_window,
                "long_window": request.long_window,
                "transaction_cost": request.transaction_cost,
            },
        )

        ai_summary_zh = ai_summaries["ai_summary_zh"]
        ai_summary_en = ai_summaries["ai_summary_en"]

        equity_curve = result_df[
            ["date", "portfolio_value", "drawdown"]
        ].dropna().tail(300)

        equity_curve_records = [
            {
                "date": row["date"].strftime("%Y-%m-%d"),
                "portfolio_value": round(float(row["portfolio_value"]), 6),
                "drawdown": round(float(row["drawdown"]), 6),
            }
            for _, row in equity_curve.iterrows()
        ]

        return MovingAverageBacktestResponse(
            symbol=request.symbol,
            strategy="moving_average",
            total_return=metrics["total_return"],
            annualized_return=metrics["annualized_return"],
            annualized_volatility=metrics["annualized_volatility"],
            sharpe_ratio=metrics["sharpe_ratio"],
            max_drawdown=metrics["max_drawdown"],
            number_of_trades=metrics["number_of_trades"],
            ai_provider=ai_summaries["ai_provider"],
            ai_summary_zh=ai_summary_zh,
            ai_summary_en=ai_summary_en,
            equity_curve=equity_curve_records,
        )
    except Exception as error:
        raise HTTPException(status_code=400, detail=str(error))