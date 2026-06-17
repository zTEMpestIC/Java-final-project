package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.CheckIn;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckInRepository checkInRepository;

    public CheckInDTO recordCheckIn(UUID userId) {
        Optional<CheckIn> lastCheckIn = checkInRepository.findLatestCheckInByUserId(userId);
        
        int consecutiveDays = 1;
        if (lastCheckIn.isPresent()) {
            LocalDate lastCheckInDate = lastCheckIn.get().getCheckedInAt().toLocalDate();
            LocalDate today = LocalDate.now();
            
            if (lastCheckInDate.equals(today)) {
                throw new IllegalArgumentException("Already checked in today");
            } else if (lastCheckInDate.equals(today.minusDays(1))) {
                consecutiveDays = lastCheckIn.get().getConsecutiveDays() + 1;
            }
        }
        
        CheckIn checkIn = new CheckIn(userId, consecutiveDays);
        CheckIn saved = checkInRepository.save(checkIn);
        return convertToDTO(saved);
    }

    public List<CheckInDTO> getCheckInsByUser(UUID userId) {
        return checkInRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Optional<CheckInDTO> getLatestCheckInByUser(UUID userId) {
        return checkInRepository.findLatestCheckInByUserId(userId)
            .map(this::convertToDTO);
    }

    public List<CheckInDTO> getCheckInsInDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return checkInRepository.findCheckInsInDateRange(userId, start, end)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public int getConsecutiveDaysCount(UUID userId) {
        Optional<CheckIn> latest = checkInRepository.findLatestCheckInByUserId(userId);
        if (latest.isEmpty()) {
            return 0;
        }
        
        LocalDate lastCheckInDate = latest.get().getCheckedInAt().toLocalDate();
        LocalDate today = LocalDate.now();
        
        if (lastCheckInDate.equals(today)) {
            return latest.get().getConsecutiveDays();
        } else if (lastCheckInDate.equals(today.minusDays(1))) {
            return latest.get().getConsecutiveDays();
        } else {
            return 0;
        }
    }

    private CheckInDTO convertToDTO(CheckIn entity) {
        return new CheckInDTO(
            entity.getId(),
            entity.getUserId(),
            entity.getCheckedInAt(),
            entity.getConsecutiveDays()
        );
    }
}
