from pydantic import BaseModel, Field


class MovingAverageBacktestRequest(BaseModel):
    symbol: str = Field(..., examples=["0050.TW"])
    start_date: str = Field(..., examples=["2020-01-01"])
    end_date: str = Field(..., examples=["2025-12-31"])
    short_window: int = Field(20, ge=1)
    long_window: int = Field(60, ge=2)
    transaction_cost: float = Field(0.001425, ge=0)


class MovingAverageBacktestResponse(BaseModel):
    symbol: str
    strategy: str
    total_return: float
    annualized_return: float
    annualized_volatility: float
    sharpe_ratio: float
    max_drawdown: float
    number_of_trades: int
    ai_summary_zh: str
    ai_summary_en: str
    equity_curve: list[dict]