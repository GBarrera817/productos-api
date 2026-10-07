package com.example.productos_api.repository;

import com.example.productos_api.model.RefreshToken;
import com.example.productos_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken r set r.revocado = true where r.usuario=:usuario")
    void revocarTodosDelUsuario(@Param("usuario") Usuario usuario);
}
