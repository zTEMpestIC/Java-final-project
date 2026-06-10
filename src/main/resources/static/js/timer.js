/**
 * timer.js
 * 前端計時器：負責 UI 即時倒數與按鈕互動。
 * 後端 Java 負責儲存最後的 startTime / endTime / durationSeconds。
 */

const TimerUI = {
  state: "IDLE",
  mode: "countdown",
  startedAt: null,
  totalSeconds: 50 * 60,
  remainingSeconds: 50 * 60,
  elapsedSeconds: 0,
  intervalId: null,
  isBreak: false,

  init() {
    this.cacheElements();
    this.setupRing();
    this.bindEvents();
    this.reset();
  },

  cacheElements() {
    this.modeSelect = document.getElementById("timerMode");
    this.focusInput = document.getElementById("focusMinutes");
    this.breakInput = document.getElementById("breakMinutes");
    this.subjectInput = document.getElementById("subjectInput");
    this.timerText = document.getElementById("timerText");
    this.timerLabel = document.getElementById("timerLabel");
    this.timerSubText = document.getElementById("timerSubText");
    this.ring = document.getElementById("ringProgress");
    this.startBtn = document.getElementById("startTimerBtn");
    this.pauseBtn = document.getElementById("pauseTimerBtn");
    this.resetBtn = document.getElementById("resetTimerBtn");
    this.finishBtn = document.getElementById("finishTimerBtn");
  },

  setupRing() {
    const radius = 112;
    this.circumference = 2 * Math.PI * radius;
    this.ring.style.strokeDasharray = `${this.circumference}`;
  },

  bindEvents() {
    this.startBtn.addEventListener("click", () => this.startOrResume());
    this.pauseBtn.addEventListener("click", () => this.pause());
    this.resetBtn.addEventListener("click", () => this.reset());
    this.finishBtn.addEventListener("click", () => this.finishAndSave());

    this.modeSelect.addEventListener("change", () => this.reset());
    this.focusInput.addEventListener("change", () => this.reset());
    this.breakInput.addEventListener("change", () => this.reset());
  },

  getFocusSeconds() {
    return Math.max(1, Number(this.focusInput.value || 50)) * 60;
  },

  getBreakSeconds() {
    return Math.max(1, Number(this.breakInput.value || 10)) * 60;
  },

  startOrResume() {
    if (this.state === "RUNNING") return;

    this.mode = this.modeSelect.value;

    if (this.state === "IDLE") {
      this.startedAt = new Date();
      this.isBreak = false;

      if (this.mode === "countup") {
        this.elapsedSeconds = 0;
        this.totalSeconds = 0;
      } else {
        this.totalSeconds = this.getFocusSeconds();
        this.remainingSeconds = this.totalSeconds;
      }
    }

    this.state = "RUNNING";
    this.timerLabel.textContent = this.isBreak ? "休息中" : "專注中";
    this.startBtn.textContent = "繼續";
    this.timerSubText.textContent = this.isBreak ? "讓大腦休息一下" : `正在讀：${this.subjectInput.value || "未命名科目"}`;

    this.intervalId = setInterval(() => this.tick(), 1000);
  },

  pause() {
    if (this.state !== "RUNNING") return;

    clearInterval(this.intervalId);
    this.intervalId = null;
    this.state = "PAUSED";
    this.timerLabel.textContent = "已暫停";
    this.timerSubText.textContent = "按下繼續回到讀書狀態";
  },

  reset() {
    clearInterval(this.intervalId);
    this.intervalId = null;
    this.state = "IDLE";
    this.mode = this.modeSelect.value;
    this.startedAt = null;
    this.elapsedSeconds = 0;
    this.isBreak = false;

    if (this.mode === "countup") {
      this.totalSeconds = 0;
      this.remainingSeconds = 0;
      this.timerText.textContent = "00:00";
      this.updateProgress(0);
    } else {
      this.totalSeconds = this.getFocusSeconds();
      this.remainingSeconds = this.totalSeconds;
      this.timerText.textContent = this.formatTime(this.remainingSeconds);
      this.updateProgress(1);
    }

    this.timerLabel.textContent = "準備開始";
    this.timerSubText.textContent = "選擇科目後開始讀書";
    this.startBtn.textContent = "開始";
  },

  tick() {
    if (this.mode === "countup") {
      this.elapsedSeconds += 1;
      this.timerText.textContent = this.formatTime(this.elapsedSeconds);
      this.updateProgress((this.elapsedSeconds % 3600) / 3600);
      return;
    }

    this.remainingSeconds -= 1;
    this.elapsedSeconds += 1;

    this.timerText.textContent = this.formatTime(Math.max(0, this.remainingSeconds));
    this.updateProgress(this.remainingSeconds / this.totalSeconds);

    if (this.remainingSeconds <= 0) {
      this.handleCountdownFinished();
    }
  },

  handleCountdownFinished() {
    clearInterval(this.intervalId);
    this.intervalId = null;

    if (this.mode === "pomodoro" && !this.isBreak) {
      this.isBreak = true;
      this.totalSeconds = this.getBreakSeconds();
      this.remainingSeconds = this.totalSeconds;
      this.timerText.textContent = this.formatTime(this.remainingSeconds);
      this.timerLabel.textContent = "進入休息";
      this.timerSubText.textContent = "番茄鐘休息時間開始";
      this.state = "PAUSED";
      this.startOrResume();
      return;
    }

    this.state = "FINISHED";
    this.timerLabel.textContent = "完成";
    this.timerSubText.textContent = "可以按結束並儲存紀錄";
  },

  async finishAndSave() {
    if (!this.startedAt && this.elapsedSeconds <= 0) {
      alert("還沒有開始讀書，無法儲存。");
      return;
    }

    clearInterval(this.intervalId);
    this.intervalId = null;

    const now = new Date();
    const durationSeconds = this.mode === "countup"
      ? this.elapsedSeconds
      : Math.max(this.elapsedSeconds, 0);

    if (durationSeconds <= 0) {
      alert("讀書時間太短，沒有儲存。");
      return;
    }

    await FlowStudyAPI.saveFocusLog({
      subject: this.subjectInput.value || "未命名科目",
      startTime: this.startedAt ? this.startedAt.toISOString() : new Date(now.getTime() - durationSeconds * 1000).toISOString(),
      endTime: now.toISOString(),
      durationSeconds
    });

    await refreshTodayFocusText();
    alert("讀書紀錄已儲存！");
    // 更新熱點圖與統計圖
    if (document.getElementById('loadStatsBtn')) {
        document.getElementById('loadStatsBtn').click(); 
    }

    this.reset();
  },

  updateProgress(ratio) {
    const safeRatio = Math.max(0, Math.min(1, ratio));
    const offset = this.circumference * (1 - safeRatio);
    this.ring.style.strokeDashoffset = `${offset}`;
  },

  formatTime(totalSeconds) {
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
  }
};

document.addEventListener("DOMContentLoaded", () => TimerUI.init());
