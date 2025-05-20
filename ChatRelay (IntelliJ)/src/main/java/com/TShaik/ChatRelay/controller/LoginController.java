package com.TShaik.ChatRelay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TShaik.ChatRelay.models.LoginRequestData;

import chatRelay.DBManager;

@RestController
@RequestMapping("login")
public class LoginController {

    private DBManager dbManager = new DBManager("./", "Users.txt", "Chats.txt", "Messages.txt");

    @PostMapping("/")
    public ResponseEntity<Boolean> getMessage(@RequestBody() LoginRequestData login) {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}