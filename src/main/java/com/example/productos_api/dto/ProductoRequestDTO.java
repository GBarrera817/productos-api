package com.example.productos_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Positive(message = "El precio debe ser mayor a 0.")
    private double precio;

    private Long categoriaId;

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
