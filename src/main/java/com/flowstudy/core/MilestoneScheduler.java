package com.flowstudy.core;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 里程碑每日進度推算結果
 */
public record DailyMilestoneTarget(
    LocalDate date,
    double dailyTarget,
    double cumulativeTarget,
    double adjustmentFactor
) {}

/**
 * 歷史完成效率紀錄
 */
public record HistoricalEfficiency(
    LocalDate date,
    double plannedProgress,
    double actualProgress,
    double completionRate
) {}

/**
 * 里程碑智能推算算法
 */
public class MilestoneScheduler {
    private final LocalDate deadline;
    private final double totalProgress;
    private final List<HistoricalEfficiency> history;
    private final LocalDate startDate;

    public MilestoneScheduler(LocalDate deadline, double totalProgress) {
        this(deadline, totalProgress, new ArrayList<>(), LocalDate.now());
    }

    public MilestoneScheduler(LocalDate deadline, double totalProgress,
                            List<HistoricalEfficiency> history) {
        this(deadline, totalProgress, history, LocalDate.now());
    }

    public MilestoneScheduler(LocalDate deadline, double totalProgress,
                            List<HistoricalEfficiency> history, LocalDate startDate) {
        if (deadline.isBefore(startDate)) {
            throw new IllegalArgumentException("截止日期不能早於開始日期");
        }
        if (totalProgress <= 0) {
            throw new IllegalArgumentException("總目標進度必須大於 0");
        }

        this.deadline = deadline;
        this.totalProgress = totalProgress;
        this.history = new ArrayList<>(history);
        this.startDate = startDate;
    }

    /**
     * 生成里程碑每日進度計畫
     */
    public List<DailyMilestoneTarget> generateMilestoneSchedule() {
        long daysRemaining = ChronoUnit.DAYS.between(startDate, deadline) + 1;

        if (daysRemaining <= 0) {
            throw new IllegalArgumentException("沒有足夠的時間完成目標");
        }

        double baseDaily = totalProgress / daysRemaining;
        double efficiencyFactor = calculateEfficiencyFactor();

        List<DailyMilestoneTarget> schedule = new ArrayList<>();
        double cumulativeTarget = 0;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(deadline)) {
            double adjustedDaily = baseDaily * efficiencyFactor;

            if (currentDate.equals(deadline)) {
                adjustedDaily = totalProgress - cumulativeTarget;
            }

            cumulativeTarget += adjustedDaily;

            schedule.add(new DailyMilestoneTarget(
                currentDate,
                adjustedDaily,
                cumulativeTarget,
                efficiencyFactor
            ));

            currentDate = currentDate.plusDays(1);
        }

        return schedule;
    }

    /**
     * 根據實際進度重新計算剩餘計畫
     */
    public List<DailyMilestoneTarget> recalculateSchedule(
        Map<LocalDate, Double> actualProgressByDate) {
        updateHistoricalEfficiency(actualProgressByDate);

        double completedProgress = actualProgressByDate.values()
            .stream()
            .mapToDouble(Double::doubleValue)
            .sum();

        LocalDate nextDate = startDate;
        for (LocalDate date : actualProgressByDate.keySet()) {
            if (date.isBefore(deadline) && date.isAfter(nextDate)) {
                nextDate = date.plusDays(1);
            }
        }

        double remainingProgress = totalProgress - completedProgress;
        MilestoneScheduler newScheduler = new MilestoneScheduler(
            deadline, remainingProgress, history, nextDate);

        return newScheduler.generateMilestoneSchedule();
    }

    public double getCompletionEfficiency() {
        return calculateEfficiencyFactor();
    }

    public double calculateRiskLevel() {
        if (history.isEmpty()) {
            return 0.5;
        }

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        double avgRecentCompletion = history.stream()
            .filter(h -> h.date().isAfter(sevenDaysAgo))
            .mapToDouble(HistoricalEfficiency::completionRate)
            .average()
            .orElse(1.0);

        return Math.max(0.0, Math.min(1.0, 1.0 - avgRecentCompletion));
    }

    public String generateWarning() {
        double riskLevel = calculateRiskLevel();

        if (riskLevel > 0.7) {
            return "⚠️ 進度嚴重落後！建議加快讀書速度或延長每日時間。";
        } else if (riskLevel > 0.5) {
            return "⏰ 進度略有落後，建議多加努力。";
        }

        return "";
    }

    private double calculateEfficiencyFactor() {
        if (history.isEmpty()) {
            return 1.0;
        }

        LocalDate twoWeeksAgo = LocalDate.now().minusDays(14);
        double avgCompletion = history.stream()
            .filter(h -> h.date().isAfter(twoWeeksAgo))
            .mapToDouble(HistoricalEfficiency::completionRate)
            .average()
            .orElse(1.0);

        return Math.max(0.8, avgCompletion);
    }

    private void updateHistoricalEfficiency(Map<LocalDate, Double> actualProgressByDate) {
        for (LocalDate date : actualProgressByDate.keySet()) {
            double actual = actualProgressByDate.get(date);
            double planned = totalProgress / ChronoUnit.DAYS.between(startDate, deadline);
            double completionRate = actual / planned;

            history.add(new HistoricalEfficiency(date, planned, actual, completionRate));
        }
    }
}
