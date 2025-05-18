package com.TShaik.ChatRelay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("echo")
public class echoController {

    @GetMapping("/{message}")
    public ResponseEntity<String> getMessage(@PathVariable(value = "message") String message) {
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}