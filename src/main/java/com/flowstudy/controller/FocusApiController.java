package com.flowstudy.controller;

import com.flowstudy.model.FocusLog;
import com.flowstudy.model.Subject;
import com.flowstudy.repository.FocusLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FocusApiController {

    private final FocusLogRepository focusLogRepository;
    private final EntityManager entityManager;

    public FocusApiController(FocusLogRepository focusLogRepository, EntityManager entityManager) {
        this.focusLogRepository = focusLogRepository;
        this.entityManager = entityManager;
    }

    // 1. 接收前端傳來的專注紀錄並存入資料庫
    @PostMapping("/focus-logs")
    @Transactional
    public Map<String, Object> saveFocusLog(@RequestBody FocusLogPayload payload) {
        List<Subject> subjects = entityManager.createQuery("SELECT s FROM Subject s WHERE s.name = :name", Subject.class)
                .setParameter("name", payload.subject())
                .getResultList();
                
        Subject subject;
        if (subjects.isEmpty()) {
            subject = Subject.builder()
                    .id(UUID.randomUUID().toString())
                    .name(payload.subject())
                    .colorCode("#4CAF50")
                    .build();
            entityManager.persist(subject);
        } else {
            subject = subjects.get(0);
        }

        FocusLog log = FocusLog.builder()
                .userId(String.valueOf(payload.userId()))
                .subject(subject)
                .durationMs(payload.durationSeconds() * 1000L)
                .endTime(LocalDateTime.parse(payload.endTime(), DateTimeFormatter.ISO_DATE_TIME))
                .build();

        focusLogRepository.save(log);
        System.out.println("✅ 成功儲存專注紀錄：" + subject.getName() + "，共 " + payload.durationSeconds() + " 秒");

        // 🌟 修正點：明確指定型別
        return Map.<String, Object>of("status", "success", "id", log.getId());
    }

    // 2. 獲取今日總專注秒數
    @GetMapping("/focus-logs/today")
    public Map<String, Object> getTodayFocus(@RequestParam String userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<FocusLogRepository.SubjectTimeAggregation> stats = 
            focusLogRepository.sumDurationBySubject(userId, startOfDay, endOfDay);

        long totalMs = stats.stream().mapToLong(FocusLogRepository.SubjectTimeAggregation::getTotalDuration).sum();
        
        // 🌟 修正點：明確指定型別
        return Map.<String, Object>of("totalSeconds", totalMs / 1000);
    }

    // 3. 獲取統計圖表資料 (圓餅圖與熱點圖)
    @GetMapping("/statistics/overview")
    public Map<String, Object> getStatistics(@RequestParam String userId) {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        LocalDateTime today = LocalDateTime.now();

        List<FocusLogRepository.SubjectTimeAggregation> pieStats = 
            focusLogRepository.sumDurationBySubject(userId, oneYearAgo, today);
            
        List<Map<String, Object>> subjectRatio = pieStats.stream()
            // 🌟 修正點：明確指定型別
            .map(stat -> Map.<String, Object>of(
                "subject", stat.getSubjectName(),
                "minutes", stat.getTotalDuration() / 60000 
            )).collect(Collectors.toList());

        List<FocusLogRepository.DailyFocusAggregation> heatStats = 
            focusLogRepository.getDailyFocusHeatmap(userId, oneYearAgo);

        List<Map<String, Object>> heatmap = heatStats.stream()
            // 🌟 修正點：明確指定型別
            .map(stat -> Map.<String, Object>of(
                "date", stat.getFocusDate().toString(),
                "minutes", stat.getDailyTotal() / 60000
            )).collect(Collectors.toList());

        // 🌟 修正點：明確指定型別
        return Map.<String, Object>of(
            "subjectRatio", subjectRatio,
            "heatmap", heatmap
        );
    }

    
    public record FocusLogPayload(int userId, String subject, String startTime, String endTime, long durationSeconds) {}
}