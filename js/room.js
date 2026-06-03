/**
 * room.js
 * 自習室 MVP 版本：建立房間、加入房間、顯示成員狀態。
 * 目前使用「刷新」模式，未使用 WebSocket。
 */

const StudyRoomUI = {
  currentRoom: null,

  async init() {
    this.roomNameInput = document.getElementById("roomNameInput");
    this.currentRoomName = document.getElementById("currentRoomName");
    this.memberGrid = document.getElementById("roomMembers");
    this.statusSelect = document.getElementById("myStatusSelect");

    document.getElementById("createRoomBtn").addEventListener("click", () => this.createRoom());
    document.getElementById("joinRoomBtn").addEventListener("click", () => this.createRoom());
    document.getElementById("refreshRoomBtn").addEventListener("click", () => this.refresh());
    this.statusSelect.addEventListener("change", () => this.updateStatus());

    this.currentRoom = await FlowStudyAPI.getCurrentRoom();
    this.render();
  },

  async createRoom() {
    const roomName = this.roomNameInput.value.trim() || "未命名自習室";
    this.currentRoom = await FlowStudyAPI.createRoom(roomName);
    this.render();
  },

  async refresh() {
    this.currentRoom = await FlowStudyAPI.getCurrentRoom();
    this.render();
  },

  async updateStatus() {
    this.currentRoom = await FlowStudyAPI.updateMyRoomStatus(this.statusSelect.value);
    this.render();
  },

  render() {
    if (!this.currentRoom) {
      this.currentRoomName.textContent = "尚未加入";
      this.memberGrid.innerHTML = `<p class="hint">請先建立或加入一個自習室。</p>`;
      return;
    }

    this.currentRoomName.textContent = `${this.currentRoom.roomName} (${this.currentRoom.id})`;
    this.memberGrid.innerHTML = "";

    this.currentRoom.members.forEach(member => {
      const card = document.createElement("div");
      card.className = "member-card";
      card.innerHTML = `
        <div class="avatar">${this.initial(member.nickname)}</div>
        <strong>${member.nickname}</strong>
        <p class="status ${member.status}">${this.statusText(member.status)}</p>
        <p class="hint">今日 ${member.todayStudyMinutes || 0} 分鐘</p>
        ${member.id !== 1 ? '<button class="btn small-btn wake-btn">拍一拍</button>' : ''}
      `;

      const wakeBtn = card.querySelector(".wake-btn");
      if (wakeBtn) {
        wakeBtn.addEventListener("click", () => alert(`已傳送提醒給 ${member.nickname}`));
      }

      this.memberGrid.appendChild(card);
    });
  },

  initial(name) {
    return String(name || "?").slice(0, 1).toUpperCase();
  },

  statusText(status) {
    return {
      studying: "讀書中",
      resting: "休息中",
      offline: "離線"
    }[status] || "未知";
  }
};

document.addEventListener("DOMContentLoaded", () => StudyRoomUI.init());
