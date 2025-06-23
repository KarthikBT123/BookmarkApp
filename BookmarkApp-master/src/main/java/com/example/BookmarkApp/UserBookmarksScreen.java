package com.example.BookmarkApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("user-bookmarks")
@SpringComponent
@UIScope
public class UserBookmarksScreen extends VerticalLayout implements HasUrlParameter<String> {
    
    private final BookmarkService bookmarkService;
    private final OauthService oauthService;
    private final Grid<Bookmark> grid;
    private final ListDataProvider<Bookmark> dataProvider;
    private TextField searchField;
    private String currentSearchTerm = "";
    
    @Autowired
    public UserBookmarksScreen(BookmarkService bookmarkService, OauthService oauthService) {
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
            .set("padding", "32px")
            .set("margin", "40px auto")
            .set("max-width", "1400px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)");

        this.bookmarkService = bookmarkService;
        this.oauthService = oauthService;
        this.grid = new Grid<>(Bookmark.class, false);
        this.dataProvider = new ListDataProvider<>(new ArrayList<>());
        grid.setDataProvider(dataProvider);

        // Check if user has pro/ultra access
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        String plan = oauthService.getPlanForUser(username);
        
        if (!"Pro".equals(plan) && !"Ultra".equals(plan)) {
            // Show premium feature message and redirect
            showPremiumFeatureMessage(plan);
            return;
        }

        setupUI();
    }
    
