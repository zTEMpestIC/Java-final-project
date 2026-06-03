/**
 * api.js
 * 統一管理前端與 Java 後端 API 溝通。
 * 如果後端還沒完成，會自動 fallback 到 localStorage，方便前端先獨立展示。
 */

const API_BASE = "/api";
const DEMO_USER_ID = 1;

const LocalStore = {
  get(key, defaultValue) {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : defaultValue;
  },
  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }
};

async function request(path, options = {}) {
  try {
    const response = await fetch(`${API_BASE}${path}`, {
      headers: { "Content-Type": "application/json" },
      ...options
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.warn(`[API fallback] ${path}`, error.message);
    document.getElementById("connectionStatus").textContent = "Local Fallback Mode";
    return null;
  }
}

const FlowStudyAPI = {
  async saveFocusLog(log) {
    const payload = {
      userId: DEMO_USER_ID,
      subject: log.subject,
      startTime: log.startTime,
      endTime: log.endTime,
      durationSeconds: log.durationSeconds
    };

    const serverResult = await request("/focus-logs", {
      method: "POST",
      body: JSON.stringify(payload)
    });

    if (serverResult) return serverResult;

    const logs = LocalStore.get("focusLogs", []);
    const newLog = { id: crypto.randomUUID(), ...payload };
    logs.push(newLog);
    LocalStore.set("focusLogs", logs);
    return newLog;
  },

  async getTodayFocusSeconds() {
    const serverResult = await request(`/focus-logs/today?userId=${DEMO_USER_ID}`);
    if (serverResult && typeof serverResult.totalSeconds === "number") {
      return serverResult.totalSeconds;
    }

    const today = new Date().toISOString().slice(0, 10);
    const logs = LocalStore.get("focusLogs", []);
    return logs
      .filter(log => String(log.startTime).startsWith(today))
      .reduce((sum, log) => sum + Number(log.durationSeconds || 0), 0);
  },

  async getTodos() {
    const serverResult = await request(`/todos?userId=${DEMO_USER_ID}`);
    if (serverResult) return serverResult;

    return LocalStore.get("todos", [
      { id: crypto.randomUUID(), title: "完成計時器功能", status: "todo" },
      { id: crypto.randomUUID(), title: "整理 Mermaid 圖", status: "doing" },
      { id: crypto.randomUUID(), title: "決定網站架構", status: "done" }
    ]);
  },

  async saveTodos(todos) {
    const serverResult = await request("/todos/batch", {
      method: "POST",
      body: JSON.stringify({ userId: DEMO_USER_ID, todos })
    });

    LocalStore.set("todos", todos);
    return serverResult || todos;
  },

  async createRoom(roomName) {
    const serverResult = await request("/rooms", {
      method: "POST",
      body: JSON.stringify({ roomName, ownerId: DEMO_USER_ID })
    });

    if (serverResult) return serverResult;

    const room = {
      id: "R" + Math.floor(Math.random() * 9000 + 1000),
      roomName,
      members: [
        { id: 1, nickname: "You", status: "studying", todayStudyMinutes: 0 },
        { id: 2, nickname: "Amy", status: "resting", todayStudyMinutes: 92 },
        { id: 3, nickname: "Tom", status: "studying", todayStudyMinutes: 143 }
      ]
    };
    LocalStore.set("currentRoom", room);
    return room;
  },

  async getCurrentRoom() {
    const localRoom = LocalStore.get("currentRoom", null);
    if (!localRoom) return null;

    const serverResult = await request(`/rooms/${localRoom.id}`);
    return serverResult || localRoom;
  },

  async updateMyRoomStatus(status) {
    const room = LocalStore.get("currentRoom", null);
    if (!room) return null;

    const serverResult = await request(`/rooms/${room.id}/status`, {
      method: "PUT",
      body: JSON.stringify({ userId: DEMO_USER_ID, status })
    });

    room.members = room.members.map(member =>
      member.id === DEMO_USER_ID ? { ...member, status } : member
    );
    LocalStore.set("currentRoom", room);
    return serverResult || room;
  },

  async getStats() {
    const serverResult = await request(`/statistics/overview?userId=${DEMO_USER_ID}`);
    if (serverResult) return serverResult;

    const logs = LocalStore.get("focusLogs", []);
    const bySubject = {};
    logs.forEach(log => {
      bySubject[log.subject] = (bySubject[log.subject] || 0) + Number(log.durationSeconds || 0);
    });

    const heatmap = [];
    const today = new Date();
    for (let i = 181; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(today.getDate() - i);
      const dateText = d.toISOString().slice(0, 10);
      const seconds = logs
        .filter(log => String(log.startTime).startsWith(dateText))
        .reduce((sum, log) => sum + Number(log.durationSeconds || 0), 0);
      heatmap.push({ date: dateText, minutes: Math.round(seconds / 60) });
    }

    return {
      subjectRatio: Object.entries(bySubject).map(([subject, seconds]) => ({
        subject,
        minutes: Math.round(seconds / 60)
      })),
      heatmap
    };
  }
};

function formatDuration(seconds) {
  const totalMinutes = Math.floor(seconds / 60);
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;

  if (hours <= 0) return `${minutes} 分鐘`;
  return `${hours} 小時 ${minutes} 分鐘`;
}

async function refreshTodayFocusText() {
  const seconds = await FlowStudyAPI.getTodayFocusSeconds();
  const target = document.getElementById("todayFocusText");
  if (target) target.textContent = formatDuration(seconds);
}

document.addEventListener("DOMContentLoaded", refreshTodayFocusText);
