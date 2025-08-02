package com.project.content.service;

import com.project.content.dto.ContentUploadResponse;
import com.project.content.entity.Content;
import com.project.content.enums.ContentType;
import com.project.content.enums.FileType;
import com.project.content.enums.Visibility;
import com.project.content.exception.AccessDeniedException;
import com.project.content.exception.ContentNotFoundException;
import com.project.content.exception.ResourceNotFoundException;
import com.project.content.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for handling content upload logic,
 * saving metadata to the database, and preparing response.
 */
@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserAccessService userAccessService;

    /**
     * Constructor injection for ContentRepository.
     *
     * @param contentRepository JPA repository for Content entity
     */
    public ContentService(ContentRepository contentRepository, UserAccessService userAccessService) {
        this.contentRepository = contentRepository;
        this.userAccessService = userAccessService;
    }

    /**
     * Uploads content file, stores metadata in DB, and returns upload response.
     *
     * @param creatorId   Creator's user ID
     * @param title       Title of the content
     * @param description Description of the content
     * @param price       Price if content is paid
     * @param visibility  PUBLIC or PRIVATE
     * @param fileType    IMAGE or VIDEO
     * @param contentType FREE or PAID
     * @param file        Multipart uploaded file
     * @return ContentUploadResponse with status and metadata
     */
    public ContentUploadResponse uploadContent(
            Long creatorId,
            String title,
            String description,
            BigDecimal price,
            Visibility visibility,
            FileType fileType,
            ContentType contentType,
            MultipartFile file
    ) {
        try {
            // Ensure upload directory exists
            File uploadFolder = ensureUploadDir();
            String baseDir = uploadFolder.getAbsolutePath() + "/";

            // Extract extension from original file name
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // Generate unique filename
            String filename = java.util.UUID.randomUUID() + extension;
            File dest = new File(baseDir + filename);
            file.transferTo(dest);

            String url = "/files/" + filename;

            // Save metadata to DB
            Content content = Content.builder()
                    .creatorId(creatorId)
                    .title(title)
                    .description(description)
                    .filename(filename)
                    .url(url)
                    .visibility(visibility)
                    .fileType(fileType)
                    .contentType(contentType)
                    .price(price)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            contentRepository.save(content);

            return ContentUploadResponse.builder()
                    .filename(filename)
                    .url(url)
                    .visibility(visibility)
                    .message("Uploaded successfully")
                    .build();

        } catch (Exception e) {
            return ContentUploadResponse.builder()
                    .filename(null)
                    .url(null)
                    .visibility(null)
                    .message("Upload failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Ensures the upload directory exists and returns it.
     *
     * @return File pointing to the /uploads directory
     */
    private File ensureUploadDir() {
        String baseDir = System.getProperty("user.dir") + "/uploads/";
        File uploadFolder = new File(baseDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        return uploadFolder;
    }


    public List<Content> getContentByCreator(Long creatorId, boolean includePrivate) {
        if (includePrivate) {
            return contentRepository.findByCreatorId(creatorId);
        } else {
            return contentRepository.findByCreatorIdAndVisibility(creatorId, Visibility.PUBLIC);
        }
    }

    public Content getContentById(UUID contentId, Long userId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ContentNotFoundException("Content not found with ID: " + contentId));

        if (content.getContentType() == ContentType.PAID) {
            if (userId == null || !userAccessService.hasAccess(userId, contentId)) {
                throw new AccessDeniedException("You must pay or subscribe to view this content.");
            }
        }

        return content;
    }






    public void moderateContent(UUID contentId, String reason) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with ID: " + contentId));

//        content.setVisibility(Visibility.DISABLED);
        content.setModerated(true);
        content.setModerationReason(reason);

        contentRepository.save(content);
    }



    public void deleteContentById(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));

        // Delete from S3 if needed
//        amazonS3.deleteObject(bucketName, content.getFileUrl());

        // Delete from DB
        contentRepository.deleteById(contentId);
    }



}
