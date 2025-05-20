package com.TShaik.ChatRelay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TShaik.ChatRelay.models.LoginRequestData;

import chatRelay.DBManager;

@RestController
@RequestMapping("login")
public class LoginController {

    private final DBManager dbManager = new DBManager("./", "Users.txt", "Chats.txt", "Messages.txt");

    @PostMapping("/")
    public ResponseEntity<Boolean> getMessage(@RequestBody() LoginRequestData login) {
        return new ResponseEntity<>(login.isValid(dbManager), HttpStatus.OK);
    }
}