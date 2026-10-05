package com.example.productos_api.dto;

import jakarta.validation.constraints.NotBlank;

public class UsuarioRequestDTO {

    @NotBlank(message = "El username no puede estar vacío.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;

    public String getUsername() { return username; }

    public String getPassword() { return password; }

     public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

}
