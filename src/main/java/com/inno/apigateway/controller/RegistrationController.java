package com.inno.apigateway.controller;

import com.inno.apigateway.dto.RegisterRequest;
import com.inno.apigateway.dto.RegisterResponse;
import com.inno.apigateway.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}