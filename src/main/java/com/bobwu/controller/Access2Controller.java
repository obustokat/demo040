package com.bobwu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Access2Controller {

    @GetMapping("/api/access2")
    public String access2() {
        return "access2";
    }
}
