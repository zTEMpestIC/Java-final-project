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
    
    // 🌟 核心修正：將後端的稀疏資料填補成完整的 181 天
    const completeHeatmapData = this.fillMissingDays(stats.heatmap || []);
    
    this.renderHeatmap(completeHeatmapData);
    this.renderPieChart(stats.subjectRatio || []);
  },

  // 🌟 演算法：生成「今年一整年」的日曆 (Jan 1 ~ Dec 31)
  fillMissingDays(backendData) {
    const dataMap = {};
    backendData.forEach(item => {
      dataMap[item.date] = item.minutes;
    });

    const filled = [];
    const currentYear = new Date().getFullYear();
    const firstDay = new Date(currentYear, 0, 1); // 今年 1月 1日
    const lastDay = new Date(currentYear, 11, 31); // 今年 12月 31日

    // 關鍵：退回到 1月 1日所在那週的「星期日」，確保網格 7 天對齊
    const startDayOfWeek = firstDay.getDay(); 
    const startDate = new Date(currentYear, 0, 1 - startDayOfWeek);

    let currentDate = new Date(startDate);
    
    // 迴圈跑到今年的最後一天為止
    while (currentDate <= lastDay) {
      const y = currentDate.getFullYear();
      const m = String(currentDate.getMonth() + 1).padStart(2, '0');
      const d = String(currentDate.getDate()).padStart(2, '0');
      const dateText = `${y}-${m}-${d}`;
      
      filled.push({
        date: dateText,
        minutes: dataMap[dateText] || 0,
        isCurrentYear: y === currentYear // 標記這天是不是真的屬於今年
      });
      currentDate.setDate(currentDate.getDate() + 1);
    }
    return filled;
  },
  // 🌟 渲染：處理隱藏格子與月份標籤
  renderHeatmap(days) {
    this.heatmap.innerHTML = "";

    const container = document.createElement("div");
    container.className = "heatmap-container";

    // --- 1. 建立頂部月份標籤 ---
    const header = document.createElement("div");
    header.className = "heatmap-header";
    const monthsDiv = document.createElement("div");
    monthsDiv.className = "heatmap-months";

    const numCols = Math.ceil(days.length / 7);
    let lastMonth = -1;

    for (let col = 0; col < numCols; col++) {
      const span = document.createElement("span");
      let label = "";
      
      for (let row = 0; row < 7; row++) {
        const dayIndex = col * 7 + row;
        // 只針對「今年」的天數來判定月份
        if (dayIndex < days.length && days[dayIndex].isCurrentYear) {
          const [y, m, d] = days[dayIndex].date.split("-");
          const dateObj = new Date(y, m - 1, d);
          const month = dateObj.getMonth();
          
          if (month !== lastMonth) {
            label = dateObj.toLocaleString('en-US', { month: 'short' });
            lastMonth = month;
          }
        }
      }
      span.textContent = label;
      monthsDiv.appendChild(span);
    }
    header.appendChild(monthsDiv);

    // --- 2. 建立主體 (星期 + 網格) ---
    const body = document.createElement("div");
    body.className = "heatmap-body";

    const weekdays = document.createElement("div");
    weekdays.className = "heatmap-weekdays";
    weekdays.innerHTML = `
      <span></span><span>Mon</span><span></span><span>Wed</span><span></span><span>Fri</span><span></span>
    `;

    const grid = document.createElement("div");
    grid.className = "heatmap-grid";

    days.forEach(day => {
      const cell = document.createElement("div");
      
      // 如果是為了對齊而補的去年年底天數，設為隱藏
      if (!day.isCurrentYear) {
        cell.className = "heat-cell hidden-cell";
      } else {
        cell.className = `heat-cell level-${this.getLevel(day.minutes)}`;
        cell.title = `${day.date}: 專注 ${day.minutes} 分鐘`;
      }
      grid.appendChild(cell);
    });

    body.appendChild(weekdays);
    body.appendChild(grid);

    // --- 3. 建立底部圖例 ---
    const footer = document.createElement("div");
    footer.className = "heatmap-footer";
    footer.innerHTML = `
      <span class="heatmap-hint">FlowStudy Focus Contributions (${new Date().getFullYear()})</span>
      <div class="heatmap-legend">
        <span>Less</span>
        <div class="heat-cell level-0"></div>
        <div class="heat-cell level-1"></div>
        <div class="heat-cell level-2"></div>
        <div class="heat-cell level-3"></div>
        <div class="heat-cell level-4"></div>
        <span>More</span>
      </div>
    `;

    container.appendChild(header);
    container.appendChild(body);
    container.appendChild(footer);
    this.heatmap.appendChild(container);
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
          borderWidth: 0,
          backgroundColor: [
            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
          ] // 給圓餅圖加上漂亮顏色的 fallback
        }]
      },
      options: {
        plugins: {
          legend: {
            labels: {
              color: "#f8fafc" // "#333"依照你的版面調整顏色，如果是深色模式可以改回 "#f8fafc"
            }
          }
        }
      }
    });
  }
};

document.addEventListener("DOMContentLoaded", () => StatisticsUI.init());