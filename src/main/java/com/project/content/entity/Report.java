package com.project.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Long reportedBy;

    @Column(nullable = false)
    private UUID postId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime reportedAt;
}
