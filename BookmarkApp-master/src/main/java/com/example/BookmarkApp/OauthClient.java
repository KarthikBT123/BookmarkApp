package com.example.BookmarkApp;

import org.springframework.stereotype.Component;

@Component
public class OauthClient {
    /**
     * Build provider-specific authorization URL.
     * This is a stub and should be replaced with real provider configuration.
     */
    public String buildAuthorizationUrl(String provider) {
        return "https://" + provider + ".com/oauth/authorize?client_id=CLIENT_ID" +
               "&redirect_uri=http://localhost:8080/oauth/callback/" + provider +
               "&response_type=code&scope=profile%20email";
    }

    /**
     * Exchange authorization code for an access token.
     * Stubbed for demonstration.
     */
    public String exchangeCodeForAccessToken(String provider, String code) {
        // TODO implement HTTP call to provider token endpoint.
        return "mock-token-" + provider + "-" + code;
    }

    /**
     * Retrieve basic user info using the access token.
     * Stubbed for demonstration.
     */
    public OauthUser fetchUserInfo(String provider, String accessToken) {
        OauthUser user = new OauthUser();
        user.setUsername("demo-" + provider);
        user.setPassword(null); // password not provided by OAuth providers
        user.setEmail("demo@" + provider + ".com");
        return user;
    }
}