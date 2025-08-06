package com.project.content.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReportRequestDto {
    private Long reportedBy;
    private UUID postId;
    private String reason;
}
