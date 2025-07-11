package com.example.BookmarkApp;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthRepository extends JpaRepository<OauthUser, Long> {
    Optional<OauthUser> findByEmail(String email);

    Optional<OauthUser> findByUsername(String username);
}
