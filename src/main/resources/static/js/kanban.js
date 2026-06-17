/**
 * kanban.js
 * 點擊切換式 Kanban，看板狀態：todo → doing → done → todo。
 */

const Kanban = {
  todos: [],

  async init() {
    this.form = document.getElementById("todoForm");
    this.input = document.getElementById("todoInput");
    this.columns = {
      todo: document.getElementById("todoList"),
      doing: document.getElementById("doingList"),
      done: document.getElementById("doneList")
    };

    // 加上 try-catch 防止 API 錯誤導致整支程式崩潰
    try {
      this.todos = await FlowStudyAPI.getTodos() || [];
    } catch (error) {
      console.warn("無法從 API 取得待辦事項，使用本地暫存:", error);
      this.todos = [];
    }
    
    this.render();

    // 確保這段監聽事件有被成功綁定
    this.form.addEventListener("submit", event => {
      event.preventDefault();
      this.addTodo();
    });
  },

  async addTodo() {
    const title = this.input.value.trim();
    if (!title) return;

    this.todos.push({
      id: crypto.randomUUID(),
      title,
      status: "todo"
    });

    this.input.value = "";
    this.render(); // 先更新畫面，讓使用者感覺不到延遲

    try {
      await this.persist();
    } catch (error) {
      console.warn("儲存失敗:", error);
    }
  },

  async changeStatus(id, status) {
    const next = {
      todo: "doing",
      doing: "done",
      done: "todo"
    };

    this.todos = this.todos.map(todo =>
      todo.id === id ? { ...todo, status: status || next[todo.status] } : todo
    );

    this.render();

    try {
      await this.persist();
    } catch (error) {
      console.warn("狀態更新失敗:", error);
    }
  },

  async deleteTodo(id) {
    this.todos = this.todos.filter(todo => todo.id !== id);
    this.render();

    try {
      await this.persist();
    } catch (error) {
      console.warn("刪除失敗:", error);
    }
  },

  async persist() {
    await FlowStudyAPI.saveTodos(this.todos);
  },

  render() {
    Object.values(this.columns).forEach(column => {
      if (column) column.innerHTML = "";
    });

    this.todos.forEach(todo => {
      const card = document.createElement("div");
      card.className = "task-card";
      card.innerHTML = `
        <strong>${this.escape(todo.title)}</strong>
        <div class="task-actions">
          <button class="btn small-btn" data-action="next">下一階段</button>
          <button class="btn small-btn danger" data-action="delete">刪除</button>
        </div>
      `;

      card.querySelector('[data-action="next"]').addEventListener("click", () => this.changeStatus(todo.id));
      card.querySelector('[data-action="delete"]').addEventListener("click", () => this.deleteTodo(todo.id));

      if (this.columns[todo.status]) {
        this.columns[todo.status].appendChild(card);
      }
    });
  },

  escape(text) {
    return text.replace(/[&<>"']/g, char => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#039;"
    }[char]));
  }
};

document.addEventListener("DOMContentLoaded", () => Kanban.init());