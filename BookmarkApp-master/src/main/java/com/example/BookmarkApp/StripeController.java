package com.example.BookmarkApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private final StripeService stripeService;
    private final OauthService oauthService;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @Autowired
    public StripeController(StripeService stripeService, OauthService oauthService) {
        this.stripeService = stripeService;
        this.oauthService = oauthService;
    }

    /**
     * Handle Stripe webhooks
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        try {
            // For now, we'll handle basic webhook processing
            // In a production environment, you should verify the webhook signature
            
            // Log the webhook received
            System.out.println("Stripe webhook received: " + payload);
            
            // TODO: Parse the webhook payload and handle different event types
            // For now, just acknowledge receipt
            
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook processing failed");
        }
    }

    /**
     * Handle successful payment redirects from Stripe
     * This endpoint receives users after they complete payment
     */
    @GetMapping("/payment-success")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam(required = false) String session_id,
            @RequestParam(required = false) String plan,
            @RequestParam(required = false) String user) {
        
        try {
            // Update the user's plan if user and plan parameters are provided
            if (user != null && plan != null) {
                oauthService.updatePlan(user, plan);
                System.out.println("Updated plan for user " + user + " to " + plan);
            }
            
            String successPage = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Payment Successful - BookmarkApp</title>" +
                "    <style>" +
                "        body {" +
                "            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;" +
                "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);" +
                "            margin: 0;" +
                "            padding: 40px 20px;" +
                "            min-height: 100vh;" +
                "            display: flex;" +
                "            align-items: center;" +
                "            justify-content: center;" +
                "        }" +
                "        .success-container {" +
                "            background: linear-gradient(145deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.85) 100%);" +
                "            border-radius: 24px;" +
                "            box-shadow: 0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1);" +
                "            padding: 48px 40px;" +
                "            max-width: 480px;" +
                "            text-align: center;" +
                "            border: 1px solid rgba(255,255,255,0.3);" +
                "            backdrop-filter: blur(20px);" +
                "        }" +
                "        .success-icon {" +
                "            font-size: 64px;" +
                "            margin-bottom: 24px;" +
                "        }" +
                "        .success-title {" +
                "            color: #1a202c;" +
                "            font-size: 32px;" +
                "            font-weight: 800;" +
                "            margin: 0 0 16px 0;" +
                "            letter-spacing: -0.5px;" +
                "        }" +
                "        .success-message {" +
                "            color: #4a5568;" +
                "            font-size: 18px;" +
                "            margin-bottom: 32px;" +
                "            line-height: 1.6;" +
                "        }" +
                "        .continue-btn {" +
                "            background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);" +
                "            color: white;" +
                "            border: none;" +
                "            border-radius: 12px;" +
                "            font-weight: 600;" +
                "            font-size: 16px;" +
                "            padding: 16px 32px;" +
                "            cursor: pointer;" +
                "            text-decoration: none;" +
                "            display: inline-block;" +
                "            transition: all 0.3s ease;" +
                "            box-shadow: 0 8px 25px rgba(56, 161, 105, 0.4);" +
                "        }" +
                "        .continue-btn:hover {" +
                "            transform: translateY(-2px);" +
                "            box-shadow: 0 12px 35px rgba(56, 161, 105, 0.5);" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='success-container'>" +
                "        <div class='success-icon'>🎉</div>" +
                "        <h1 class='success-title'>Payment Successful!</h1>" +
                "        <p class='success-message'>" +
                "            Thank you for your payment! Your subscription has been activated." +
                "            <br>You can now enjoy all the premium features." +
                "        </p>" +
                "        <a href='/mainscreen' class='continue-btn'>Continue to Dashboard</a>" +
                "    </div>" +
                "    <script>" +
                "        // Auto-redirect after 5 seconds" +
                "        setTimeout(function() {" +
                "            window.location.href = '/mainscreen';" +
                "        }, 5000);" +
                "    </script>" +
                "</body>" +
                "</html>";
            
            return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(successPage);
                
        } catch (Exception e) {
            System.err.println("Error handling payment success: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing payment success");
        }
    }
} 