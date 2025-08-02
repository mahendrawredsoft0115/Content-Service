package com.project.content.controller;

import com.project.content.dto.ContentUploadResponse;
import com.project.content.dto.ModerationRequestDto;
import com.project.content.entity.Content;
import com.project.content.enums.ContentType;
import com.project.content.enums.FileType;
import com.project.content.enums.Visibility;
import com.project.content.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing content-related endpoints.
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

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
    @GetMapping("/creator/{creatorId}/posts")
    public List<Content> getContentByCreator(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "false") boolean includePrivate
    ) {
        return contentService.getContentByCreator(creatorId, includePrivate);
    }


    /**
     * Fetch a content by its UUID.
     *
     * @param contentId content ID (UUID)
     * @return Content entity
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<Content> getContentById(
            @PathVariable UUID contentId,
            @RequestParam(required = false) Long userId
    ) {
        Content content = contentService.getContentById(contentId, userId);
        return ResponseEntity.ok(content);
    }



//    @PatchMapping("/moderate/{contentId}")
@PostMapping("/moderate/{contentId}")
    public ResponseEntity<String> moderateContent(
            @PathVariable UUID contentId,
            @RequestBody ModerationRequestDto requestDto) {
        contentService.moderateContent(contentId, requestDto.getReason());
        return ResponseEntity.ok("Content moderated successfully");
    }


    @DeleteMapping("/{contentId}")
    public ResponseEntity<String> deleteContent(@PathVariable UUID contentId) {
        contentService.deleteContentById(contentId);
        return ResponseEntity.ok("Content deleted successfully.");
    }



}
