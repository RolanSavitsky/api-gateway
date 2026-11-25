package com.inno.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CreateUserRequest {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
