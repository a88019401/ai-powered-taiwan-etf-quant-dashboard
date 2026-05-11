INSERT INTO etfs (symbol, name, category, description)
VALUES
('0050.TW', '元大台灣50', 'Market Cap Weighted ETF', 'Tracks the performance of the Taiwan 50 Index.'),
('0056.TW', '元大高股息', 'High Dividend ETF', 'Focuses on high dividend stocks in Taiwan.'),
('00878.TW', '國泰永續高股息', 'ESG High Dividend ETF', 'Combines ESG and high dividend investment themes.'),
('006208.TW', '富邦台50', 'Market Cap Weighted ETF', 'Tracks the Taiwan 50 Index with a similar exposure to 0050.'),
('00713.TW', '元大台灣高息低波', 'Low Volatility High Dividend ETF', 'Focuses on high dividend and low volatility Taiwan stocks.')
ON CONFLICT (symbol) DO NOTHING;