/**
 * stats.js
 * 讀書熱點圖與科目圓餅圖。
 * 圓餅圖使用 Chart.js；熱點圖用原生 HTML/CSS 繪製。
 */

const StatisticsUI = {
  chart: null,

  init() {
    this.heatmap = document.getElementById("heatmap");
    this.chartCanvas = document.getElementById("subjectPieChart");
    document.getElementById("loadStatsBtn").addEventListener("click", () => this.load());
    this.load();
  },

  async load() {
    const stats = await FlowStudyAPI.getStats();
    this.renderHeatmap(stats.heatmap || []);
    this.renderPieChart(stats.subjectRatio || []);
  },

  renderHeatmap(days) {
    this.heatmap.innerHTML = "";

    days.forEach(day => {
      const cell = document.createElement("div");
      cell.className = `heat-cell level-${this.getLevel(day.minutes)}`;
      cell.title = `${day.date}: ${day.minutes} 分鐘`;
      this.heatmap.appendChild(cell);
    });
  },

  getLevel(minutes) {
    if (minutes <= 0) return 0;
    if (minutes < 60) return 1;
    if (minutes < 180) return 2;
    if (minutes < 300) return 3;
    return 4;
  },

  renderPieChart(subjectRatio) {
    const labels = subjectRatio.length ? subjectRatio.map(item => item.subject) : ["尚無資料"];
    const data = subjectRatio.length ? subjectRatio.map(item => item.minutes) : [1];

    if (this.chart) this.chart.destroy();

    this.chart = new Chart(this.chartCanvas, {
      type: "doughnut",
      data: {
        labels,
        datasets: [{
          data,
          borderWidth: 0
        }]
      },
      options: {
        plugins: {
          legend: {
            labels: {
              color: "#f8fafc"
            }
          }
        }
      }
    });
  }
};

document.addEventListener("DOMContentLoaded", () => StatisticsUI.init());
