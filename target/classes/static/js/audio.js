/**
 * audio.js
 * 白噪音播放與音量控制。
 * 網站版請使用 HTMLAudioElement / Web Audio API，不使用 Java javax.sound.sampled。
 */

const AudioMixer = {
  tracks: {},

  init() {
    this.createTrack("rain", "assets/sounds/rain.mp3");
    this.createTrack("cafe", "assets/sounds/cafe.mp3");
    this.createTrack("campfire", "assets/sounds/campfire.mp3");
    this.createTrack("keyboard", "assets/sounds/keyboard.mp3");

    document.querySelectorAll(".noise-item").forEach(item => {
      const sound = item.dataset.sound;
      const button = item.querySelector(".audio-toggle");
      const slider = item.querySelector(".volume-slider");

      button.addEventListener("click", () => this.toggle(sound, button));
      slider.addEventListener("input", () => this.setVolume(sound, Number(slider.value)));
      this.setVolume(sound, Number(slider.value));
    });

    document.getElementById("stopAllAudioBtn").addEventListener("click", () => this.stopAll());
  },

  createTrack(name, src) {
    const audio = new Audio(src);
    audio.loop = true;
    audio.preload = "auto";
    this.tracks[name] = audio;
  },

  async toggle(name, button) {
    const audio = this.tracks[name];
    if (!audio) return;

    if (audio.paused) {
      try {
        await audio.play();
        button.textContent = "停止";
        button.classList.add("primary");
      } catch (error) {
        alert(`無法播放 ${name}。請確認音檔是否存在於 assets/sounds/。`);
        console.error(error);
      }
    } else {
      audio.pause();
      button.textContent = "播放";
      button.classList.remove("primary");
    }
  },

  setVolume(name, volume) {
    const audio = this.tracks[name];
    if (!audio) return;
    audio.volume = Math.max(0, Math.min(1, volume));
  },

  stopAll() {
    Object.values(this.tracks).forEach(audio => {
      audio.pause();
      audio.currentTime = 0;
    });

    document.querySelectorAll(".audio-toggle").forEach(button => {
      button.textContent = "播放";
      button.classList.remove("primary");
    });
  }
};

document.addEventListener("DOMContentLoaded", () => AudioMixer.init());
