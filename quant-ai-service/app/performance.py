import numpy as np
import pandas as pd


TRADING_DAYS_PER_YEAR = 252


def calculate_performance(df: pd.DataFrame) -> dict:
    if df.empty or "strategy_return" not in df.columns:
        raise ValueError("Backtest result is empty or missing strategy_return.")

    returns = df["strategy_return"].fillna(0)

    equity_curve = (1 + returns).cumprod()
    total_return = equity_curve.iloc[-1] - 1

    annualized_return = (1 + total_return) ** (TRADING_DAYS_PER_YEAR / len(df)) - 1
    annualized_volatility = returns.std() * np.sqrt(TRADING_DAYS_PER_YEAR)

    if annualized_volatility == 0:
        sharpe_ratio = 0
    else:
        sharpe_ratio = annualized_return / annualized_volatility

    running_max = equity_curve.cummax()
    drawdown = equity_curve / running_max - 1
    max_drawdown = drawdown.min()

    df["portfolio_value"] = equity_curve
    df["drawdown"] = drawdown

    return {
        "total_return": round(float(total_return), 6),
        "annualized_return": round(float(annualized_return), 6),
        "annualized_volatility": round(float(annualized_volatility), 6),
        "sharpe_ratio": round(float(sharpe_ratio), 6),
        "max_drawdown": round(float(max_drawdown), 6),
    }