
package com.example.BookmarkApp;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.server.VaadinSession;

@Route("signup")
@SpringComponent
@UIScope
public class SignUpScreen extends VerticalLayout {

    private final OauthService oauthService;

    public SignUpScreen(OauthService oauthService) {
        this.oauthService = oauthService;
        buildUI();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Enhanced gradient background
        UI.getCurrent().getElement().executeJs(
            "document.body.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';" +
            "document.body.style.minHeight = '100vh';"
        );

        // Enhanced card styling with glassmorphism
        getStyle()
            .set("background", "linear-gradient(145deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.85) 100%)")
            .set("border-radius", "24px")
            .set("box-shadow", "0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1)")
            .set("padding", "48px 40px")
            .set("margin", "40px auto")
            .set("max-width", "480px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)");
    }

    private void buildUI() {
        // Header with icon
        Icon signupIcon = VaadinIcon.USER.create();
        signupIcon.setSize("56px");
        signupIcon.getStyle()
            .set("color", "#667eea")
            .set("margin-bottom", "16px");

        H1 title = new H1("🚀 Create Account");
        title.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0 0 8px 0")
            .set("color", "#1a202c")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Join thousands of users organizing their bookmarks");
        subtitle.getStyle()
            .set("color", "#64748b")
            .set("font-size", "16px")
            .set("text-align", "center")
            .set("margin-bottom", "32px")
            .set("display", "block")
            .set("font-weight", "400");

        // Enhanced form fields
        TextField usernameField = new TextField();
        usernameField.setLabel("Username");
        usernameField.setPlaceholder("Choose a unique username");
        usernameField.setPrefixComponent(VaadinIcon.USER.create());
        usernameField.setRequiredIndicatorVisible(true);
        styleFormField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.setPlaceholder("Create a secure password");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setRequiredIndicatorVisible(true);
        styleFormField(passwordField);

        EmailField emailField = new EmailField();
        emailField.setLabel("Email Address");
        emailField.setPlaceholder("Enter your email address");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setRequiredIndicatorVisible(true);
        styleFormField(emailField);

        // Enhanced signup button
        Button signUpButton = new Button("Create Account", VaadinIcon.ARROW_RIGHT.create());
        signUpButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        signUpButton.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
            .set("padding", "16px 32px")
            .set("font-size", "16px")
            .set("width", "100%")
            .set("margin-top", "8px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");

        signUpButton.getElement().addEventListener("mouseenter", e -> 
            signUpButton.getStyle().set("transform", "translateY(-2px)")
                .set("box-shadow", "0 12px 35px rgba(102, 126, 234, 0.5)")
        );
        
        signUpButton.getElement().addEventListener("mouseleave", e -> 
            signUpButton.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
        );

        signUpButton.addClickListener(e -> handleSignUp(usernameField.getValue(), passwordField.getValue(), emailField.getValue()));

        // Terms notice
        Span termsNotice = new Span("By creating an account, you agree to our Terms of Service and Privacy Policy");
        termsNotice.getStyle()
            .set("color", "#64748b")
            .set("font-size", "13px")
            .set("text-align", "center")
            .set("margin-top", "16px")
            .set("line-height", "1.4")
            .set("display", "block");

        // Divider
        HorizontalLayout divider = new HorizontalLayout();
        divider.setWidthFull();
        divider.setAlignItems(Alignment.CENTER);
        divider.setJustifyContentMode(JustifyContentMode.CENTER);
        divider.getStyle().set("margin", "24px 0 16px 0");
        
        Span dividerLine1 = new Span();
        dividerLine1.getStyle()
            .set("height", "1px")
            .set("background", "linear-gradient(to right, transparent, #e2e8f0, transparent)")
            .set("flex", "1");
        
        Span dividerText = new Span("or");
        dividerText.getStyle()
            .set("color", "#94a3b8")
            .set("font-size", "14px")
            .set("margin", "0 16px")
            .set("font-weight", "500");
        
        Span dividerLine2 = new Span();
        dividerLine2.getStyle()
            .set("height", "1px")
            .set("background", "linear-gradient(to left, transparent, #e2e8f0, transparent)")
            .set("flex", "1");
        
        divider.add(dividerLine1, dividerText, dividerLine2);

        // Enhanced login link
        Button toLogin = new Button("Already have an account? Sign in", VaadinIcon.SIGN_IN.create());
        toLogin.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        toLogin.getStyle()
            .set("color", "#667eea")
            .set("font-weight", "600")
            .set("width", "100%")
            .set("padding", "16px 32px")
            .set("border-radius", "12px")
            .set("font-size", "16px")
            .set("transition", "all 0.3s ease")
            .set("border", "2px solid transparent")
            .set("background", "rgba(102, 126, 234, 0.05)");

        toLogin.getElement().addEventListener("mouseenter", e -> 
            toLogin.getStyle().set("background", "rgba(102, 126, 234, 0.1)")
                .set("border-color", "rgba(102, 126, 234, 0.2)")
        );
        
        toLogin.getElement().addEventListener("mouseleave", e -> 
            toLogin.getStyle().set("background", "rgba(102, 126, 234, 0.05)")
                .set("border-color", "transparent")
        );

        toLogin.addClickListener(e -> UI.getCurrent().navigate(""));

        VerticalLayout card = new VerticalLayout();
        card.setSpacing(true);
        card.setPadding(false);
        card.setAlignItems(Alignment.CENTER);
        card.add(signupIcon, title, subtitle, usernameField, passwordField, emailField, signUpButton, termsNotice, divider, toLogin);

        add(card);
    }

    private void styleFormField(com.vaadin.flow.component.HasElement field) {
        field.getElement().getStyle().set("width", "100%").set("margin-bottom", "16px");
        
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
                label.style.color = '#374151';
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

    private void handleSignUp(String username, String password, String email) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            Notification.show("All fields (username, password, email) are required.", 4000, Notification.Position.MIDDLE);
            return;
        }
        try {
            oauthService.signUp(username, password, email);
            Notification.show("Sign up successful! Choose a plan.");
            VaadinSession.getCurrent().setAttribute("username", username);
            UI.getCurrent().navigate("plans");
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}
