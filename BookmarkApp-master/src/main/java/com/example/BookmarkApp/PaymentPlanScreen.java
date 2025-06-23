
package com.example.BookmarkApp;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("plans")
@SpringComponent
@UIScope
public class PaymentPlanScreen extends VerticalLayout {

    private final OauthService oauthService;

    @Autowired
    public PaymentPlanScreen(OauthService oauthService) {
        this.oauthService = oauthService;
        styleView();
        buildUI();
    }

    private void styleView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Enhanced gradient background
        UI.getCurrent().getElement().executeJs(
            "document.body.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';" +
            "document.body.style.minHeight = '100vh';"
        );
    }

    private void buildUI() {
        // Header section
        Icon planIcon = VaadinIcon.DIAMOND.create();
        planIcon.setSize("56px");
        planIcon.getStyle().set("color", "#ffffff").set("margin-bottom", "16px");

        H1 title = new H1("✨ Choose Your Perfect Plan");
        title.getStyle()
            .set("color", "#ffffff")
            .set("font-size", "42px")
            .set("font-weight", "800")
            .set("text-align", "center")
            .set("margin", "0 0 16px 0")
            .set("letter-spacing", "-1px")
            .set("text-shadow", "0 4px 20px rgba(0,0,0,0.3)");

        Span subtitle = new Span("Unlock powerful features to supercharge your bookmark management");
        subtitle.getStyle()
            .set("color", "rgba(255,255,255,0.9)")
            .set("font-size", "18px")
            .set("text-align", "center")
            .set("margin-bottom", "48px")
            .set("display", "block")
            .set("font-weight", "400");

        VerticalLayout header = new VerticalLayout(planIcon, title, subtitle);
        header.setAlignItems(Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(false);

        // Plan cards
        HorizontalLayout cards = new HorizontalLayout(
                planCard("Free", "Free forever", "• Up to 20 bookmarks\n• Basic organization", null, "#38a169", VaadinIcon.GIFT),
                planCard("Pro", "$10 / year", "• Up to 50 bookmarks\n• Share bookmarks with others\n• Advanced search", "pro", "#667eea", VaadinIcon.STAR),
                planCard("Ultra", "$20 / year", "• Unlimited bookmarks\n• All Pro features\n• Priority support\n• Custom categories", "ultra", "#9f7aea", VaadinIcon.DIAMOND));
        
        cards.setSpacing(true);
        cards.setAlignItems(Alignment.STRETCH);
        cards.getStyle().set("gap", "24px");

        add(header, cards);
    }

    private VerticalLayout planCard(String name, String price, String benefits, String planCode, String accentColor, VaadinIcon iconType) {
        VerticalLayout card = new VerticalLayout();
        
        // Enhanced card styling with glassmorphism effect
        card.getStyle()
            .set("background", "linear-gradient(145deg, rgba(255,255,255,0.95) 0%, rgba(255,255,255,0.85) 100%)")
            .set("border-radius", "24px")
            .set("box-shadow", "0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1)")
            .set("padding", "32px 24px")
            .set("width", "320px")
            .set("min-height", "480px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)")
            .set("transition", "all 0.3s ease")
            .set("cursor", "pointer")
            .set("position", "relative")
            .set("overflow", "hidden");

        // Add hover effect
        card.getElement().addEventListener("mouseenter", e -> 
            card.getStyle().set("transform", "translateY(-8px)")
                .set("box-shadow", "0 30px 80px rgba(0,0,0,0.2), 0 12px 35px rgba(0,0,0,0.15)")
        );
        
        card.getElement().addEventListener("mouseleave", e -> 
            card.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", "0 20px 60px rgba(0,0,0,0.15), 0 8px 25px rgba(0,0,0,0.1)")
        );

        // Plan icon
        Icon planIcon = iconType.create();
        planIcon.setSize("48px");
        planIcon.getStyle().set("color", accentColor).set("margin-bottom", "16px");

        // Plan name with accent
        H3 nameH = new H3(name);
        nameH.getStyle()
            .set("color", "#1a202c")
            .set("font-size", "28px")
            .set("font-weight", "700")
            .set("margin", "0 0 8px 0")
            .set("text-align", "center");

        // Price styling
        H3 priceH = new H3(price);
        priceH.getStyle()
            .set("color", accentColor)
            .set("font-size", "24px")
            .set("font-weight", "600")
            .set("margin", "0 0 24px 0")
            .set("text-align", "center");

        // Benefits list with enhanced styling
        VerticalLayout benefitsList = new VerticalLayout();
        benefitsList.setPadding(false);
        benefitsList.setSpacing(true);
        benefitsList.getStyle()
            .set("background", "rgba(" + hexToRgb(accentColor) + ", 0.05)")
            .set("border-radius", "16px")
            .set("padding", "20px")
            .set("margin", "16px 0 24px 0")
            .set("border-left", "4px solid " + accentColor);

        for (String line : benefits.split("\\n")) {
            HorizontalLayout benefitItem = new HorizontalLayout();
            benefitItem.setAlignItems(Alignment.CENTER);
            benefitItem.setPadding(false);
            benefitItem.setSpacing(true);
            
            Icon checkIcon = VaadinIcon.CHECK_CIRCLE.create();
            checkIcon.setSize("16px");
            checkIcon.getStyle().set("color", accentColor);
            
            Span benefitText = new Span(line.substring(1).trim()); // Remove bullet point
            benefitText.getStyle()
                .set("color", "#4a5568")
                .set("font-size", "15px")
                .set("font-weight", "500")
                .set("line-height", "1.5");
            
            benefitItem.add(checkIcon, benefitText);
            benefitsList.add(benefitItem);
        }

        // Enhanced choose button
        Button choose = new Button("Choose " + name, VaadinIcon.ARROW_RIGHT.create());
        choose.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        choose.getStyle()
            .set("background", "linear-gradient(135deg, " + accentColor + " 0%, " + darkenColor(accentColor) + " 100%)")
            .set("color", "#ffffff")
            .set("border", "none")
            .set("border-radius", "12px")
            .set("font-weight", "600")
            .set("font-size", "16px")
            .set("padding", "16px 24px")
            .set("width", "100%")
            .set("margin-top", "auto")
            .set("cursor", "pointer")
            .set("transition", "all 0.3s ease")
            .set("box-shadow", "0 8px 25px rgba(" + hexToRgb(accentColor) + ", 0.4)");

        choose.addClickListener(e -> {
            String username = (String) VaadinSession.getCurrent().getAttribute("username");
            if ("Free".equalsIgnoreCase(name)) {
                if(username != null){
                    oauthService.updatePlan(username, name);
                }
                UI.getCurrent().navigate("mainscreen");
            } else {
                UI.getCurrent().navigate("pay?plan=" + name);
            }
        });

        card.setAlignItems(Alignment.CENTER);
        card.add(planIcon, nameH, priceH, benefitsList, choose);
        
        // Add popular badge for Pro plan
        if ("Pro".equals(name)) {
            Span popularBadge = new Span("Most Popular");
            popularBadge.getStyle()
                .set("position", "absolute")
                .set("top", "-12px")
                .set("right", "20px")
                .set("background", "linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%)")
                .set("color", "#ffffff")
                .set("padding", "8px 16px")
                .set("border-radius", "20px")
                .set("font-size", "12px")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px")
                .set("box-shadow", "0 4px 15px rgba(255, 107, 107, 0.4)");
            card.getElement().appendChild(popularBadge.getElement());
        }
        
        return card;
    }

    private String hexToRgb(String hex) {
        // Convert hex color to RGB values for rgba usage
        if (hex.equals("#38a169")) return "56, 161, 105";
        if (hex.equals("#667eea")) return "102, 126, 234";
        if (hex.equals("#9f7aea")) return "159, 122, 234";
        return "102, 126, 234"; // default
    }

    private String darkenColor(String hex) {
        // Return darker version of the color for gradient
        if (hex.equals("#38a169")) return "#2f855a";
        if (hex.equals("#667eea")) return "#5a67d8";
        if (hex.equals("#9f7aea")) return "#805ad5";
        return "#5a67d8"; // default
    }
}
