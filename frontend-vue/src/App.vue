<script setup>
import { computed, onMounted, ref } from "vue";
import * as echarts from "echarts";
import http from "./api/http";

const etfs = ref([]);
const selectedSymbol = ref("0050.TW");
const startDate = ref("2020-01-01");
const endDate = ref("2025-12-31");
const shortWindow = ref(20);
const longWindow = ref(60);
const transactionCost = ref(0.001425);

const loading = ref(false);
const errorMessage = ref("");
const result = ref(null);

let equityChart = null;
let drawdownChart = null;

const metrics = computed(() => {
  if (!result.value) return [];

  return [
    {
      label: "Total Return",
      value: formatPercent(result.value.total_return),
    },
    {
      label: "Annualized Return",
      value: formatPercent(result.value.annualized_return),
    },
    {
      label: "Annualized Volatility",
      value: formatPercent(result.value.annualized_volatility),
    },
    {
      label: "Sharpe Ratio",
      value: result.value.sharpe_ratio?.toFixed(3),
    },
    {
      label: "Max Drawdown",
      value: formatPercent(result.value.max_drawdown),
    },
    {
      label: "Trades",
      value: result.value.number_of_trades,
    },
  ];
});

onMounted(async () => {
  await loadEtfs();
});

async function loadEtfs() {
  try {
    const response = await http.get("/etfs");
    etfs.value = response.data;
    if (response.data.length > 0) {
      selectedSymbol.value = response.data[0].symbol;
    }
  } catch (error) {
    errorMessage.value = "Failed to load ETF list from backend.";
    console.error(error);
  }
}

async function runBacktest() {
  loading.value = true;
  errorMessage.value = "";
  result.value = null;

  try {
    const payload = {
      symbol: selectedSymbol.value,
      start_date: startDate.value,
      end_date: endDate.value,
      short_window: Number(shortWindow.value),
      long_window: Number(longWindow.value),
      transaction_cost: Number(transactionCost.value),
    };

    const response = await http.post("/backtests/moving-average", payload);
    result.value = response.data;

    setTimeout(() => {
      renderCharts();
    }, 100);
  } catch (error) {
    errorMessage.value =
      error.response?.data?.message ||
      error.response?.data?.detail ||
      "Backtest failed. Please check backend and quant service.";
    console.error(error);
  } finally {
    loading.value = false;
  }
}

function renderCharts() {
  if (!result.value || !result.value.equity_curve) return;

  const dates = result.value.equity_curve.map((point) => point.date);
  const portfolioValues = result.value.equity_curve.map(
    (point) => point.portfolio_value,
  );
  const drawdowns = result.value.equity_curve.map((point) => point.drawdown);

  const equityElement = document.getElementById("equity-chart");
  const drawdownElement = document.getElementById("drawdown-chart");

  if (equityChart) equityChart.dispose();
  if (drawdownChart) drawdownChart.dispose();

  equityChart = echarts.init(equityElement);
  drawdownChart = echarts.init(drawdownElement);

  equityChart.setOption({
    title: {
      text: "Equity Curve",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
    },
    xAxis: {
      type: "category",
      data: dates,
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "Portfolio Value",
        type: "line",
        data: portfolioValues,
        smooth: true,
      },
    ],
  });

  drawdownChart.setOption({
    title: {
      text: "Drawdown Curve",
      left: "center",
    },
    tooltip: {
      trigger: "axis",
      valueFormatter: (value) => `${(value * 100).toFixed(2)}%`,
    },
    xAxis: {
      type: "category",
      data: dates,
    },
    yAxis: {
      type: "value",
      axisLabel: {
        formatter: (value) => `${(value * 100).toFixed(0)}%`,
      },
    },
    series: [
      {
        name: "Drawdown",
        type: "line",
        data: drawdowns,
        smooth: true,
      },
    ],
  });

  window.addEventListener("resize", () => {
    equityChart?.resize();
    drawdownChart?.resize();
  });
}

function formatPercent(value) {
  if (value === null || value === undefined) return "-";
  return `${(value * 100).toFixed(2)}%`;
}
</script>

<template>
  <main class="page">
    <section class="hero">
      <p class="eyebrow">AI-Powered Quant Research Dashboard</p>
      <h1>Taiwan ETF Quant Research Dashboard</h1>
      <p class="subtitle">
        A full-stack fintech research system integrating Vue, Spring Boot,
        Supabase, Python FastAPI, ETF backtesting, and AI-generated bilingual
        analysis.
      </p>
    </section>

    <section class="panel">
      <div class="panel-header">
        <div>
          <h2>Backtest Settings</h2>
          <p>
            Configure ETF symbol, date range, moving averages, and transaction
            cost.
          </p>
        </div>
      </div>

      <div class="form-grid">
        <label>
          ETF Symbol
          <select v-model="selectedSymbol">
            <option v-for="etf in etfs" :key="etf.symbol" :value="etf.symbol">
              {{ etf.symbol }} - {{ etf.name }}
            </option>
          </select>
        </label>

        <label>
          Start Date
          <input v-model="startDate" type="date" />
        </label>

        <label>
          End Date
          <input v-model="endDate" type="date" />
        </label>

        <label>
          Short MA
          <input v-model="shortWindow" type="number" min="1" />
        </label>

        <label>
          Long MA
          <input v-model="longWindow" type="number" min="2" />
        </label>

        <label>
          Transaction Cost
          <input
            v-model="transactionCost"
            type="number"
            min="0"
            step="0.000001"
          />
        </label>
      </div>

      <button class="primary-button" :disabled="loading" @click="runBacktest">
        {{ loading ? "Running Backtest..." : "Run Moving Average Backtest" }}
      </button>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>
    </section>

    <section v-if="result" class="panel">
      <div class="panel-header">
        <div>
          <h2>Performance Summary</h2>
          <p>
            Run ID: {{ result.run_id }} ｜ {{ result.symbol }} ｜
            {{ result.strategy }}
          </p>
        </div>
      </div>

      <div class="metric-grid">
        <div v-for="metric in metrics" :key="metric.label" class="metric-card">
          <p>{{ metric.label }}</p>
          <strong>{{ metric.value }}</strong>
        </div>
      </div>
    </section>

    <section v-if="result" class="chart-grid">
      <div class="chart-card">
        <div id="equity-chart" class="chart"></div>
      </div>

      <div class="chart-card">
        <div id="drawdown-chart" class="chart"></div>
      </div>
    </section>

    <section v-if="result" class="panel report-panel">
      <h2>AI Research Summary</h2>
      <p class="ai-provider">AI Provider: {{ result.ai_provider }}</p>
      <div class="report-block">
        <h3>中文摘要</h3>
        <p>{{ result.ai_summary_zh }}</p>
      </div>

      <div class="report-block">
        <h3>English Summary</h3>
        <p>{{ result.ai_summary_en }}</p>
      </div>

      <p class="disclaimer">
        本系統僅供教育與研究用途，不構成投資建議。 This system is for
        educational and research purposes only and does not constitute
        investment advice.
      </p>
    </section>
  </main>
</template>
