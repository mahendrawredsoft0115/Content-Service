package com.project.content.controller;

import com.project.content.dto.ContentUploadResponse;
import com.project.content.dto.ModerationRequestDto;
import com.project.content.dto.ReportRequestDto;
import com.project.content.entity.Content;
import com.project.content.enums.ContentType;
import com.project.content.enums.FileType;
import com.project.content.enums.Visibility;
import com.project.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing content-related endpoints for creators and users.
 * Handles upload, fetch, delete, moderation, and reporting.
 */
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    /**
     * Uploads content with metadata and file.
     *
     * @param creatorId   creator user ID
     * @param title       title of the content
     * @param description description of the content
     * @param price       price (if paid content)
     * @param visibility  public/private
     * @param fileType    image/video
     * @param contentType FREE/PAID
     * @param file        uploaded file
     * @return response with URL, filename, and status
     */
    @PostMapping("/upload")
    public ResponseEntity<ContentUploadResponse> upload(
            @RequestParam Long creatorId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            @RequestParam Visibility visibility,
            @RequestParam FileType fileType,
            @RequestParam ContentType contentType,
            @RequestParam MultipartFile file
    ) {
        ContentUploadResponse response = contentService.uploadContent(
                creatorId, title, description, price, visibility, fileType, contentType, file
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches content by creator ID.
     *
     * @param creatorId      creator's ID
     * @param includePrivate if true, return both public and private posts
     * @return list of content
     */
    @GetMapping("/creator/{creatorId}")
    public List<Content> getContentByCreator(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "false") boolean includePrivate
    ) {
        return contentService.getContentByCreator(creatorId, includePrivate);
    }

    /**
     * Fetch a single content by UUID with optional user access validation.
     *
     * @param contentId content UUID
     * @param userId    requesting user ID (optional)
     * @return content details
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<Content> getContentById(
            @PathVariable UUID contentId,
            @RequestParam(required = false) Long userId
    ) {
        Content content = contentService.getContentById(contentId, userId);
        return ResponseEntity.ok(content);
    }

    /**
     * Moderate content by marking as reviewed with a reason.
     *
     * @param contentId   content UUID
     * @param requestDto  reason for moderation
     * @return success message
     */
    @PostMapping("/moderate/{contentId}")
    public ResponseEntity<String> moderateContent(
            @PathVariable UUID contentId,
            @RequestBody ModerationRequestDto requestDto
    ) {
        contentService.moderateContent(contentId, requestDto.getReason());
        return ResponseEntity.ok("Content moderated successfully.");
    }

    /**
     * Delete content by UUID.
     *
     * @param contentId content UUID
     * @return success message
     */
    @DeleteMapping("/{contentId}")
    public ResponseEntity<String> deleteContent(@PathVariable UUID contentId) {
        contentService.deleteContentById(contentId);
        return ResponseEntity.ok("Content deleted successfully.");
    }

    /**
     * Report a content post by user.
     *
     * @param dto report request payload
     * @return confirmation message
     */
    @PostMapping("/report")
    public ResponseEntity<String> reportContent(@RequestBody ReportRequestDto dto) {
        String result = contentService.reportContent(dto);
        return ResponseEntity.ok(result);
    }

}
