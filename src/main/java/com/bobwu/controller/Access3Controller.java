package com.bobwu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Access3Controller {

    @GetMapping("/api/access3")
    public String access3() {
        return "access3";
    }
}
