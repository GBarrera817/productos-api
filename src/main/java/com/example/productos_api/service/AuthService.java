package com.example.productos_api.service;

import com.example.productos_api.config.JwtService;
import com.example.productos_api.dto.LoginRequestDTO;
import com.example.productos_api.model.Usuario;
import com.example.productos_api.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UsuarioRepository usuarioRepository,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public Map<String, String> login(LoginRequestDTO dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado pero no encontrado: " + dto.getUsername()));

        String accessToken = jwtService.generarToken(usuario.getUsername());
        String refreshToken = refreshTokenService.crear(usuario);

        return Map.of("token", accessToken, "refreshToken", refreshToken);
    }

    public Map<String, String> refresh(String refreshToken) {

        RefreshTokenService.TokensRotados resultado = refreshTokenService.rotar(refreshToken);

        String accessToken = jwtService.generarToken(resultado.username());

        return Map.of("token", accessToken, "refreshToken", resultado.refreshToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revocar(refreshToken);
    }
}
