package com.example.productos_api.repository;

import com.example.productos_api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByPrecioGreaterThan(double precio);
    List<Producto> findByPrecioBetween(double precioMin, double precioMax);

    @Query("Select p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.precio <= :precioMax")
    List<Producto> buscarPorCategoriaYPrecioMax(@Param("categoriaId") Long categoriaId, @Param("precioMax") double precioMax);
}
