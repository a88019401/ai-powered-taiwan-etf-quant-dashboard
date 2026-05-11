import pandas as pd
import yfinance as yf


def download_price_data(symbol: str, start_date: str, end_date: str) -> pd.DataFrame:
    df = yf.download(
        symbol,
        start=start_date,
        end=end_date,
        auto_adjust=True,
        progress=False,
    )

    if df.empty:
        raise ValueError(f"No price data found for symbol: {symbol}")

    df = df.reset_index()

    # yfinance may return multi-index columns in some cases.
    if isinstance(df.columns, pd.MultiIndex):
        df.columns = [col[0] for col in df.columns]

    df = df.rename(
        columns={
            "Date": "date",
            "Open": "open",
            "High": "high",
            "Low": "low",
            "Close": "close",
            "Volume": "volume",
        }
    )

    required_columns = ["date", "open", "high", "low", "close", "volume"]
    df = df[required_columns].dropna()

    return df