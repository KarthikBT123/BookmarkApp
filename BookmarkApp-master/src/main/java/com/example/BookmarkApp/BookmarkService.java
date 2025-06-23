package com.example.BookmarkApp;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.flow.server.VaadinSession;

@Service
public class BookmarkService {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    
    @Autowired
    private OauthService oauthService;
    
    public void addBookmark(Bookmark bookmark) {
        bookmarkRepository.save(bookmark);
    }
    
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }
    
    // Get bookmarks for a specific user
    public List<Bookmark> getBookmarksByUsername(String username) {
        return bookmarkRepository.findByUsername(username);
    }
    
    // Get public bookmarks for a specific user - requires Pro/Ultra access
    public List<Bookmark> getPublicBookmarksByUsername(String username) {
        if (!hasProOrUltraAccess()) {
            return Collections.emptyList();
        }
        return bookmarkRepository.findByUsernameAndSecurityOption(username, "Public");
    }
    
    // Search for users' public bookmarks by username pattern - requires Pro/Ultra access
    public List<Bookmark> searchPublicBookmarksByUsername(String searchTerm) {
        if (!hasProOrUltraAccess()) {
            return Collections.emptyList();
        }
        return bookmarkRepository.findPublicBookmarksByUsernameContaining(searchTerm);
    }
    
    // Get all public bookmarks - requires Pro/Ultra access
    public List<Bookmark> getAllPublicBookmarks() {
        if (!hasProOrUltraAccess()) {
            return Collections.emptyList();
        }
        return bookmarkRepository.findBySecurityOption("Public");
    }
    
    // Helper method to check if current user has Pro or Ultra access
    private boolean hasProOrUltraAccess() {
        try {
            String username = (String) VaadinSession.getCurrent().getAttribute("username");
            if (username == null) {
                return false;
            }
            String plan = oauthService.getPlanForUser(username);
            return "Pro".equals(plan) || "Ultra".equals(plan);
        } catch (Exception e) {
            // If there's any issue getting the session or plan, deny access
            return false;
        }
    }
}


