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
 * ContentService handles the business logic related to uploading,
 * retrieving, moderating, and deleting content for creators.
 * <p>
 * This includes:
 * - Saving content metadata
 * - Handling file storage locally (can be extended to S3)
 * - Content visibility control (public/private)
 * - Enforcing paid access for premium content
 */
@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserAccessService userAccessService;

    /**
     * Constructs ContentService with required dependencies.
     *
     * @param contentRepository Repository for accessing Content entity
     * @param userAccessService Service for validating user access to paid content
     */
    public ContentService(ContentRepository contentRepository, UserAccessService userAccessService) {
        this.contentRepository = contentRepository;
        this.userAccessService = userAccessService;
    }

    /**
     * Uploads a content file, stores metadata in DB, and returns a response.
     *
     * @param creatorId   ID of the creator uploading the content
     * @param title       Title of the content
     * @param description Description of the content
     * @param price       Price for the content (if paid)
     * @param visibility  PUBLIC or PRIVATE
     * @param fileType    IMAGE or VIDEO
     * @param contentType FREE or PAID
     * @param file        MultipartFile being uploaded
     * @return ContentUploadResponse with upload status and metadata
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
            // Ensure /uploads directory exists
            File uploadFolder = ensureUploadDir();
            String baseDir = uploadFolder.getAbsolutePath() + "/";

            // Get file extension
            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            // Generate unique file name
            String filename = UUID.randomUUID() + extension;
            File dest = new File(baseDir + filename);
            file.transferTo(dest);

            String url = "/files/" + filename;

            // Save content metadata in DB
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
     * Returns content by creator ID. Optionally includes PRIVATE content.
     *
     * @param creatorId      Creator's user ID
     * @param includePrivate If true, includes private content
     * @return List of Content objects
     */
    public List<Content> getContentByCreator(Long creatorId, boolean includePrivate) {
        if (includePrivate) {
            return contentRepository.findByCreatorId(creatorId);
        } else {
            return contentRepository.findByCreatorIdAndVisibility(creatorId, Visibility.PUBLIC);
        }
    }

    /**
     * Returns a single content by its ID, checking access if it’s PAID content.
     *
     * @param contentId UUID of the content
     * @param userId    User requesting the content
     * @return Content entity
     * @throws AccessDeniedException     if user does not have access to paid content
     * @throws ContentNotFoundException if content does not exist
     */
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

    /**
     * Moderates a content by marking it as reviewed and setting a reason.
     *
     * @param contentId UUID of the content
     * @param reason    Reason for moderation
     * @throws ResourceNotFoundException if content does not exist
     */
    public void moderateContent(UUID contentId, String reason) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with ID: " + contentId));

        content.setModerated(true);
        content.setModerationReason(reason);
        contentRepository.save(content);
    }

    /**
     * Deletes content by ID from the database.
     * (Optionally extendable to delete from S3/local filesystem)
     *
     * @param contentId UUID of the content
     * @throws ResourceNotFoundException if content is not found
     */
    public void deleteContentById(UUID contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));

        // Optional: Delete file from S3/local folder if needed
        // amazonS3.deleteObject(bucketName, content.getFileUrl());

        contentRepository.deleteById(contentId);
    }

    /**
     * Ensures the /uploads directory exists; creates it if missing.
     *
     * @return File object pointing to the upload directory
     */
    private File ensureUploadDir() {
        String baseDir = System.getProperty("user.dir") + "/uploads/";
        File uploadFolder = new File(baseDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        return uploadFolder;
    }
}
