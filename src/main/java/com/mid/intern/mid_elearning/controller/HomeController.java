package com.mid.intern.mid_elearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Mengarahkan ke src/main/resources/templates/index.html
        return "index";
    }
}
