package com.example.BookmarkApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("mainscreen")
@SpringComponent
@UIScope
public class MainScreen extends VerticalLayout {
    private final BookmarkService bookmarkService;
    private final OauthService oauthService;
    private final Grid<Bookmark> grid;
    private final ListDataProvider<Bookmark> dataProvider;
    private ComboBox<String> categoryFilter;
    private MenuBar userMenu;
    private MenuItem rootMenuItem;
    
    // Dynamic search components
    private HorizontalLayout searchBar;
    private TextField searchField;
    private TextField userSearchField;
    private Button searchUsersButton;
    private String currentUserPlan = "";
    
    @Autowired
    public MainScreen(BookmarkService bookmarkService, OauthService oauthService) {
        // Initialize fields first
        this.bookmarkService = bookmarkService;
        this.oauthService = oauthService;
        this.grid = new Grid<>(Bookmark.class, false);
        this.dataProvider = new ListDataProvider<>(new ArrayList<>());
        
        // Session validation - ensure user is authenticated
        String sessionUsername = (String) VaadinSession.getCurrent().getAttribute("username");
        if (sessionUsername == null || sessionUsername.isEmpty()) {
            UI.getCurrent().navigate("");
            return;
        }
        
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
            .set("max-width", "1200px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)");

        grid.setDataProvider(dataProvider);

        setupStaticUI();
        setupDynamicSearchBar();
        refreshUserInterface();
        refreshGrid();
        populateCategoryOptions();
    }
    
