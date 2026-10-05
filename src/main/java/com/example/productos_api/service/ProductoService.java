package com.example.productos_api.service;

import com.example.productos_api.dto.ProductoRequestDTO;
import com.example.productos_api.dto.ProductoResponseDTO;
import com.example.productos_api.model.Categoria;
import com.example.productos_api.model.Producto;
import com.example.productos_api.repository.CategoriaRepository;
import com.example.productos_api.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

//    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listar(Pageable pageable) {
        return productoRepository.findAll(pageable).map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id).map(this::convertirADTO);
    }

    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto producto = convertirAEntidad(dto);
        Producto guardado = productoRepository.save(producto);

        return convertirADTO(guardado);
    }

    @Transactional
    public Optional<ProductoResponseDTO> actualizar(Long id, ProductoRequestDTO dto) {

        return productoRepository.findById(id)
                .map(productoExistente -> {
                    productoExistente.setNombre(dto.getNombre());
                    productoExistente.setPrecio(dto.getPrecio());

                    if (dto.getCategoriaId() != null) {
                        productoExistente.setCategoria(buscarCategoria(dto.getCategoriaId()));
                    }

                    Producto actualizado = productoRepository.save(productoExistente);

                    return convertirADTO(actualizado);
                });
    }

    @Transactional
    public boolean eliminar(Long id) {

        if (!productoRepository.existsById(id)) {
            return false;
        }

        productoRepository.deleteById(id);

        return true;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {

        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    private Categoria buscarCategoria(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
    }

    private ProductoResponseDTO convertirADTO(Producto p) {

        String nombreCategoria = p.getCategoria()== null ? null : p.getCategoria().getNombre();

        return new ProductoResponseDTO(p.getId(), p.getNombre(), p.getPrecio(), nombreCategoria);
    }

    private Producto convertirAEntidad(ProductoRequestDTO dto) {

        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        if (dto.getCategoriaId() != null) {
            producto.setCategoria(buscarCategoria(dto.getCategoriaId()));
        }

        return producto;
    }
}
