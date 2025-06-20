package org.example.tokenauth.controller;

import lombok.RequiredArgsConstructor;
import org.example.tokenauth.service.TokenVerification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenVerification tokenVerification;

    @GetMapping("/register")
    public UUID register() {
        return tokenVerification.generateToken();
    }
}
