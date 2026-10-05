package com.example.productos_api.repository;

import com.example.productos_api.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @EntityGraph(attributePaths = "categoria")
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    @EntityGraph(attributePaths = "categoria")
    List<Producto> findByPrecioGreaterThan(double precio);

    @EntityGraph(attributePaths = "categoria")
    List<Producto> findByPrecioBetween(double precioMin, double precioMax);

    @Query("Select p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.precio <= :precioMax")
    List<Producto> buscarPorCategoriaYPrecioMax(@Param("categoriaId") Long categoriaId, @Param("precioMax") double precioMax);

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findAll(Pageable pageable);
}
