package com.example.hackathontest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController { //WebController für web

    @GetMapping("/")
    public String trialTriumphs(Model model) {
        // Model-Attribute
        model.addAttribute("headerTitle", "Trial Triumphs");
        return "trialTriumphs";  // Referenziert `src/main/resources/templates/trialTriumphs.html`
    }
}

