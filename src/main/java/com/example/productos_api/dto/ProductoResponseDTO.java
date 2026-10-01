package com.example.productos_api.dto;

public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private double precio;
    private String nombreCategoria;

    public ProductoResponseDTO(Long id, String nombre, double precio, String nombreCategoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.nombreCategoria = nombreCategoria;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }
}
