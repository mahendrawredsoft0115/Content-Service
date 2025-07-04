package com.project.content.service;

import com.project.content.dto.ContentUploadResponse;
import com.project.content.entity.Content;
import com.project.content.enums.Visibility;
import com.project.content.exception.ContentNotFoundException;
import com.project.content.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final String uploadDir = "uploads/";

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    // Existing: Upload file
    public ContentUploadResponse uploadFile(MultipartFile file, String visibilityStr, Long creatorId) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        Files.createDirectories(Paths.get(uploadDir));
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;

        Path filePath = Paths.get(uploadDir + uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Visibility visibility;
        try {
            visibility = Visibility.valueOf(visibilityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid visibility: must be PUBLIC or PRIVATE");
        }

        Content content = Content.builder()
                .filename(uniqueFilename)
                .fileType(file.getContentType())
                .url("/files/" + uniqueFilename)
                .creatorId(creatorId)
                .visibility(visibility)
                .uploadedAt(LocalDateTime.now())
                .build();

        contentRepository.save(content);

        return new ContentUploadResponse(uniqueFilename, "/files/" + uniqueFilename, visibility.name(), "Uploaded successfully");
    }

    // ✅ New: Fetch posts by creator with visibility filter
    public List<Content> getPostsByCreator(Long creatorId, boolean includePrivate) {
        List<Content> posts;

        if (includePrivate) {
            posts = contentRepository.findByCreatorId(creatorId);
        } else {
            posts = contentRepository.findByCreatorIdAndVisibility(creatorId, Visibility.PUBLIC);
        }

        if (posts.isEmpty()) {
            throw new ContentNotFoundException("No posts found for creator ID: " + creatorId);
        }

        return posts;
    }
}
