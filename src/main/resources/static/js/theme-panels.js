document.addEventListener("DOMContentLoaded", () => {
  console.log("theme-panels.js 成功載入！"); // 測試有沒有被正確讀取

  const navLinks = document.querySelectorAll('.nav-link');
  const mainContent = document.querySelector('.main-content');
  const sections = document.querySelectorAll('.main-content > section');

  // 動態建立背景透明遮罩
  let overlay = document.querySelector('.panel-overlay');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.className = 'panel-overlay';
    document.body.appendChild(overlay);
  }

  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault(); // 阻擋原本的網址跳轉
      console.log("點擊了選單：", link.innerText); // 測試有沒有成功攔截點擊

      const targetId = link.getAttribute('href');
      
      // 處理 HTML 中統計 ID 不一致的問題 (#stats-section vs #heatmap-section)
      let targetElement = document.querySelector(targetId);
      if (!targetElement && targetId === '#stats-section') {
        targetElement = document.querySelector('#heatmap-section');
      }
      
      if (!targetElement) {
        console.warn("找不到對應的卡片:", targetId);
        return; 
      }

      // 找到包含該卡片的最外層 section
      const targetSection = targetElement.closest('section');

      // 1. 先把所有區塊隱藏
      sections.forEach(s => s.classList.remove('active-section'));
      navLinks.forEach(l => l.classList.remove('active'));

      // 2. 顯示被點擊的區塊
      targetSection.classList.add('active-section');
      link.classList.add('active');

      // 3. 展開右側抽屜與遮罩
      mainContent.classList.add('open');
      overlay.classList.add('active');
    });
  });

  // 點擊遮罩時關閉抽屜
  overlay.addEventListener('click', () => {
    mainContent.classList.remove('open');
    overlay.classList.remove('active');
    navLinks.forEach(l => l.classList.remove('active'));
  });
});