package com.example.productos_api.dto;

public class UsuarioResponseDTO {

    private String username;

    public UsuarioResponseDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
