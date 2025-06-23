package com.example.BookmarkApp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BookmarkService bookmarkService;
    
    @Autowired
    private OauthService oauthService;
    
    @Override
    public void run(String... args) throws Exception {
        // Update existing bookmarks without username to assign them to a default user
        List<Bookmark> allBookmarks = bookmarkService.getAllBookmarks();
        for (Bookmark bookmark : allBookmarks) {
            if (bookmark.getUsername() == null || "unknown".equals(bookmark.getUsername())) {
                bookmark.setUsername("admin"); // Assign to a default user
                bookmarkService.addBookmark(bookmark);
            }
        }
        
        System.out.println("Data initialization completed!");
    }
} 