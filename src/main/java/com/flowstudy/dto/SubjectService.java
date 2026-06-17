package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.Subject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectDTO createSubject(SubjectDTO dto) {
        Subject subject = new Subject(
            dto.userId(),
            dto.name(),
            dto.color()
        );
        Subject saved = subjectRepository.save(subject);
        return convertToDTO(saved);
    }

    public SubjectDTO getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));
        return convertToDTO(subject);
    }

    public List<SubjectDTO> getSubjectsByUser(UUID userId) {
        return subjectRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public SubjectDTO updateSubject(SubjectDTO dto) {
        Subject subject = subjectRepository.findById(dto.id())
            .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + dto.id()));
        
        subject.setName(dto.name());
        subject.setColor(dto.color());
        subject.setTotalFocusMs(dto.totalFocusMs());
        
        Subject updated = subjectRepository.save(subject);
        return convertToDTO(updated);
    }

    public void updateTotalFocusTime(UUID subjectId, long durationMs) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));
        
        subject.setTotalFocusMs(subject.getTotalFocusMs() + durationMs);
        subjectRepository.save(subject);
    }

    public void deleteSubject(UUID id) {
        subjectRepository.deleteById(id);
    }

    private SubjectDTO convertToDTO(Subject entity) {
        return new SubjectDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getName(),
            entity.getColor(),
            entity.getTotalFocusMs(),
            entity.getCreatedAt()
        );
    }
}
