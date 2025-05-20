package com.TShaik.ChatRelay.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestData {
    private String username;
    private String password;
}
