package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.Milestone;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;

    public MilestoneDTO createMilestone(MilestoneDTO dto) {
        Milestone milestone = new Milestone(
            dto.userId(),
            dto.subjectId(),
            dto.title(),
            dto.targetProgress(),
            dto.deadline(),
            dto.priority()
        );
        Milestone saved = milestoneRepository.save(milestone);
        return convertToDTO(saved);
    }

    public MilestoneDTO getMilestoneById(UUID id) {
        Milestone milestone = milestoneRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + id));
        return convertToDTO(milestone);
    }

    public List<MilestoneDTO> getMilestonesByUser(UUID userId) {
        return milestoneRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<MilestoneDTO> getMilestonesByUserAndStatus(UUID userId, String status) {
        return milestoneRepository.findByUserIdAndStatus(userId, status)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<MilestoneDTO> getMilestonesBySubject(UUID subjectId) {
        return milestoneRepository.findBySubjectId(subjectId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public void updateMilestoneProgress(UUID milestoneId, double progressDelta) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
            .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + milestoneId));
        
        double newProgress = milestone.getCurrentProgress() + progressDelta;
        milestone.setCurrentProgress(newProgress);
        
        if (newProgress >= milestone.getTargetProgress()) {
            milestone.setStatus("COMPLETED");
        } else {
            double progressPercentage = newProgress / milestone.getTargetProgress();
            if (progressPercentage < 0.5) {
                milestone.setStatus("AT_RISK");
            } else {
                milestone.setStatus("ON_TRACK");
            }
        }
        
        milestoneRepository.save(milestone);
    }

    public MilestoneDTO updateMilestone(MilestoneDTO dto) {
        Milestone milestone = milestoneRepository.findById(dto.id())
            .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + dto.id()));
        
        milestone.setTitle(dto.title());
        milestone.setTargetProgress(dto.targetProgress());
        milestone.setCurrentProgress(dto.currentProgress());
        milestone.setDeadline(dto.deadline());
        milestone.setPriority(dto.priority());
        milestone.setStatus(dto.status());
        
        Milestone updated = milestoneRepository.save(milestone);
        return convertToDTO(updated);
    }

    public void deleteMilestone(UUID id) {
        milestoneRepository.deleteById(id);
    }

    private MilestoneDTO convertToDTO(Milestone entity) {
        return new MilestoneDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getSubjectId(),
            entity.getTitle(),
            entity.getTargetProgress(),
            entity.getCurrentProgress(),
            entity.getDeadline(),
            entity.getPriority(),
            entity.getStatus()
        );
    }
}
