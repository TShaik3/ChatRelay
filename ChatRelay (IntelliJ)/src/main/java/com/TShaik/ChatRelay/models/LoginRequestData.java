package com.TShaik.ChatRelay.models;

import chatRelay.AbstractUser;
import chatRelay.DBManager;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestData {
    private String username;
    private String password;

    public Boolean isValid(DBManager dbManager) {
        AbstractUser user = dbManager.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password) && !user.isDisabled();
    }
}
