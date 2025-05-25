package com.TShaik.ChatRelay.models;

import chatRelay.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class LoginRequestData {
    private String username;
    private String password;

    public Packet isValid(DBManager dbManager) {
        AbstractUser thisUser = dbManager.getUserByUsername(username);
        ArrayList<String> userInfoStringed = new ArrayList<>();

        userInfoStringed.add(thisUser.getId());
        userInfoStringed.add(thisUser.getFirstName());
        userInfoStringed.add(thisUser.getLastName());
        userInfoStringed.add(String.valueOf(thisUser.isAdmin()));
        userInfoStringed.add(String.valueOf(thisUser.isDisabled()));

        return new Packet(Status.SUCCESS, actionType.LOGIN, userInfoStringed, "Server");
    }

    public Packet getAllUsers(DBManager dbManager) {
        ArrayList<String> allUsersStringed = dbManager.fetchAllUsers();
        return new Packet(Status.SUCCESS, actionType.GET_ALL_USERS, allUsersStringed, "SERVER");
    }

    public Packet getAllChats(DBManager dbManager, AbstractUser thisUser) {
        ArrayList<String> allChatsStringed = dbManager.fetchAllChats(thisUser);
        return new Packet(Status.SUCCESS, actionType.GET_ALL_CHATS, allChatsStringed, "SERVER");
    }

    public Packet getAllMessages(DBManager dbManager, AbstractUser thisUser) {
        ArrayList<String> allMessagesStringed = dbManager.fetchAllMessages(thisUser);
        return new Packet(Status.SUCCESS, actionType.GET_ALL_MESSAGES, allMessagesStringed, "SERVER");
    }
}