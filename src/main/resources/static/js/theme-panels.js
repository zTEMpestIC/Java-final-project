// js/theme-panels.js
document.addEventListener("DOMContentLoaded", () => {
  const navLinks = document.querySelectorAll('.nav-link');
  const mainContent = document.querySelector('.main-content');
  const sections = document.querySelectorAll('.main-content > section');

  // 動態建立背景遮罩
  let overlay = document.querySelector('.panel-overlay');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.className = 'panel-overlay';
    document.body.appendChild(overlay);
  }

  // 關閉所有抽屜的方法
  function closeAll() {
    mainContent.classList.remove('open');
    overlay.classList.remove('active');
    navLinks.forEach(l => l.classList.remove('active'));
  }

  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();

      // 目標 3: 如果點擊的是「已經在開啟狀態」的選單，就關閉它並結束動作
      if (link.classList.contains('active')) {
        closeAll();
        return; 
      }

      const targetId = link.getAttribute('href');
      const targetSection = document.querySelector(targetId);
      if (!targetSection) return;

      // 如果抽屜本來就是開著的，為了避免右側跟下方直接飛來飛去
      // 我們先把它收起來，等動畫跑完 (250ms) 再展開新的
      const isCurrentlyOpen = mainContent.classList.contains('open');

      if (isCurrentlyOpen) {
        mainContent.classList.remove('open'); // 先收合
        setTimeout(() => {
          openSection(targetId, targetSection, link);
        }, 250);
      } else {
        openSection(targetId, targetSection, link);
      }
    });
  });

  // 處理開啟特定區塊的邏輯
  function openSection(targetId, targetSection, link) {
    // 隱藏內部所有區塊
    sections.forEach(s => s.classList.remove('active-section'));
    navLinks.forEach(l => l.classList.remove('active'));

    // 目標 2: 如果點擊的是統計資料，切換為「底部抽屜」模式
    if (targetId === '#stats-section') {
      mainContent.classList.add('stats-mode');
    } else {
      mainContent.classList.remove('stats-mode');
    }

    // 啟動對應區塊與選單亮起
    targetSection.classList.add('active-section');
    link.classList.add('active');

    // 滑出面板與顯示遮罩
    mainContent.classList.add('open');
    overlay.classList.add('active');
  }

  // 點擊背景空白處關閉
  overlay.addEventListener('click', closeAll);
});