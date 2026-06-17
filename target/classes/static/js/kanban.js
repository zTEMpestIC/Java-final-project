/**
 * kanban.js
 * 點擊切換式 Kanban，看板狀態：todo → doing → done → todo。
 * 如果之後要做拖曳，可以把 click handler 改成 drag and drop。
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

    this.todos = await FlowStudyAPI.getTodos();
    this.render();

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
    await this.persist();
    this.render();
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

    await this.persist();
    this.render();
  },

  async deleteTodo(id) {
    this.todos = this.todos.filter(todo => todo.id !== id);
    await this.persist();
    this.render();
  },

  async persist() {
    await FlowStudyAPI.saveTodos(this.todos);
  },

  render() {
    Object.values(this.columns).forEach(column => column.innerHTML = "");

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

      this.columns[todo.status]?.appendChild(card);
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
