package com.project.content.service;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Simulated access service to check if a user has access
 * to paid content via subscription or pay-per-view.
 */
@Service
public class UserAccessService {

    public boolean hasAccess(Long userId, UUID contentId) {
        // 👉 Simulated logic (replace this later with real subscription/payment checks)
        return userId != null && userId % 2 == 0; // e.g., even user IDs have access
    }
}
