package com.example.helloworldmvc.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class RootController {

    @GetMapping("/health")
    public String health() {
        return "I'm Healthy!!!";
    }

    @GetMapping("/test")
    public String test(){
        return "cicd is done";
    }
}
