package com.inno.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveCredentialsRequest {
    private Long userId;
    private String login;
    private String password;
    private String role;
}
