# FlowStudy Frontend

這是一組可以直接放進 Spring Boot `src/main/resources/static/` 的前端檔案。

## 檔案結構

```text
static/
├── index.html
├── css/
│   └── style.css
├── js/
│   ├── api.js
│   ├── timer.js
│   ├── audio.js
│   ├── kanban.js
│   ├── room.js
│   ├── stats.js
│   └── milestone.js
└── assets/
    └── sounds/
        ├── rain.mp3
        ├── cafe.mp3
        ├── campfire.mp3
        └── keyboard.mp3
```

## 功能

- 沉浸式計時器
- 正計時 / 倒計時 / 番茄鐘
- 圓形進度條
- 白噪音播放與獨立音量
- 待辦 Kanban 看板
- 自習室 MVP 畫面
- 熱點圖與科目圓餅圖
- 里程碑每日進度推算簡化版

## 後端尚未完成時

`api.js` 會自動使用 localStorage fallback，所以前端可以先獨立展示。

## 後端 API 建議

- `POST /api/focus-logs`
- `GET /api/focus-logs/today?userId=1`
- `GET /api/todos?userId=1`
- `POST /api/todos/batch`
- `POST /api/rooms`
- `GET /api/rooms/{id}`
- `PUT /api/rooms/{id}/status`
- `GET /api/statistics/overview?userId=1`
