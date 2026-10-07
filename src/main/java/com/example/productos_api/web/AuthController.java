package com.example.productos_api.web;

import com.example.productos_api.dto.LoginRequestDTO;
import com.example.productos_api.dto.RefreshRequestDTO;
import com.example.productos_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequestDTO dto) {
        return authService.login(dto);
    }

    @PostMapping("/auth/refresh")
    public Map<String, String> refresh(@Valid @RequestBody RefreshRequestDTO dto) { return authService.refresh(dto.getRefreshToken()); }
}
