package by.effective.mobile.eb.controller;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;

    private long expiresIn;
}