    private void setupStaticUI() {
        // Enhanced grid styling
        grid.getStyle()
            .set("background", "rgba(255, 255, 255, 0.9)")
            .set("border-radius", "16px")
            .set("overflow", "hidden")
            .set("box-shadow", "0 8px 25px rgba(0,0,0,0.08)")
            .set("border", "1px solid rgba(229, 231, 235, 0.8)");

        // Grid columns with enhanced styling
        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Icon icon = "Public".equals(bookmark.getSecurityOption())
                    ? VaadinIcon.EYE.create()
                    : VaadinIcon.EYE_SLASH.create();
            icon.setColor("#667eea");
            Button button = new Button(icon, click -> {
                bookmark.setSecurityOption("Public".equals(bookmark.getSecurityOption()) ? "Private" : "Public");
                bookmarkService.addBookmark(bookmark);
                dataProvider.refreshItem(bookmark);
            });
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            return button;
        })).setHeader("Privacy").setWidth("100px").setFlexGrow(0);

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
            Icon icon = "Yes".equals(bookmark.getFavoriteOption())
                    ? VaadinIcon.STAR.create()
                    : VaadinIcon.STAR_O.create();
            icon.setColor("#fbbf24");
            icon.setSize("20px");
            Button button = new Button(icon, click -> {
                bookmark.setFavoriteOption("Yes".equals(bookmark.getFavoriteOption()) ? "No" : "Yes");
                bookmarkService.addBookmark(bookmark);
                refreshGrid();
            });
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            return button;
        })).setHeader("Favorite").setWidth("120px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(bookmark -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT), click -> {
                UI.getCurrent().navigate("Edit-Bookmark-Screen");
            });
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            editButton.getStyle().set("color", "#059669");
            return editButton;
        })).setHeader("Actions").setWidth("100px").setFlexGrow(0);

        // Header section
        H1 title = new H1("📚 Bookmark Manager");
        title.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0")
            .set("color", "#1a202c")
            .set("letter-spacing", "-0.5px");

        // Enhanced buttons
        Button addBookmarkButton = new Button("Add Bookmark", VaadinIcon.PLUS.create());
        addBookmarkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        addBookmarkButton.getStyle()
            .set("background", "linear-gradient(135deg, #059669 0%, #047857 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(5, 150, 105, 0.4)")
            .set("padding", "12px 24px")
            .set("font-size", "16px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");

        addBookmarkButton.getElement().addEventListener("mouseenter", e -> 
            addBookmarkButton.getStyle().set("transform", "translateY(-2px)")
                .set("box-shadow", "0 12px 35px rgba(5, 150, 105, 0.5)")
        );

        addBookmarkButton.addClickListener(event -> UI.getCurrent().navigate("Add-Bookmark-Screen"));

        // Enhanced user menu with change plan listener
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        String plan = oauthService.getPlanForUser(username);

        userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        rootMenuItem = userMenu.addItem((username != null ? username : "User") + " (" + plan + ")");
        rootMenuItem.getSubMenu().addItem("Change Plan", e -> UI.getCurrent().navigate("plans"));
        rootMenuItem.getSubMenu().addItem("Logout", e -> {
            // Clear all session attributes
            VaadinSession.getCurrent().setAttribute("username", null);
            
            // Use client-side redirect to avoid scope issues after session invalidation
            UI.getCurrent().getPage().executeJs("window.location.href = '/';");
            
            // Invalidate the entire session to ensure clean logout
            VaadinSession.getCurrent().getSession().invalidate();
        });
        
        // Enhanced user menu styling
        userMenu.getStyle()
            .set("background", "rgba(102, 126, 234, 0.1)")
            .set("border-radius", "12px")
            .set("padding", "8px 16px")
            .set("color", "#667eea")
            .set("font-weight", "600");

        // Layout assembly
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setPadding(false);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.add(title, addBookmarkButton, userMenu);
        topBar.expand(title);
        topBar.getStyle().set("margin-bottom", "24px");

        add(topBar);
    }
    
    private void setupDynamicSearchBar() {
        // Create basic search components
        searchField = new TextField();
        searchField.setPlaceholder("Search your bookmarks...");
        searchField.setClearButtonVisible(true);
        searchField.setWidth("400px");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        styleSearchField(searchField);

        categoryFilter = new ComboBox<>();
        categoryFilter.setLabel("Category");
        categoryFilter.setPlaceholder("All Categories");
        categoryFilter.setWidth("200px");
        categoryFilter.addValueChangeListener(e -> applyFilters(searchField.getValue(), categoryFilter.getValue()));
        styleCategoryFilter(categoryFilter);

        searchField.addValueChangeListener(event -> applyFilters(event.getValue(), categoryFilter.getValue()));

        // Create premium search components
        userSearchField = new TextField();
        userSearchField.setPlaceholder("Search users...");
        userSearchField.setClearButtonVisible(true);
        userSearchField.setWidth("300px");
        userSearchField.setPrefixComponent(new Icon(VaadinIcon.USERS));
        styleSearchField(userSearchField);
        
        searchUsersButton = new Button("Search Users", VaadinIcon.SEARCH.create());
        searchUsersButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchUsersButton.getStyle()
            .set("background", "linear-gradient(135deg, #9f7aea 0%, #805ad5 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(159, 122, 234, 0.4)")
            .set("padding", "12px 24px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");
        
        searchUsersButton.addClickListener(e -> {
            String searchTerm = userSearchField.getValue();
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                UI.getCurrent().navigate("user-bookmarks?search=" + searchTerm.trim());
            }
        });

        // Create search bar container
        searchBar = new HorizontalLayout();
        searchBar.setAlignItems(FlexComponent.Alignment.END);
        searchBar.setSpacing(true);
        searchBar.getStyle()
            .set("background", "rgba(248, 250, 252, 0.8)")
            .set("padding", "20px")
            .set("border-radius", "16px")
            .set("margin-bottom", "24px")
            .set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)");
    }
    
    private void updateSearchBarComponents() {
        // Remove all components from search bar
        searchBar.removeAll();
        
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        String plan = oauthService.getPlanForUser(username);
        
        // Add components based on current plan
        if ("Pro".equals(plan) || "Ultra".equals(plan)) {
            searchBar.add(userSearchField, searchUsersButton, searchField, categoryFilter);
        } else {
            searchBar.add(searchField, categoryFilter);
        }
    }
    
    public void refreshUserInterface() {
        String username = (String) VaadinSession.getCurrent().getAttribute("username");
        String plan = oauthService.getPlanForUser(username);
        
        // Check if plan has changed
        if (!plan.equals(currentUserPlan)) {
            currentUserPlan = plan;
            updateSearchBarComponents();
            
            // Update user menu text
            if (rootMenuItem != null) {
                rootMenuItem.setText((username != null ? username : "User") + " (" + plan + ")");
            }
        }
        
        // Make sure search bar is added to the layout if it's not already
        if (!getChildren().anyMatch(component -> component == searchBar)) {
            addComponentAtIndex(1, searchBar); // Add after topBar
        }
        
        // Add grid if not already present
        if (!getChildren().anyMatch(component -> component == grid)) {
            add(grid);
        }
    }
    
    // Public method to force refresh the UI - can be called when plan changes
    public void forceRefreshUI() {
        currentUserPlan = ""; // Reset to force update
        refreshUserInterface();
    }
    
    // Method to periodically check for plan changes (called from client-side)
    public void checkForPlanChanges() {
        getUI().ifPresent(ui -> ui.access(() -> {
            refreshUserInterface();
        }));
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

    private void styleCategoryFilter(ComboBox<String> field) {
        field.getElement().executeJs("""
            const input = this.shadowRoot.querySelector('input');
            const label = this.shadowRoot.querySelector('label');
            if (input) {
                input.style.padding = '12px 16px';
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
            }
        """);
    }

    private void applyFilters(String searchTerm, String category) {
        String currentUsername = (String) VaadinSession.getCurrent().getAttribute("username");
        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUsername(currentUsername);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String lower = searchTerm.toLowerCase();
            bookmarks = bookmarks.stream().filter(b ->
                    b.getDisplayName().toLowerCase().contains(lower) ||
                    b.getUrl().toLowerCase().contains(lower) ||
                    b.getTagsInput().toLowerCase().contains(lower))
                    .toList();
        }

        if (category != null && !category.isBlank()) {
            bookmarks = bookmarks.stream()
                    .filter(b -> category.equalsIgnoreCase(b.getCategory()))
                    .toList();
        }

        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(sortBookmarks(bookmarks));
        dataProvider.refreshAll();
    }
    
    private void populateCategoryOptions() {
        String currentUsername = (String) VaadinSession.getCurrent().getAttribute("username");
        List<String> categories = bookmarkService.getBookmarksByUsername(currentUsername).stream()
                .map(Bookmark::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .toList();
        categoryFilter.setItems(categories);
    }
    
    public void refreshGrid() {
        String currentUsername = (String) VaadinSession.getCurrent().getAttribute("username");
        
        List<Bookmark> bookmarks = Objects.requireNonNullElse(bookmarkService.getBookmarksByUsername(currentUsername), new ArrayList<>());
        
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(sortBookmarks(bookmarks));
        dataProvider.refreshAll();
        populateCategoryOptions();
        refreshUserInterface(); // Refresh UI when grid refreshes
    }
    
    private List<Bookmark> sortBookmarks(List<Bookmark> bookmarks) {
        List<Bookmark> favorites = new ArrayList<>();
        List<Bookmark> others = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            if ("Yes".equals(b.getFavoriteOption())) {
                favorites.add(b);
            } else {
                others.add(b);
            }
        }
        List<Bookmark> sortedBookmarks = new ArrayList<>();
        sortedBookmarks.addAll(favorites);
        sortedBookmarks.addAll(others);
        return sortedBookmarks;
    }
    
    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refreshUserInterface();
        setupPlanChangeDetection();
    }
    
    private void setupPlanChangeDetection() {
        // Add client-side JavaScript to periodically check for plan changes
        getElement().executeJs("""
            // Check for plan changes every 2 seconds
            if (window.planChangeInterval) {
                clearInterval(window.planChangeInterval);
            }
            
            window.planChangeInterval = setInterval(() => {
                if ($0.$server && $0.$server.checkForPlanChanges) {
                    $0.$server.checkForPlanChanges();
                }
            }, 2000);
            
            // Also check when window gains focus (user returns from another tab/page)
            window.addEventListener('focus', () => {
                if ($0.$server && $0.$server.checkForPlanChanges) {
                    $0.$server.checkForPlanChanges();
                }
            });
        """, getElement());
    }
    
    @Override
    protected void onDetach(com.vaadin.flow.component.DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Clean up the interval when component is detached
        getElement().executeJs("""
            if (window.planChangeInterval) {
                clearInterval(window.planChangeInterval);
                window.planChangeInterval = null;
            }
        """);
    }
}
