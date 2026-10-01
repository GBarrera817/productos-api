package com.example.productos_api.service;

import com.example.productos_api.dto.CategoriaRequestDTO;
import com.example.productos_api.dto.CategoriaResponseDTO;
import com.example.productos_api.model.Categoria;
import com.example.productos_api.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Page<CategoriaResponseDTO> listar(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(this::convertirADTO);
    }

    public Optional<CategoriaResponseDTO> obtenerPorId(Long id) {
        return categoriaRepository.findById(id).map(this::convertirADTO);
    }

    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {

        Categoria categoria = convertirAEntidad(dto);
        Categoria guardado = categoriaRepository.save(categoria);

        return convertirADTO(guardado);
    }

    private CategoriaResponseDTO convertirADTO(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNombre());
    }

    private Categoria convertirAEntidad(CategoriaRequestDTO dto) {

        Categoria categoria = new Categoria();

        categoria.setNombre(dto.getNombre());

        return categoria;
    }
}
