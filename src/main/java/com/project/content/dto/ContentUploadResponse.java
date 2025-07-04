package com.project.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentUploadResponse {
    private String filename;
    private String url;
    private String visibility;
    private String message;
}
