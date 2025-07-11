package com.example.BookmarkApp;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    private final OauthService oauthService;

    @Value("${stripe.api.secret-key}")
    private String secretKey;

    @Value("${stripe.price.pro}")
    private String proPriceId;

    @Value("${stripe.price.ultra}")
    private String ultraPriceId;

    @Value("${stripe.payment-link.pro}")
    private String proPaymentLink;

    @Value("${stripe.payment-link.ultra}")
    private String ultraPaymentLink;

    public StripeService(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    /**
     * Get payment link for direct Stripe payment
     */
    public String getPaymentLink(String planName) {
        switch (planName.toLowerCase()) {
            case "pro":
                return proPaymentLink;
            case "ultra":
                return ultraPaymentLink;
            default:
                throw new IllegalArgumentException("Invalid plan: " + planName);
        }
    }

    /**
     * Create a checkout session with custom success/cancel URLs
     * This gives you full control over the redirect flow
     */
    public String createCheckoutSession(String planName, String customerEmail, String baseUrl, String username) throws StripeException {
        String priceId = planName.toLowerCase().equals("pro") ? proPriceId : ultraPriceId;
        
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)  // Changed to SUBSCRIPTION for recurring payments
            .setSuccessUrl(baseUrl + "/api/stripe/payment-success?session_id={CHECKOUT_SESSION_ID}&plan=" + planName + "&user=" + username)
            .setCancelUrl(baseUrl + "/plans")  // Fixed URL path
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPrice(priceId)
                    .build()
            )
            .setCustomerEmail(customerEmail)
            .build();

        com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.create(params);
        return session.getUrl();
    }

    /**
     * Process webhook events from Stripe
     */
    public void processWebhookEvent(Event event) {
        // Handle different event types
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "invoice.payment_succeeded":
                handlePaymentSucceeded(event);
                break;
            case "customer.subscription.created":
                handleSubscriptionCreated(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        // Implementation for successful checkout
        System.out.println("Checkout session completed: " + event.getId());
    }

    private void handlePaymentSucceeded(Event event) {
        // Implementation for successful payment
        System.out.println("Payment succeeded: " + event.getId());
    }

    private void handleSubscriptionCreated(Event event) {
        // Implementation for new subscription
        System.out.println("Subscription created: " + event.getId());
    }
} 