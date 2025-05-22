package com.example.backendb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/b")
public class BController {
    @GetMapping("/hello")
    public String hello() {
        return "Hola desde Backend B";
    }
}
