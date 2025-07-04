package com.project.content.repository;

import com.project.content.entity.Content;
import com.project.content.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCreatorId(Long creatorId);
    List<Content> findByCreatorIdAndVisibility(Long creatorId, Visibility visibility);
}
