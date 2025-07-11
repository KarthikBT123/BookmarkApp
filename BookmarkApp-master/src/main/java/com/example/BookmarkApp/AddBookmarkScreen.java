package com.example.BookmarkApp;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("Add-Bookmark-Screen")
@SpringComponent
@UIScope
public class AddBookmarkScreen extends VerticalLayout {
    private final BookmarkService bookmarkService;
    private final MainScreen mainScreen;
    private final H2 error;
    private boolean errorShown = false;
    
    @Autowired
    public AddBookmarkScreen(BookmarkService bookmarkService, MainScreen mainScreen) {
        this.bookmarkService = bookmarkService;
        this.mainScreen = mainScreen;
        
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
            .set("max-width", "680px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)");

        setAlignItems(Alignment.CENTER);

        // Error message styling
        error = new H2("⚠️ Please enter all required information properly");
        error.getStyle()
            .set("font-family", "'Inter', sans-serif")
            .set("font-size", "16px")
            .set("font-weight", "600")
            .set("margin", "0 0 20px 0")
            .set("color", "#dc2626")
            .set("padding", "16px")
            .set("background", "rgba(220, 38, 38, 0.1)")
            .set("border-radius", "12px")
            .set("border-left", "4px solid #dc2626")
            .set("width", "100%")
            .set("text-align", "center");

        // Header with icon
        Icon bookmarkIcon = VaadinIcon.BOOKMARK.create();
        bookmarkIcon.setSize("56px");
        bookmarkIcon.getStyle()
            .set("color", "#667eea")
            .set("margin-bottom", "16px");

        H1 title = new H1("📌 Add New Bookmark");
        title.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0 0 8px 0")
            .set("color", "#1a202c")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Save and organize your favorite websites");
        subtitle.getStyle()
            .set("color", "#64748b")
            .set("font-size", "16px")
            .set("text-align", "center")
            .set("margin-bottom", "32px")
            .set("display", "block")
            .set("font-weight", "400");

        // Form fields with enhanced styling
        TextField urlTextField = new TextField();
        urlTextField.setLabel("Website URL");
        urlTextField.setPlaceholder("https://example.com or www.example.com");
        urlTextField.setPrefixComponent(VaadinIcon.GLOBE.create());
        urlTextField.setClearButtonVisible(true);
        styleFormField(urlTextField, true);

        TextField displayTextField = new TextField();
        displayTextField.setLabel("Bookmark Name");
        displayTextField.setPlaceholder("Give your bookmark a memorable name");
        displayTextField.setPrefixComponent(VaadinIcon.TEXT_LABEL.create());
        displayTextField.setClearButtonVisible(true);
        styleFormField(displayTextField, true);

        TextArea tags = new TextArea();
        tags.setLabel("Tags");
        tags.setPlaceholder("work, development, tutorial (comma separated)");
        tags.getStyle().set("width", "100%").set("margin-bottom", "20px");
        styleTextArea(tags);

        ComboBox<String> categoryField = new ComboBox<>();
        categoryField.setLabel("Category");
        categoryField.setItems("Work", "Personal", "Education", "Entertainment", "Research", "Other");
        categoryField.setPlaceholder("Select a category");
        categoryField.setPrefixComponent(VaadinIcon.FOLDER.create());
        styleComboBox(categoryField);

        TextField customCategoryField = new TextField();
        customCategoryField.setLabel("Custom Category");
        customCategoryField.setPlaceholder("Enter your custom category");
        customCategoryField.setPrefixComponent(VaadinIcon.EDIT.create());
        customCategoryField.setClearButtonVisible(true);
        customCategoryField.setVisible(false); // Initially hidden
        styleFormField(customCategoryField, false);
        
        // Show/hide custom category field based on dropdown selection
        categoryField.addValueChangeListener(event -> {
            boolean isOther = "Other".equals(event.getValue());
            customCategoryField.setVisible(isOther);
            if (!isOther) {
                customCategoryField.clear();
            }
        });

        ComboBox<String> security = new ComboBox<>();
        security.setLabel("Privacy Setting");
        security.setItems("Private", "Public");
        security.setPlaceholder("Who can see this bookmark?");
        security.setPrefixComponent(VaadinIcon.EYE.create());
        styleComboBox(security);

        // Enhanced add button
        Button addBookmark = new Button("Add Bookmark", VaadinIcon.PLUS.create());
        addBookmark.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        addBookmark.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
            .set("padding", "16px 32px")
            .set("font-size", "16px")
            .set("width", "100%")
            .set("margin-top", "24px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");

        addBookmark.getElement().addEventListener("mouseenter", e -> 
            addBookmark.getStyle().set("transform", "translateY(-2px)")
                .set("box-shadow", "0 12px 35px rgba(102, 126, 234, 0.5)")
        );
        
        addBookmark.getElement().addEventListener("mouseleave", e -> 
            addBookmark.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", "0 8px 25px rgba(102, 126, 234, 0.4)")
        );

        addBookmark.addClickListener(event -> {
            String url = urlTextField.getValue();
            String displayName = displayTextField.getValue();
            String tagInput = tags.getValue();
            String favoriteOption = "No";
            String category;
            if ("Other".equals(categoryField.getValue())) {
                category = customCategoryField.getValue();
            } else {
                category = categoryField.getValue();
            }
            String securityOption = security.getValue();
            String username = (String) VaadinSession.getCurrent().getAttribute("username");
            
            if (url == null || url.trim().isEmpty() || !isValidUrl(url) ||
                    displayName == null || displayName.trim().isEmpty() ||
                    category == null || category.trim().isEmpty() || securityOption == null) {
                if (!errorShown) {
                    add(error);
                    errorShown = true;
                }
            } else {
                if (errorShown) {
                    remove(error);
                    errorShown = false;
                }
                bookmarkService.addBookmark(new Bookmark(url, displayName, tagInput, favoriteOption, securityOption, category, username));
                mainScreen.refreshGrid();
                UI.getCurrent().navigate("mainscreen");
            }
        });

        // Back button
        Button backButton = new Button("Back to Bookmarks", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.getStyle()
            .set("color", "#667eea")
            .set("font-weight", "600")
            .set("margin-top", "16px")
            .set("transition", "all 0.3s ease");
        backButton.addClickListener(e -> UI.getCurrent().navigate("mainscreen"));

        HorizontalLayout buttonLayout = new HorizontalLayout(addBookmark, backButton);
        buttonLayout.setWidthFull();
        buttonLayout.setFlexGrow(1, addBookmark);

        VerticalLayout formLayout = new VerticalLayout(
                urlTextField, displayTextField, tags, categoryField, customCategoryField, security, buttonLayout);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setWidthFull();
        formLayout.setPadding(false);
        formLayout.setSpacing(true);

        add(bookmarkIcon, title, subtitle, formLayout);
    }

    private void styleFormField(TextField field, boolean required) {
        field.getStyle().set("width", "100%").set("margin-bottom", "20px");
        if (required) field.setRequiredIndicatorVisible(true);
        
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

    private void styleTextArea(TextArea field) {
        field.getElement().executeJs("""
            const textarea = this.shadowRoot.querySelector('textarea');
            const label = this.shadowRoot.querySelector('label');
            if (textarea) {
                textarea.style.padding = '16px';
                textarea.style.borderRadius = '12px';
                textarea.style.border = '2px solid #e2e8f0';
                textarea.style.fontSize = '16px';
                textarea.style.transition = 'all 0.3s ease';
                textarea.style.backgroundColor = '#ffffff';
                textarea.style.minHeight = '80px';
                textarea.style.resize = 'vertical';
            }
            if (label) {
                label.style.fontSize = '14px';
                label.style.fontWeight = '600';
                label.style.color = '#374151';
                label.style.marginBottom = '8px';
            }
            this.addEventListener('focus', () => {
                if (textarea) {
                    textarea.style.borderColor = '#667eea';
                    textarea.style.boxShadow = '0 0 0 3px rgba(102, 126, 234, 0.1)';
                }
            });
            this.addEventListener('blur', () => {
                if (textarea) {
                    textarea.style.borderColor = '#e2e8f0';
                    textarea.style.boxShadow = 'none';
                }
            });
        """);
    }

    private void styleComboBox(ComboBox<String> field) {
        field.getStyle().set("width", "100%").set("margin-bottom", "20px");
        field.setRequiredIndicatorVisible(true);
        
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
        """);
    }

    private boolean isValidUrl(String url) {
        if (url == null) return false;
        String trimmedUrl = url.trim().toLowerCase();
        return trimmedUrl.startsWith("http://") || 
               trimmedUrl.startsWith("https://") || 
               trimmedUrl.startsWith("www.");
    }
}
