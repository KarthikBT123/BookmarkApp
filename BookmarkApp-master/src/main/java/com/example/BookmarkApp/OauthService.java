package com.example.BookmarkApp;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OauthService {
    private final OauthRepository oauthRepository;
    private final OauthClient oauthClient;
    private final PasswordEncoder passwordEncoder;
    

    public OauthService(OauthRepository oauthRepository, OauthClient oauthClient, PasswordEncoder passwordEncoder) {
        this.oauthRepository = oauthRepository;
        this.oauthClient = oauthClient;
        this.passwordEncoder = passwordEncoder;
    }

    public String getAuthorizationUrl(String provider) {
        return oauthClient.buildAuthorizationUrl(provider);
    }

    public OauthUser handleCallback(String provider, String code) {
        String token = oauthClient.exchangeCodeForAccessToken(provider, code);
        OauthUser fetched = oauthClient.fetchUserInfo(provider, token);
        return oauthRepository
                .findByEmail(fetched.getEmail())
                .orElseGet(() -> oauthRepository.save(fetched));
    }

    // ------------------ manual signup / login -----------------------
    @Transactional
    public OauthUser signUp(String username, String password, String email) throws IllegalArgumentException {
        // basic duplicate check on username or email
        if (oauthRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (oauthRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Encrypt the password before saving
        String encryptedPassword = passwordEncoder.encode(password);
        OauthUser user = new OauthUser(username, encryptedPassword, email);
        OauthUser saved = oauthRepository.save(user);
        
        return saved;
    }

    public boolean authenticate(String username, String password) {
        return authenticateWithMigration(username, password);
    }

    /**
     * Authenticate with automatic migration from plain text to encrypted passwords.
     * This method handles both existing plain text passwords and new encrypted passwords.
     */
    private boolean authenticateWithMigration(String username, String password) {
        Optional<OauthUser> userOpt = oauthRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        OauthUser user = userOpt.get();
        String storedPassword = user.getPassword();
        
        // Check if password is already encrypted (BCrypt hashes start with $2a$, $2b$, or $2y$)
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            // Password is already encrypted, use normal verification
            return passwordEncoder.matches(password, storedPassword);
        } else {
            // Password is plain text, check directly and then encrypt
            if (password.equals(storedPassword)) {
                // Migrate the password to encrypted format
                user.setPassword(passwordEncoder.encode(password));
                oauthRepository.save(user);
                return true;
            } else {
                return false;
            }
        }
    }

    public String getPlanForUser(String username) {
        return oauthRepository.findByUsername(username)
                .map(OauthUser::getPlan)
                .orElse("Free");
    }

    public void updatePlan(String username, String newPlan) {
        oauthRepository.findByUsername(username).ifPresent(user -> {
            user.setPlan(newPlan);
            oauthRepository.save(user);
        });
    }
    
    // Get all usernames for search functionality
    public List<String> getAllUsernames() {
        return oauthRepository.findAll().stream()
                .map(OauthUser::getUsername)
                .collect(Collectors.toList());
    }
    
    // Get user's email by username
    public String getEmailForUser(String username) {
        return oauthRepository.findByUsername(username)
                .map(OauthUser::getEmail)
                .orElse(null);
    }
    
    /**
     * Utility method to reset a user's password (for debugging/admin purposes)
     */
    @Transactional
    public boolean resetPassword(String username, String newPassword) {
        Optional<OauthUser> userOpt = oauthRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            OauthUser user = userOpt.get();
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            oauthRepository.save(user);
            return true;
        }
        return false;
    }
}