    private void setupUI() {
        // Enhanced grid styling
        grid.getStyle()
            .set("background", "rgba(255, 255, 255, 0.9)")
            .set("border-radius", "16px")
            .set("overflow", "hidden")
            .set("box-shadow", "0 8px 25px rgba(0,0,0,0.08)")
            .set("border", "1px solid rgba(229, 231, 235, 0.8)");

        // Grid columns
        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Span userSpan = new Span("@" + bookmark.getUsername());
            userSpan.getStyle()
                .set("color", "#9f7aea")
                .set("font-weight", "600")
                .set("font-size", "14px")
                .set("background", "rgba(159, 122, 234, 0.1)")
                .set("padding", "4px 8px")
                .set("border-radius", "8px");
            return userSpan;
        })).setHeader("User").setWidth("150px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Anchor link = new Anchor(bookmark.getUrl(), bookmark.getDisplayName());
            link.setTarget("_blank");
            link.getStyle()
                .set("color", "#667eea")
                .set("text-decoration", "none")
                .set("font-weight", "600")
                .set("transition", "color 0.3s ease");
            link.getElement().addEventListener("mouseenter", e -> 
                link.getStyle().set("color", "#5a67d8")
            );
            link.getElement().addEventListener("mouseleave", e -> 
                link.getStyle().set("color", "#667eea")
            );
            return link;
        })).setHeader("Bookmark").setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Span categorySpan = new Span(bookmark.getCategory() != null ? bookmark.getCategory() : "Uncategorized");
            categorySpan.getStyle()
                .set("color", "#059669")
                .set("font-weight", "500")
                .set("background", "rgba(5, 150, 105, 0.1)")
                .set("padding", "4px 8px")
                .set("border-radius", "8px")
                .set("font-size", "14px");
            return categorySpan;
        })).setHeader("Category").setWidth("150px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Icon icon = "Yes".equals(bookmark.getFavoriteOption())
                    ? VaadinIcon.STAR.create()
                    : VaadinIcon.STAR_O.create();
            icon.setColor("#fbbf24");
            icon.setSize("20px");
            return icon;
        })).setHeader("Favorited").setWidth("100px").setFlexGrow(0);

        // Header section
        H1 title = new H1("🔍 Discover Public Bookmarks");
        title.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0")
            .set("color", "#1a202c")
            .set("letter-spacing", "-0.5px");

        // Search section
        searchField = new TextField();
        searchField.setPlaceholder("Search for users...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("400px");
        searchField.setPrefixComponent(new Icon(VaadinIcon.USERS));
        styleSearchField(searchField);

        Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.getStyle()
            .set("background", "linear-gradient(135deg, #9f7aea 0%, #805ad5 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(159, 122, 234, 0.4)")
            .set("padding", "12px 24px")
            .set("font-size", "16px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");

        searchButton.addClickListener(e -> performSearch(searchField.getValue()));
        searchField.addValueChangeListener(e -> {
            if (e.getValue() == null || e.getValue().trim().isEmpty()) {
                clearResults();
            }
        });

        // Back button
        Button backButton = new Button("Back to My Bookmarks", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        backButton.getStyle()
            .set("color", "#667eea")
            .set("font-weight", "600")
            .set("transition", "all 0.3s ease");
        backButton.addClickListener(e -> UI.getCurrent().navigate("mainscreen"));

        // Layout assembly
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setPadding(false);
        topBar.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        topBar.add(title, backButton);
        topBar.expand(title);
        topBar.getStyle().set("margin-bottom", "24px");

        HorizontalLayout searchBar = new HorizontalLayout();
        searchBar.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);
        searchBar.setSpacing(true);
        searchBar.add(searchField, searchButton);
        searchBar.getStyle()
            .set("background", "rgba(248, 250, 252, 0.8)")
            .set("padding", "20px")
            .set("border-radius", "16px")
            .set("margin-bottom", "24px")
            .set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");

        add(topBar, searchBar, grid);
    }
    
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
        if (params.containsKey("search")) {
            String searchTerm = params.get("search").get(0);
            this.currentSearchTerm = searchTerm;
            if (searchField != null) {
                searchField.setValue(searchTerm);
                performSearch(searchTerm);
            }
        }
    }
    
    private void performSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            clearResults();
            return;
        }
        
        List<Bookmark> results = bookmarkService.searchPublicBookmarksByUsername(searchTerm.trim());
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(results);
        dataProvider.refreshAll();
        
        this.currentSearchTerm = searchTerm.trim();
    }
    
    private void clearResults() {
        dataProvider.getItems().clear();
        dataProvider.refreshAll();
        this.currentSearchTerm = "";
    }
    
    private void styleSearchField(TextField field) {
        field.getElement().executeJs("""
            const input = this.shadowRoot.querySelector('input');
            if (input) {
                input.style.padding = '12px 16px';
                input.style.borderRadius = '12px';
                input.style.border = '2px solid #e2e8f0';
                input.style.fontSize = '16px';
                input.style.transition = 'all 0.3s ease';
                input.style.backgroundColor = '#ffffff';
            }
            this.addEventListener('focus', () => {
                if (input) {
                    input.style.borderColor = '#9f7aea';
                    input.style.boxShadow = '0 0 0 3px rgba(159, 122, 234, 0.1)';
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
    
    private void showPremiumFeatureMessage(String currentPlan) {
        // Header with lock icon
        Icon lockIcon = VaadinIcon.LOCK.create();
        lockIcon.setSize("64px");
        lockIcon.getStyle()
            .set("color", "#ef4444")
            .set("margin-bottom", "24px");

        H1 title = new H1("🔒 Premium Feature");
        title.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0 0 16px 0")
            .set("color", "#1a202c")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("User bookmark search is only available for Pro and Ultra members");
        subtitle.getStyle()
            .set("color", "#6b7280")
            .set("font-size", "18px")
            .set("text-align", "center")
            .set("margin-bottom", "32px")
            .set("display", "block")
            .set("font-weight", "400");

        Span currentPlanInfo = new Span("Your current plan: " + (currentPlan != null ? currentPlan : "Free"));
        currentPlanInfo.getStyle()
            .set("color", "#ef4444")
            .set("font-size", "16px")
            .set("font-weight", "600")
            .set("text-align", "center")
            .set("margin-bottom", "32px")
            .set("display", "block");

        // Upgrade button
        Button upgradeButton = new Button("Upgrade to Pro", VaadinIcon.ARROW_RIGHT.create());
        upgradeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        upgradeButton.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
            .set("padding", "16px 32px")
            .set("font-size", "16px")
            .set("transition", "all 0.3s ease")
            .set("border", "none")
            .set("margin-right", "16px");

        upgradeButton.addClickListener(e -> UI.getCurrent().navigate("plans"));

        // Back button
        Button backButton = new Button("Back to My Bookmarks", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        backButton.getStyle()
            .set("color", "#667eea")
            .set("font-weight", "600")
            .set("transition", "all 0.3s ease");
        backButton.addClickListener(e -> UI.getCurrent().navigate("mainscreen"));

        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.add(upgradeButton, backButton);

        // Center everything
        setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        add(lockIcon, title, subtitle, currentPlanInfo, buttonLayout);
    }
} 