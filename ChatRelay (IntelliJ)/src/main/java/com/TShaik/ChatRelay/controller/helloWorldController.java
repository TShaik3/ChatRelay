package com.TShaik.ChatRelay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("us_java")
public class helloWorldController {

    @GetMapping("/ip")
    public ResponseEntity<String> getIp() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}