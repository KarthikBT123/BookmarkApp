package com.example.BookmarkApp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 2048)
    private String url;
    
    @Column(name = "display_name", length = 255)
    private String displayName;
    
    @Column(name = "tags_input", length = 500)
    private String tagsInput;
    
    @Column(name = "favorite_option", length = 10)
    private String favoriteOption;
    
    @Column(name = "security_option", length = 20)
    private String securityOption;
    
    @Column(length = 100)
    private String category;
    
    @Column(length = 100)
    private String username; // Owner of the bookmark

    public Bookmark() {
    }
    
    // Old constructor for backward compatibility
    public Bookmark(String url, String displayName, String tagsInput, String favoriteOption, String securityOption, String category) {
        this.url = url;
        this.displayName = displayName;
        this.tagsInput = tagsInput;
        this.favoriteOption = favoriteOption;
        this.securityOption = securityOption;
        this.category = category;
        this.username = "unknown"; // Default value for existing data
    }
    
    // New constructor with username
    public Bookmark(String url, String displayName, String tagsInput, String favoriteOption, String securityOption, String category, String username) {
        this.url = url;
        this.displayName = displayName;
        this.tagsInput = tagsInput;
        this.favoriteOption = favoriteOption;
        this.securityOption = securityOption;
        this.category = category;
        this.username = username;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getTagsInput() {
        return tagsInput;
    }
    public void setTagsInput(String tagsInput) {
        this.tagsInput = tagsInput;
    }
    public String getFavoriteOption() {
        return favoriteOption;
    }
    public void setFavoriteOption(String favoriteOption) {
        this.favoriteOption = favoriteOption;
    }
    public String getSecurityOption() {
        return securityOption;
    }
    public void setSecurityOption(String securityOption) {
        this.securityOption = securityOption;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public String getCategory(){
        return category;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}   

