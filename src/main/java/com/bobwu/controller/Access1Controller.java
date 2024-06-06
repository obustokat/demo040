package com.bobwu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Access1Controller {

    @GetMapping("/api/access1")
    public String access1() {
        return "access1";
    }
}
