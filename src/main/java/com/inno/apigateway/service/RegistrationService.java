package com.inno.apigateway.service;

import com.inno.apigateway.dto.CreateUserRequest;
import com.inno.apigateway.dto.CreateUserResponse;
import com.inno.apigateway.dto.RegisterRequest;
import com.inno.apigateway.dto.RegisterResponse;
import com.inno.apigateway.dto.SaveCredentialsRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RegistrationService {

    private static final String DEFAULT_ROLE = "USER";

    private final WebClient userWebClient;
    private final WebClient authWebClient;

    public RegistrationService(@Qualifier("userWebClient") WebClient userWebClient,
                               @Qualifier("authWebClient") WebClient authWebClient) {

        this.userWebClient = userWebClient;
        this.authWebClient = authWebClient;
    }

    public Mono<RegisterResponse> register(RegisterRequest request) {
        return createUser(request)
                .flatMap(user -> saveCredentials(user.getId(), request)
                        .thenReturn(new RegisterResponse(user.getId(), request.getLogin()))
                        .onErrorResume(error -> rollBackUser(user.getId())
                                .then(Mono.error(error))));
    }

    private Mono<CreateUserResponse> createUser(RegisterRequest request) {
        CreateUserRequest body = new CreateUserRequest(
                request.getName(), request.getSurname(),
                request.getBirthDate(), request.getEmail());

        return userWebClient.post()
                .uri("/internal/users")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CreateUserResponse.class);
    }

    private Mono<Void> saveCredentials(Long userId, RegisterRequest request) {
        SaveCredentialsRequest body = new SaveCredentialsRequest(
                userId, request.getLogin(), request.getPassword(), DEFAULT_ROLE);

        return authWebClient.post()
                .uri("/auth/credentials")
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    private Mono<Void> rollBackUser(Long userId) {
        return userWebClient.delete()
                .uri("/internal/users/{id}", userId)
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorResume(exception -> Mono.empty());
    }
}
