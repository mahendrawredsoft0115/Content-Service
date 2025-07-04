package com.project.content.controller;

import com.project.content.dto.ContentUploadResponse;
import com.project.content.entity.Content;
import com.project.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    //  Upload API
    @PostMapping("/upload")
    public ResponseEntity<ContentUploadResponse> uploadContent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("visibility") String visibility,
            @RequestParam("creatorId") Long creatorId) {
        try {
            ContentUploadResponse response = contentService.uploadFile(file, visibility, creatorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ContentUploadResponse(null, null, null, "Upload failed: " + e.getMessage()));
        }
    }

    //  Cleaned Fetch API
    @GetMapping("/creator/{creatorId}/posts")
    public ResponseEntity<List<Content>> getPostsByCreator(
            @PathVariable Long creatorId,
            @RequestParam(defaultValue = "false") boolean includePrivate) {

        List<Content> posts = contentService.getPostsByCreator(creatorId, includePrivate);
        return ResponseEntity.ok(posts);
    }




}
