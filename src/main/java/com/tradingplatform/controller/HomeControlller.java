package com.tradingplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeControlller {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Trading Platform!";
    }

    @GetMapping("/secure")
    public String secure() {
        return "Welcome to the secure section of the Trading Platform!"; 
    }
}
