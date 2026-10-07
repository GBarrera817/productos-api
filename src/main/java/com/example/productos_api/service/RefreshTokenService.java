package com.example.productos_api.service;

import com.example.productos_api.exception.TokenInvalidoException;
import com.example.productos_api.model.RefreshToken;
import com.example.productos_api.model.Usuario;
import com.example.productos_api.repository.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final SecureRandom RAMDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long expiracionDias;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, @Value("${jwt.refresh-expiration-days:7}") long expiracionDias) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.expiracionDias = expiracionDias;
    }

    private String generarTokenAleatorio() {
        byte[] bytes = new byte[32];

        RAMDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public String crear(Usuario usuario) {
        String tokenEnClaro = generarTokenAleatorio();

        RefreshToken entidad = new RefreshToken();
        entidad.setUsuario(usuario);
        entidad.setTokenHash(hash(tokenEnClaro));
        entidad.setExpiraEn(Instant.now().plus(expiracionDias, ChronoUnit.DAYS));
        entidad.setRevocado(false);
        refreshTokenRepository.save(entidad);

        return tokenEnClaro;
    }

    public record TokensRotados(String username, String refreshToken) {}

    @Transactional(noRollbackFor = TokenInvalidoException.class)
    public TokensRotados rotar(String tokenRecibido) {

        RefreshToken actual = refreshTokenRepository.findByTokenHash(hash(tokenRecibido))
                .orElseThrow(() -> new TokenInvalidoException("Refresh token inválido"));

        if (actual.isRevocado()) {
            refreshTokenRepository.revocarTodosDelUsuario(actual.getUsuario());

            throw new TokenInvalidoException("Refresh token reutilizado");
        }

        if (actual.getExpiraEn().isBefore(Instant.now())) {
            throw new TokenInvalidoException("Refresh token expirado");
        }

        actual.setRevocado(true);

        String nuevo = crear(actual.getUsuario());

        return new TokensRotados(actual.getUsuario().getUsername(), nuevo);
    }

    private String hash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] resultado = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(resultado);
        } catch (NoSuchAlgorithmException e ) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
