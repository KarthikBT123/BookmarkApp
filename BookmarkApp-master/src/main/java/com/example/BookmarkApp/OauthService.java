package com.example.BookmarkApp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OauthService {
    private final OauthRepository oauthRepository;
    private final OauthClient oauthClient;
    

    public OauthService(OauthRepository oauthRepository, OauthClient oauthClient) {
        this.oauthRepository = oauthRepository;
        this.oauthClient = oauthClient;
       
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
        OauthUser user = new OauthUser(username, password, email);
        OauthUser saved = oauthRepository.save(user);
        
        return saved;
    }

    public boolean authenticate(String username, String password) {
        return oauthRepository.findByUsernameAndPassword(username, password).isPresent();
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
}
