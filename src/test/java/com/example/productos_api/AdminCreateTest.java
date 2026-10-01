package com.example.productos_api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminCreateTest {

    @Test
    void generarHashParaAdmin() {
        String hash = new BCryptPasswordEncoder().encode("clave-admin-segura");
        System.out.println(hash);
    }
}
