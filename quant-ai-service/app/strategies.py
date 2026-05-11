import pandas as pd

from app.performance import calculate_performance


def run_moving_average_strategy(
    price_df: pd.DataFrame,
    short_window: int,
    long_window: int,
    transaction_cost: float,
) -> tuple[dict, pd.DataFrame]:
    if short_window >= long_window:
        raise ValueError("short_window must be smaller than long_window.")

    df = price_df.copy()

    df["daily_return"] = df["close"].pct_change().fillna(0)
    df["short_ma"] = df["close"].rolling(window=short_window).mean()
    df["long_ma"] = df["close"].rolling(window=long_window).mean()

    # position = 1 means holding ETF, position = 0 means cash.
    df["position"] = 0
    df.loc[df["short_ma"] > df["long_ma"], "position"] = 1

    # Use yesterday's signal to avoid look-ahead bias.
    df["position"] = df["position"].shift(1).fillna(0)

    df["trade"] = df["position"].diff().abs().fillna(0)
    df["cost"] = df["trade"] * transaction_cost

    df["strategy_return"] = df["position"] * df["daily_return"] - df["cost"]

    number_of_trades = int(df["trade"].sum())

    metrics = calculate_performance(df)
    metrics["number_of_trades"] = number_of_trades

    return metrics, df