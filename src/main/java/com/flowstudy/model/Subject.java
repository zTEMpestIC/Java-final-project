package com.flowstudy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {
    
    @Id
    @Column(length = 36)
    private String id; // 對應 DTO 的 UUID

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "color_code", nullable = false, length = 7)
    private String colorCode; // 例如 "#FF5733"
}