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
        String currentUser = getCurrentUsername();
        if (currentUser == null) {
            return Collections.emptyList();
        }
        
        String plan = oauthService.getPlanForUser(currentUser);
        int maxUsers = getMaxUsersForPlan(plan);
        
        // If unlimited (Ultra plan), allow access
        if (maxUsers == -1) {
            return bookmarkRepository.findByUsernameAndSecurityOption(username, "Public");
        }
        
        // Check if this user is within the allowed limit
        List<String> allowedUsers = bookmarkRepository.findBySecurityOption("Public").stream()
            .map(Bookmark::getUsername)
            .distinct()
            .limit(maxUsers)
            .toList();
            
        if (!allowedUsers.contains(username)) {
            return Collections.emptyList(); // User not in allowed list
        }
        
        return bookmarkRepository.findByUsernameAndSecurityOption(username, "Public");
    }
    
    // Search for users' public bookmarks by username pattern - requires Pro/Ultra access
    public List<Bookmark> searchPublicBookmarksByUsername(String searchTerm) {
        String currentUser = getCurrentUsername();
        if (currentUser == null) {
            return Collections.emptyList();
        }
        
        String plan = oauthService.getPlanForUser(currentUser);
        int maxUsers = getMaxUsersForPlan(plan);
        
        String escaped = escapeLikeWildcards(searchTerm);
        String pattern = "%" + escaped.toLowerCase() + "%"; // case-insensitive handled by query LOWER
        List<Bookmark> searchResults = bookmarkRepository.findPublicBookmarksByUsernameContaining(pattern);
        
        // If unlimited (Ultra plan), return all results
        if (maxUsers == -1) {
            return searchResults;
        }
        
        // Apply user viewing limits
        List<String> limitedUsers = searchResults.stream()
            .map(Bookmark::getUsername)
            .distinct()
            .limit(maxUsers)
            .toList();
            
        return searchResults.stream()
            .filter(bookmark -> limitedUsers.contains(bookmark.getUsername()))
            .toList();
    }
    
    // Get all public bookmarks - requires Pro/Ultra access
    public List<Bookmark> getAllPublicBookmarks() {
        String username = getCurrentUsername();
        if (username == null) {
            return Collections.emptyList();
        }
        
        String plan = oauthService.getPlanForUser(username);
        List<Bookmark> allPublicBookmarks = bookmarkRepository.findBySecurityOption("Public");
        
        // Apply user viewing limits based on plan
        int maxUsers = getMaxUsersForPlan(plan);
        if (maxUsers == -1) {
            return allPublicBookmarks; // Unlimited for Ultra
        }
        
        // Get distinct users and limit them
        List<String> limitedUsers = allPublicBookmarks.stream()
            .map(Bookmark::getUsername)
            .distinct()
            .limit(maxUsers)
            .toList();
            
        // Return bookmarks only from allowed users
        return allPublicBookmarks.stream()
            .filter(bookmark -> limitedUsers.contains(bookmark.getUsername()))
            .toList();
    }
    
    private int getMaxUsersForPlan(String plan) {
        return switch (plan) {
            case "Free" -> 5;
            case "Pro" -> 15;
            case "Ultra" -> -1; // Unlimited
            default -> 5; // Default to Free limits
        };
    }
    
    private String getCurrentUsername() {
        try {
            return (String) VaadinSession.getCurrent().getAttribute("username");
        } catch (Exception e) {
            return null;
        }
    }
    
    // Escape % and _ for LIKE queries
    private String escapeLikeWildcards(String term) {
        if (term == null) return "";
        return term.replace("%", "\\%")
                   .replace("_", "\\_");
    }
}


