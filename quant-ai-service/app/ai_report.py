def generate_ai_summary_zh(symbol: str, metrics: dict) -> str:
    total_return = metrics["total_return"]
    annualized_return = metrics["annualized_return"]
    annualized_volatility = metrics["annualized_volatility"]
    sharpe_ratio = metrics["sharpe_ratio"]
    max_drawdown = metrics["max_drawdown"]

    if total_return > 0:
        return_direction = "呈現正報酬"
    else:
        return_direction = "呈現負報酬"

    if max_drawdown < -0.2:
        risk_comment = "最大回撤偏高，代表市場下跌期間的風險較明顯"
    else:
        risk_comment = "最大回撤相對可控，但仍需注意市場波動"

    if sharpe_ratio > 1:
        sharpe_comment = "夏普比率表現良好，代表風險調整後報酬較佳"
    elif sharpe_ratio > 0:
        sharpe_comment = "夏普比率為正，代表策略仍具一定風險補償"
    else:
        sharpe_comment = "夏普比率偏低，代表策略承擔的風險未能有效轉換為報酬"

    return (
        f"本次針對 {symbol} 的均線策略回測結果顯示，策略在測試期間{return_direction}。"
        f"年化報酬率約為 {annualized_return:.2%}，年化波動率約為 {annualized_volatility:.2%}。"
        f"{sharpe_comment}。"
        f"此外，最大回撤約為 {max_drawdown:.2%}，{risk_comment}。"
        f"本分析僅供教育與研究用途，不構成投資建議。"
    )


def generate_ai_summary_en(symbol: str, metrics: dict) -> str:
    total_return = metrics["total_return"]
    annualized_return = metrics["annualized_return"]
    annualized_volatility = metrics["annualized_volatility"]
    sharpe_ratio = metrics["sharpe_ratio"]
    max_drawdown = metrics["max_drawdown"]

    return_direction = "positive returns" if total_return > 0 else "negative returns"

    if max_drawdown < -0.2:
        risk_comment = "the maximum drawdown is relatively high, indicating notable downside risk during market declines"
    else:
        risk_comment = "the maximum drawdown appears relatively controlled, although market volatility should still be considered"

    if sharpe_ratio > 1:
        sharpe_comment = "The Sharpe ratio is strong, suggesting favorable risk-adjusted returns"
    elif sharpe_ratio > 0:
        sharpe_comment = "The Sharpe ratio is positive, suggesting some degree of risk compensation"
    else:
        sharpe_comment = "The Sharpe ratio is weak, suggesting that the strategy did not effectively convert risk into returns"

    return (
        f"The moving average backtest for {symbol} generated {return_direction} during the testing period. "
        f"The annualized return was approximately {annualized_return:.2%}, while annualized volatility was approximately {annualized_volatility:.2%}. "
        f"{sharpe_comment}. "
        f"The maximum drawdown was approximately {max_drawdown:.2%}; {risk_comment}. "
        f"This analysis is for educational and research purposes only and does not constitute investment advice."
    )