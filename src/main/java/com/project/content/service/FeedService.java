package com.project.content.service;

import com.project.content.entity.Content;
import com.project.content.entity.Follow;
import com.project.content.enums.Visibility;
import com.project.content.repository.ContentRepository;
import com.project.content.exception.ContentNotFoundException;
import com.project.content.repository.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FeedService {

    private final FollowRepository followRepository;
    private final ContentRepository contentRepository;

    public FeedService(FollowRepository followRepository, ContentRepository contentRepository) {
        this.followRepository = followRepository;
        this.contentRepository = contentRepository;
    }

    public List<Content> getFeedForUser(Long userId) {
        List<Follow> followed = followRepository.findByUserId(userId);

        if (followed.isEmpty()) {
            throw new ContentNotFoundException("User is not following any creators.");
        }

        List<Content> feed = new ArrayList<>();

        for (Follow f : followed) {
            List<Content> creatorPosts = contentRepository
                    .findByCreatorIdAndVisibility(f.getCreatorId(), Visibility.PUBLIC);
            feed.addAll(creatorPosts);
        }

        // Sort by uploadedAt desc
        feed.sort(Comparator.comparing(Content::getUploadedAt).reversed());

        return feed;
    }
}
