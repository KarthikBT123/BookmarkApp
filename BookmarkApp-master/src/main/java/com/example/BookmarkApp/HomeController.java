package com.example.BookmarkApp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("#")
    public String home() {
        return "Hello! The app is up and running.";
    }
}
