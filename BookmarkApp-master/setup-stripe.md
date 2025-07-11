# 🚀 Stripe Integration Setup Guide

This guide will help you set up Stripe payments for your BookmarkApp.

## 📋 Prerequisites

1. A Stripe account (sign up at [https://stripe.com](https://stripe.com))
2. Java 17+ installed
3. Maven installed

## 🔧 Step 1: Get Your Stripe Keys

### For Testing (Use Test Keys)
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)
2. Copy your **Publishable key** (starts with `pk_test_`)
3. Copy your **Secret key** (starts with `sk_test_`)

### For Production (Use Live Keys)
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/apikeys)
2. Copy your **Publishable key** (starts with `pk_live_`)
3. Copy your **Secret key** (starts with `sk_live_`)

## 🎯 Step 2: Create Products and Prices in Stripe

1. Go to [Products](https://dashboard.stripe.com/test/products) in your Stripe Dashboard
2. Create two products:

### Pro Plan Product
- **Name**: Pro Plan
- **Description**: Advanced bookmark features
- **Pricing**: $9.99/month (recurring)
- Copy the **Price ID** (starts with `price_`)

### Ultra Plan Product  
- **Name**: Ultra Plan
- **Description**: Premium bookmark features with unlimited access
- **Pricing**: $19.99/month (recurring)
- Copy the **Price ID** (starts with `price_`)

## 🔐 Step 3: Set Environment Variables

### Windows (PowerShell)
```powershell
# Set your Stripe keys (replace with your actual keys)
setx STRIPE_SECRET_KEY "sk_test_your_secret_key_here"
setx STRIPE_PUBLISHABLE_KEY "pk_test_your_publishable_key_here"
setx STRIPE_PRICE_PRO "price_your_pro_price_id_here"
setx STRIPE_PRICE_ULTRA "price_your_ultra_price_id_here"

# Restart your terminal/IDE after setting these
```

### macOS/Linux (Bash)
```bash
# Add to your ~/.bashrc or ~/.zshrc
export STRIPE_SECRET_KEY="sk_test_your_secret_key_here"
export STRIPE_PUBLISHABLE_KEY="pk_test_your_publishable_key_here"
export STRIPE_PRICE_PRO="price_your_pro_price_id_here"
export STRIPE_PRICE_ULTRA="price_your_ultra_price_id_here"

# Reload your shell
source ~/.bashrc
```

## 🪝 Step 4: Set Up Webhooks

### During Development (Using Stripe CLI)
1. Install [Stripe CLI](https://stripe.com/docs/stripe-cli)
2. Login: `stripe login`
3. Forward webhooks to your local app:
   ```bash
   stripe listen --forward-to localhost:8080/api/payments/webhook
   ```
4. Copy the webhook signing secret (starts with `whsec_`) and set it:
   ```bash
   # Windows
   setx STRIPE_WEBHOOK_SECRET "whsec_your_webhook_secret_here"
   
   # macOS/Linux
   export STRIPE_WEBHOOK_SECRET="whsec_your_webhook_secret_here"
   ```

### For Production
1. Go to [Webhooks](https://dashboard.stripe.com/webhooks) in Stripe Dashboard
2. Click "Add endpoint"
3. Set endpoint URL: `https://yourdomain.com/api/payments/webhook`
4. Select these events:
   - `checkout.session.completed`
   - `customer.subscription.created`
   - `customer.subscription.updated` 
   - `customer.subscription.deleted`
   - `invoice.payment_succeeded`
   - `invoice.payment_failed`
5. Copy the webhook signing secret and set the environment variable

## 🏃‍♂️ Step 5: Run the Application

```bash
# Navigate to your project directory
cd BookmarkApp-master

# Run the application
mvn spring-boot:run
```

## ✅ Step 6: Test the Integration

1. Navigate to `http://localhost:8080/plans`
2. Try upgrading to Pro or Ultra plan
3. Use Stripe's test card numbers:
   - **Success**: `4242424242424242`
   - **Declined**: `4000000000000002`
   - **3D Secure**: `4000002500003155`

## 🛠️ Troubleshooting

### Common Issues

**Error: "No such price"**
- Make sure your price IDs are correct in environment variables
- Verify the prices exist in your Stripe dashboard

**Error: "Invalid API key"**
- Check that your secret key is set correctly
- Make sure you're using the right key for test/live mode

**Webhook not working**
- Verify the webhook secret is set correctly
- Check that the webhook endpoint is accessible
- Look at webhook delivery logs in Stripe Dashboard

**Payment not updating user plan**
- Check application logs for webhook processing errors
- Verify the user's email matches between app and Stripe

### Debug Mode
Add this to `application.properties` for more detailed logging:
```properties
logging.level.com.example.BookmarkApp=DEBUG
logging.level.com.stripe=DEBUG
```

## 🎉 Features Available

Once configured, users can:

✅ **Upgrade to Pro/Ultra plans**
- Secure Stripe Checkout integration
- Automatic plan activation via webhooks

✅ **Manage subscriptions**
- Access to Stripe Customer Portal
- Cancel, pause, or change plans
- Update payment methods

✅ **Premium Features Access**
- Pro: Advanced search, team collaboration, AI features
- Ultra: Unlimited everything, API access, priority support

## 🔒 Security Features

- CSRF protection enabled (except for webhooks)
- Webhook signature verification
- Secure environment variable handling
- Content Security Policy headers
- HTTPS enforcement (in production)

## 📞 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review Stripe Dashboard logs
3. Check application logs
4. Consult [Stripe Documentation](https://stripe.com/docs)

---

**⚠️ Important Security Notes:**
- Never commit API keys to version control
- Use test keys during development
- Always verify webhooks with signature verification
- Set up proper HTTPS in production 