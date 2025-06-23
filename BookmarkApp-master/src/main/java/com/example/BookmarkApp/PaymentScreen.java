
package com.example.BookmarkApp;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("pay")
@SpringComponent
@UIScope
public class PaymentScreen extends VerticalLayout implements HasUrlParameter<String> {
    private final OauthService oauthService;
    private String planName;

    @Autowired
    public PaymentScreen(OauthService oauthService) {
        this.oauthService = oauthService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        // Enhanced gradient background
        UI.getCurrent().getElement().executeJs(
            "document.body.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';" +
            "document.body.style.minHeight = '100vh';"
        );
        
        getStyle()
            .set("background", "linear-gradient(145deg, #ffffff 0%, #f8fafc 100%)")
            .set("border-radius", "24px")
            .set("box-shadow", "0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1)")
            .set("padding", "48px 40px")
            .set("max-width", "480px")
            .set("margin", "40px auto")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.2)")
            .set("backdrop-filter", "blur(20px)");
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        String query = event.getLocation().getQueryParameters().getParameters().getOrDefault("plan", java.util.List.of()).stream().findFirst().orElse(null);
        this.planName = query;
        removeAll();
        if (planName == null || planName.isBlank()) {
            H1 errorTitle = new H1("❌ No Plan Selected");
            errorTitle.getStyle()
                .set("color", "#e53e3e")
                .set("font-size", "28px")
                .set("font-weight", "700")
                .set("text-align", "center")
                .set("margin", "0");
            add(errorTitle);
            return;
        }
        buildUI();
    }

    private void buildUI() {
        // Header with icon
        Icon paymentIcon = VaadinIcon.CREDIT_CARD.create();
        paymentIcon.setSize("48px");
        paymentIcon.getStyle().set("color", "#667eea");
        
        H1 title = new H1("💳 Secure Payment");
        title.getStyle()
            .set("color", "#1a202c")
            .set("font-size", "32px")
            .set("font-weight", "800")
            .set("margin", "16px 0 8px 0")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Upgrade to " + planName + " Plan");
        subtitle.getStyle()
            .set("color", "#667eea")
            .set("font-size", "18px")
            .set("font-weight", "600")
            .set("text-align", "center")
            .set("margin-bottom", "32px")
            .set("display", "block");

        // Card input fields with enhanced styling
        TextField cardNumber = new TextField();
        cardNumber.setLabel("Card Number");
        cardNumber.setPlaceholder("1234 5678 9012 3456");
        cardNumber.setPrefixComponent(VaadinIcon.CREDIT_CARD.create());
        styleTextField(cardNumber);

        HorizontalLayout cardRow = new HorizontalLayout();
        cardRow.setWidthFull();
        cardRow.setSpacing(true);

        TextField expiry = new TextField();
        expiry.setLabel("Expiry Date");
        expiry.setPlaceholder("MM/YY");
        expiry.setPrefixComponent(VaadinIcon.CALENDAR.create());
        styleTextField(expiry);
        expiry.setWidth("160px");

        TextField cvc = new TextField();
        cvc.setLabel("CVC");
        cvc.setPlaceholder("123");
        cvc.setPrefixComponent(VaadinIcon.LOCK.create());
        styleTextField(cvc);
        cvc.setWidth("120px");

        cardRow.add(expiry, cvc);

        // Enhanced payment button
        Button payBtn = new Button("Complete Payment", VaadinIcon.ARROW_RIGHT.create());
        payBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        payBtn.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("color", "#ffffff")
            .set("border", "none")
            .set("border-radius", "12px")
            .set("font-weight", "600")
            .set("font-size", "16px")
            .set("padding", "16px 32px")
            .set("width", "100%")
            .set("margin-top", "24px")
            .set("cursor", "pointer")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)");

        payBtn.getElement().addEventListener("mouseenter", e -> 
            payBtn.getStyle().set("transform", "translateY(-2px)")
                .set("box-shadow", "0 12px 35px rgba(102, 126, 234, 0.5)")
        );
        
        payBtn.getElement().addEventListener("mouseleave", e -> 
            payBtn.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
        );

        payBtn.addClickListener(e -> handleFakePayment(cardNumber.getValue(), expiry.getValue(), cvc.getValue()));

        // Security notice
        HorizontalLayout securityNotice = new HorizontalLayout();
        securityNotice.setAlignItems(Alignment.CENTER);
        securityNotice.setJustifyContentMode(JustifyContentMode.CENTER);
        Icon shieldIcon = VaadinIcon.SHIELD.create();
        shieldIcon.setSize("16px");
        shieldIcon.getStyle().set("color", "#38a169");
        Span securityText = new Span("Your payment information is secure and encrypted");
        securityText.getStyle()
            .set("color", "#4a5568")
            .set("font-size", "14px")
            .set("margin-left", "8px");
        securityNotice.add(shieldIcon, securityText);
        securityNotice.getStyle().set("margin-top", "16px");

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setPadding(false);
        content.setSpacing(true);
        content.add(paymentIcon, title, subtitle, cardNumber, cardRow, payBtn, securityNotice);

        add(content);
    }

    private void styleTextField(TextField field) {
        field.getStyle()
            .set("width", "100%")
            .set("margin-bottom", "16px");
        
        field.getElement().executeJs("""
            const input = this.shadowRoot.querySelector('input');
            const label = this.shadowRoot.querySelector('label');
            if (input) {
                input.style.padding = '16px';
                input.style.borderRadius = '12px';
                input.style.border = '2px solid #e2e8f0';
                input.style.fontSize = '16px';
                input.style.transition = 'all 0.3s ease';
                input.style.backgroundColor = '#ffffff';
            }
            if (label) {
                label.style.fontSize = '14px';
                label.style.fontWeight = '600';
                label.style.color = '#4a5568';
                label.style.marginBottom = '8px';
            }
            this.addEventListener('focus', () => {
                if (input) {
                    input.style.borderColor = '#667eea';
                    input.style.boxShadow = '0 0 0 3px rgba(102, 126, 234, 0.1)';
                }
            });
            this.addEventListener('blur', () => {
                if (input) {
                    input.style.borderColor = '#e2e8f0';
                    input.style.boxShadow = 'none';
                }
            });
        """);
    }

    private void handleFakePayment(String card, String expiry, String cvc) {
        if (card == null || card.length() < 8 || expiry == null || expiry.isBlank() || cvc == null || cvc.length() < 3) {
            Notification.show("Please enter valid payment details.", 3000, Notification.Position.MIDDLE);
            return;
        }
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        if (username != null && planName != null) {
            oauthService.updatePlan(username, planName);
            Notification.show("Payment successful! Plan upgraded to " + planName + ".", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("mainscreen");
        } else {
            Notification.show("Error: Could not update plan.", 3000, Notification.Position.MIDDLE);
        }
    }
}
