package com.TShaik.ChatRelay.controller;

import chatRelay.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TShaik.ChatRelay.models.LoginRequestData;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("login")
public class LoginController {

    private final DBManager dbManager = new DBManager("./", "Users.txt", "Chats.txt", "Messages.txt");
    LoginRequestData loginInfo;
    AbstractUser thisUser;

    @PostMapping("/")
    public ResponseEntity<Packet> getLogin(@RequestBody() LoginRequestData login) {
        loginInfo = login;
        thisUser = dbManager.getUserByUsername(loginInfo.getUsername());
        return new ResponseEntity<>(login.isValid(dbManager), HttpStatus.OK);
    }

    @GetMapping("/loading-u")
    public ResponseEntity<Packet> getUsers() {
        return new ResponseEntity<>(loginInfo.getAllUsers(dbManager), HttpStatus.OK);
    }

    @GetMapping("/loading-c")
    public ResponseEntity<Packet> getChats() {
        return new ResponseEntity<>(loginInfo.getAllChats(dbManager, thisUser), HttpStatus.OK);
    }

    @GetMapping("/loading-m")
    public ResponseEntity<Packet> getMessages() {
        return new ResponseEntity<>(loginInfo.getAllMessages(dbManager, thisUser), HttpStatus.OK);
    }
}