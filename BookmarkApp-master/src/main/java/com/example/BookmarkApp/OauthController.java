package com.example.BookmarkApp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OauthController {
    private final OauthService oauthService;

    public OauthController(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    /**
     * Step 1 – start OAuth flow.
     * Redirect the user to the provider's authorization screen.
     */
    @GetMapping("/oauth/login/{provider}")
    public ResponseEntity<Void> login(@PathVariable String provider) {
        String redirectUrl = oauthService.getAuthorizationUrl(provider);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * Step 2 – provider calls back with ?code=…
     * Exchange the code and persist or return the user.
     */
    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<OauthUser> callback(@PathVariable String provider, @RequestParam String code) {
        OauthUser user = oauthService.handleCallback(provider, code);
        return ResponseEntity.ok(user);
    }
}
