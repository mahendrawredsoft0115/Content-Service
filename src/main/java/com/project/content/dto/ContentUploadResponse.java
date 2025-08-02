package com.project.content.dto;

import com.project.content.enums.Visibility;
import lombok.*;

/**
 * Response DTO for uploaded content.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentUploadResponse {
    private String filename;
    private String url;
    private Visibility visibility;
    private String message;
}
