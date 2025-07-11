# Stripe Environment Variables Setup Script
# Run this script as Administrator in PowerShell

Write-Host "Setting up Stripe environment variables..." -ForegroundColor Green

# Set Stripe API Keys
setx STRIPE_SECRET_KEY "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" /M
setx STRIPE_PUBLISHABLE_KEY "pk_live_51RbifxGKlvongX7kjGd2L3NFiGWZDZAPTWT6kbqXmMMZnCtx0tKKGfThdcttqWSI1q2qH3KLlbe2WN9TLsLAS4Xd00KLLucpyX" /M

# Set Price IDs for your products
setx STRIPE_PRICE_PRO "price_1RgpkWGKlvongX7kP6Ml6jf2" /M
setx STRIPE_PRICE_ULTRA "price_1RgplWGKlvongX7k7MUtrC3e" /M

# Webhook secret (you'll need to set this after setting up webhooks)
setx STRIPE_WEBHOOK_SECRET "" /M

Write-Host ""
Write-Host "✅ Environment variables have been set!" -ForegroundColor Green
Write-Host ""
Write-Host "⚠️  Important:" -ForegroundColor Yellow
Write-Host "1. Restart your IDE/terminal for changes to take effect"
Write-Host "2. Set up webhook endpoints in your Stripe Dashboard:"
Write-Host "   - Endpoint URL: https://your-domain.com/api/stripe/webhook"
Write-Host "   - Events to select:"
Write-Host "     • checkout.session.completed"
Write-Host "     • customer.subscription.created"
Write-Host "     • customer.subscription.updated"
Write-Host "     • customer.subscription.deleted"
Write-Host "     • invoice.payment_succeeded"
Write-Host "     • invoice.payment_failed"
Write-Host "3. Copy the webhook signing secret and run:"
Write-Host "   setx STRIPE_WEBHOOK_SECRET 'whsec_your_webhook_secret_here' /M"
Write-Host ""
Write-Host "🚀 Your Stripe integration is ready!" -ForegroundColor Green 