package com.project.content.repository;

import com.project.content.entity.Content;
import com.project.content.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    List<Content> findByCreatorIdAndVisibility(Long creatorId, Visibility visibility);
    List<Content> findByCreatorId(Long creatorId);
    Optional<Content> findById(UUID id);

}
