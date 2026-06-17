package com.flowstudy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowstudy.core.MilestoneScheduler;
import com.flowstudy.core.contract.a.MilestoneDTO;

@RestController
@RequestMapping("/api/milestone")
public class MilestoneController {

    private final MilestoneScheduler scheduler;

    public MilestoneController() {
        this.scheduler = new MilestoneScheduler();
    }

    @PostMapping("/calculate")
    public Map<String, Object> calculate(@RequestBody MilestoneDTO dto) {

        List<Integer> dailyTargets =
                scheduler.calculateDailyTargets(dto);

        return Map.of(
                "title", dto.title(),
                "daysRemaining", dto.getDaysRemaining(),
                "dailyTargets", dailyTargets
        );
    }
}