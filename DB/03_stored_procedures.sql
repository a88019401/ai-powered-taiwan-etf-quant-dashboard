CREATE OR REPLACE FUNCTION sp_get_all_etfs()
RETURNS TABLE (
    symbol VARCHAR,
    name VARCHAR,
    category VARCHAR,
    description TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT e.symbol, e.name, e.category, e.description
    FROM etfs e
    ORDER BY e.symbol;
END;
$$;


CREATE OR REPLACE FUNCTION sp_get_etf_price_history(p_symbol VARCHAR)
RETURNS TABLE (
    trade_date DATE,
    open_price NUMERIC,
    high_price NUMERIC,
    low_price NUMERIC,
    close_price NUMERIC,
    volume BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.trade_date,
        p.open_price,
        p.high_price,
        p.low_price,
        p.close_price,
        p.volume
    FROM etf_prices p
    WHERE p.symbol = p_symbol
    ORDER BY p.trade_date;
END;
$$;