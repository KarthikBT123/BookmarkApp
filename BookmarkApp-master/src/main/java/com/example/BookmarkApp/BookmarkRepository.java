package com.example.BookmarkApp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository <Bookmark, Long>{

    // Find bookmarks by username
    List<Bookmark> findByUsername(String username);
    
    // Find public bookmarks by username
    List<Bookmark> findByUsernameAndSecurityOption(String username, String securityOption);
    
    // Find all public bookmarks
    List<Bookmark> findBySecurityOption(String securityOption);
    
    // Find public bookmarks by username containing search term
    @Query("SELECT b FROM Bookmark b WHERE b.username LIKE %:searchTerm% AND b.securityOption = 'Public'")
    List<Bookmark> findPublicBookmarksByUsernameContaining(@Param("searchTerm") String searchTerm);
}
