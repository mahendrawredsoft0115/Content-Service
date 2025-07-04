package com.project.content.controller;

import com.project.content.entity.Content;

import com.project.content.service.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Content>> getFeed(@PathVariable Long userId) {
        List<Content> feed = feedService.getFeedForUser(userId);
        return ResponseEntity.ok(feed);
    }
}
