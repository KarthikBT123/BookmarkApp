package com.example.BookmarkApp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@Route("Edit-Bookmark-Screen")
@SpringComponent
@UIScope
public class EditBookmarkScreen extends VerticalLayout {
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private MainScreen mainScreen;
    
    public EditBookmarkScreen() {
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
            .set("max-width", "800px")
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("border", "1px solid rgba(255,255,255,0.3)")
            .set("backdrop-filter", "blur(20px)");
        
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Header with icon
        Icon editIcon = VaadinIcon.EDIT.create();
        editIcon.setSize("56px");
        editIcon.getStyle()
            .set("color", "#667eea")
            .set("margin-bottom", "16px");

        H1 titleText = new H1("✏️ Edit Your Bookmarks");
        titleText.getStyle()
            .set("font-family", "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .set("font-size", "36px")
            .set("font-weight", "800")
            .set("margin", "0 0 8px 0")
            .set("color", "#1a202c")
            .set("text-align", "center")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Update or remove your saved bookmarks");
        subtitle.getStyle()
            .set("color", "#64748b")
            .set("font-size", "16px")
            .set("text-align", "center")
            .set("margin-bottom", "40px")
            .set("display", "block")
            .set("font-weight", "400");

        // Edit section
        VerticalLayout editSection = createSection("📝 Edit Bookmark", "#667eea");
        
        TextField editSearchField = new TextField();
        editSearchField.setLabel("Search Bookmark to Edit");
        editSearchField.setPlaceholder("Enter bookmark name or tag");
        editSearchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        editSearchField.setClearButtonVisible(true);
        styleFormField(editSearchField);

        TextField nameField = new TextField();
        nameField.setLabel("Bookmark Name");
        nameField.setPlaceholder("Update bookmark name");
        nameField.setPrefixComponent(VaadinIcon.TEXT_LABEL.create());
        nameField.setClearButtonVisible(true);
        styleFormField(nameField);

        TextField urlField = new TextField();
        urlField.setLabel("Bookmark URL");
        urlField.setPlaceholder("Update bookmark URL");
        urlField.setPrefixComponent(VaadinIcon.GLOBE.create());
        urlField.setClearButtonVisible(true);
        styleFormField(urlField);

        TextField tagsField = new TextField();
        tagsField.setLabel("Tags");
        tagsField.setPlaceholder("Update tags (comma separated)");
        tagsField.setPrefixComponent(VaadinIcon.TAG.create());
        tagsField.setClearButtonVisible(true);
        styleFormField(tagsField);

        Button saveButton = new Button("Save Changes", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        styleActionButton(saveButton, "#059669", "0 8px 25px rgba(5, 150, 105, 0.4)");

        saveButton.addClickListener(event -> {
            String searchInput = editSearchField.getValue();
            if (searchInput == null || searchInput.trim().isEmpty()) {
                Notification.show("Please search for a bookmark to edit", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            List<Bookmark> allBookmarks = bookmarkService.getAllBookmarks();
            List<Bookmark> exactMatches = allBookmarks.stream()
                .filter(b -> b.getDisplayName() != null && b.getDisplayName().equalsIgnoreCase(searchInput.trim()))
                .collect(Collectors.toList());
            
            List<Bookmark> bookmarks;
            if (!exactMatches.isEmpty()) {
                bookmarks = exactMatches;
            } else {
                bookmarks = allBookmarks.stream()
                    .filter(b -> (b.getDisplayName() != null && b.getDisplayName().toLowerCase().contains(searchInput.toLowerCase())) ||
                            (b.getTagsInput() != null && b.getTagsInput().toLowerCase().contains(searchInput.toLowerCase())))
                    .collect(Collectors.toList());
            }
            
            if (bookmarks.isEmpty()) {
                Notification.show("No bookmarks found with name or tag: " + searchInput, 3000, Notification.Position.MIDDLE);
                return;
            }
            if (bookmarks.size() > 1) {
                Notification.show("Multiple bookmarks found. Please refine your search (try the exact bookmark name).", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            Bookmark bookmarkToEdit = bookmarks.get(0);
            bookmarkToEdit.setDisplayName(nameField.getValue());
            bookmarkToEdit.setUrl(urlField.getValue());
            bookmarkToEdit.setTagsInput(tagsField.getValue());
            
            bookmarkRepository.save(bookmarkToEdit);
            mainScreen.refreshGrid();
            Notification.show("Bookmark updated successfully", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("mainscreen");
        });

        editSection.add(editSearchField, nameField, urlField, tagsField, saveButton);

        // Delete section
        VerticalLayout deleteSection = createSection("🗑️ Delete Bookmark", "#dc2626");
        
        TextField deleteField = new TextField();
        deleteField.setLabel("Search Bookmark to Delete");
        deleteField.setPlaceholder("Enter bookmark name or tag");
        deleteField.setPrefixComponent(VaadinIcon.SEARCH.create());
        deleteField.setClearButtonVisible(true);
        styleFormField(deleteField);

        Button deleteBookmarkButton = new Button("Delete Bookmark", VaadinIcon.TRASH.create());
        deleteBookmarkButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_LARGE);
        styleActionButton(deleteBookmarkButton, "#dc2626", "0 8px 25px rgba(220, 38, 38, 0.4)");

        deleteBookmarkButton.addClickListener(event -> {
            String searchInput = deleteField.getValue();
            if (searchInput == null || searchInput.trim().isEmpty()) {
                Notification.show("Please enter a bookmark name or tag", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            List<Bookmark> allBookmarks = bookmarkService.getAllBookmarks();
            List<Bookmark> exactMatches = allBookmarks.stream()
                .filter(b -> b.getDisplayName() != null && b.getDisplayName().equalsIgnoreCase(searchInput.trim()))
                .collect(Collectors.toList());
            
            List<Bookmark> bookmarks;
            if (!exactMatches.isEmpty()) {
                bookmarks = exactMatches;
            } else {
                bookmarks = allBookmarks.stream()
                    .filter(b -> (b.getDisplayName() != null && b.getDisplayName().toLowerCase().contains(searchInput.toLowerCase())) ||
                            (b.getTagsInput() != null && b.getTagsInput().toLowerCase().contains(searchInput.toLowerCase())))
                    .collect(Collectors.toList());
            }
            
            if (bookmarks.isEmpty()) {
                Notification.show("No bookmarks found with name or tag: " + searchInput, 3000, Notification.Position.MIDDLE);
                return;
            }
            if (bookmarks.size() > 1) {
                Notification.show("Multiple bookmarks found. Please refine your search (try the exact bookmark name).", 3000, Notification.Position.MIDDLE);
                return;
            }
            
            Bookmark bookmarkToDelete = bookmarks.get(0);
            bookmarkRepository.deleteById(bookmarkToDelete.getId());
            mainScreen.refreshGrid();
            Notification.show("Bookmark deleted successfully", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("mainscreen");
        });

        deleteSection.add(deleteField, deleteBookmarkButton);

        // Back button
        Button backButton = new Button("Back to Bookmarks", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        backButton.getStyle()
            .set("color", "#667eea")
            .set("font-weight", "600")
            .set("margin-top", "24px")
            .set("padding", "12px 24px")
            .set("border-radius", "12px")
            .set("transition", "all 0.3s ease")
            .set("background", "rgba(102, 126, 234, 0.05)")
            .set("border", "2px solid transparent");

        backButton.getElement().addEventListener("mouseenter", e -> 
            backButton.getStyle().set("background", "rgba(102, 126, 234, 0.1)")
                .set("border-color", "rgba(102, 126, 234, 0.2)")
        );
        
        backButton.addClickListener(e -> UI.getCurrent().navigate("mainscreen"));

        add(editIcon, titleText, subtitle, editSection, deleteSection, backButton);
    }

    private VerticalLayout createSection(String title, String accentColor) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidthFull();
        section.getStyle()
            .set("background", "rgba(248, 250, 252, 0.8)")
            .set("border-radius", "16px")
            .set("border-left", "4px solid " + accentColor)
            .set("margin-bottom", "32px")
            .set("padding", "24px");

        H3 sectionTitle = new H3(title);
        sectionTitle.getStyle()
            .set("color", accentColor)
            .set("font-size", "24px")
            .set("font-weight", "700")
            .set("margin", "0 0 20px 0")
            .set("text-align", "center");

        section.add(sectionTitle);
        return section;
    }

    private void styleFormField(TextField field) {
        field.getStyle().set("width", "100%").set("margin-bottom", "16px");
        
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

    private void styleActionButton(Button button, String color, String shadow) {
        button.getStyle()
            .set("background", "linear-gradient(135deg, " + color + " 0%, " + darkenColor(color) + " 100%)")
            .set("color", "#ffffff")
            .set("font-weight", "600")
            .set("border-radius", "12px")
            .set("box-shadow", shadow)
            .set("padding", "16px 32px")
            .set("font-size", "16px")
            .set("width", "100%")
            .set("margin-top", "16px")
            .set("transition", "all 0.3s ease")
            .set("border", "none");

        button.getElement().addEventListener("mouseenter", e -> 
            button.getStyle().set("transform", "translateY(-2px)")
                .set("box-shadow", shadow.replace("0.4)", "0.6)"))
        );
        
        button.getElement().addEventListener("mouseleave", e -> 
            button.getStyle().set("transform", "translateY(0)")
                .set("box-shadow", shadow)
        );
    }

    private String darkenColor(String color) {
        if (color.equals("#059669")) return "#047857";
        if (color.equals("#dc2626")) return "#b91c1c";
        return color;
    }
}
