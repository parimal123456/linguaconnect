package com.parimal.linguaconnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hellocontroller {
    @GetMapping("/")
    String hello(){
        return "hello";
    }
}
