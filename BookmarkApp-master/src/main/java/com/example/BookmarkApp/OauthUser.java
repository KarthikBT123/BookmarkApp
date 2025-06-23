package com.example.BookmarkApp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "oauth_users")
public class OauthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)
    private String username;
    
    @Column(length = 255)
    private String password;
    
    @Column(unique = true, length = 255)
    private String email;
    
    @Column(length = 20)
    private String plan = "Free"; // subscription plan

    // --- constructors ------------------------------------------------------
    public OauthUser() {
    }

    public OauthUser(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // --- getters & setters -------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}