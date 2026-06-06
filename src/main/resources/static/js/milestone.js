/**
 * milestone.js
 * 前端簡化版里程碑推算。
 * 正式演算法可由 Java MilestoneScheduler.java 完成，前端只負責顯示結果。
 */

const MilestoneUI = {
  init() {
    this.form = document.getElementById("milestoneForm");
    this.goalNameInput = document.getElementById("goalNameInput");
    this.totalProgressInput = document.getElementById("totalProgressInput");
    this.deadlineInput = document.getElementById("deadlineInput");
    this.result = document.getElementById("milestoneResult");

    const defaultDeadline = new Date();
    defaultDeadline.setDate(defaultDeadline.getDate() + 7);
    this.deadlineInput.value = defaultDeadline.toISOString().slice(0, 10);

    this.form.addEventListener("submit", event => {
      event.preventDefault();
      this.calculate();
    });
  },

  calculate() {
    const goalName = this.goalNameInput.value.trim() || "未命名目標";
    const totalProgress = Math.max(1, Number(this.totalProgressInput.value || 1));
    const today = new Date();
    const deadline = new Date(this.deadlineInput.value);

    const diffMs = deadline.setHours(23, 59, 59, 999) - today.getTime();
    const remainingDays = Math.max(1, Math.ceil(diffMs / (1000 * 60 * 60 * 24)));
    const dailyProgress = Math.ceil(totalProgress / remainingDays);

    this.result.innerHTML = `
      <div class="result-row">
        <span>目標</span>
        <strong>${goalName}</strong>
      </div>
      <div class="result-row">
        <span>剩餘天數</span>
        <strong>${remainingDays} 天</strong>
      </div>
      <div class="result-row">
        <span>每日最低進度</span>
        <strong>${dailyProgress} 單位 / 天</strong>
      </div>
      <p class="hint">目前是前端簡化版。正式版可由 Java MilestoneScheduler 根據歷史效率與落後情況動態調整。</p>
    `;
  }
};

document.addEventListener("DOMContentLoaded", () => MilestoneUI.init());
