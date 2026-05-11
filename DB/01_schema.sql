CREATE TABLE IF NOT EXISTS app_users (
    user_id BIGSERIAL PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    registration_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS etfs (
    symbol VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    description TEXT
);

CREATE TABLE IF NOT EXISTS etf_prices (
    price_id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    trade_date DATE NOT NULL,
    open_price NUMERIC(18, 4),
    high_price NUMERIC(18, 4),
    low_price NUMERIC(18, 4),
    close_price NUMERIC(18, 4),
    volume BIGINT,
    CONSTRAINT fk_etf_prices_symbol
        FOREIGN KEY (symbol)
        REFERENCES etfs(symbol),
    CONSTRAINT uq_etf_price_symbol_date
        UNIQUE (symbol, trade_date)
);

CREATE TABLE IF NOT EXISTS backtest_runs (
    run_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    strategy_type VARCHAR(50) NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    transaction_cost NUMERIC(10, 6) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_backtest_user
        FOREIGN KEY (user_id)
        REFERENCES app_users(user_id),
    CONSTRAINT fk_backtest_etf
        FOREIGN KEY (symbol)
        REFERENCES etfs(symbol)
);

CREATE TABLE IF NOT EXISTS backtest_metrics (
    metric_id BIGSERIAL PRIMARY KEY,
    run_id BIGINT NOT NULL,
    total_return NUMERIC(18, 6),
    annualized_return NUMERIC(18, 6),
    annualized_volatility NUMERIC(18, 6),
    sharpe_ratio NUMERIC(18, 6),
    max_drawdown NUMERIC(18, 6),
    number_of_trades INTEGER,
    CONSTRAINT fk_metrics_run
        FOREIGN KEY (run_id)
        REFERENCES backtest_runs(run_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS backtest_equity_curve (
    curve_id BIGSERIAL PRIMARY KEY,
    run_id BIGINT NOT NULL,
    trade_date DATE NOT NULL,
    portfolio_value NUMERIC(18, 6) NOT NULL,
    drawdown NUMERIC(18, 6),
    CONSTRAINT fk_curve_run
        FOREIGN KEY (run_id)
        REFERENCES backtest_runs(run_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ai_reports (
    report_id BIGSERIAL PRIMARY KEY,
    run_id BIGINT NOT NULL,
    language VARCHAR(10) NOT NULL,
    report_content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_report_run
        FOREIGN KEY (run_id)
        REFERENCES backtest_runs(run_id)
        ON DELETE CASCADE
);