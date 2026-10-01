package com.example.productos_api.web;


import com.example.productos_api.dto.CategoriaRequestDTO;
import com.example.productos_api.dto.CategoriaResponseDTO;
import com.example.productos_api.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/categorias")
    public Page<CategoriaResponseDTO> listar(Pageable pageable) {

        return categoriaService.listar(pageable);
    }

    @PostMapping("/categorias")
    public CategoriaResponseDTO crear(@Valid @RequestBody CategoriaRequestDTO dto) {

        return categoriaService.crear(dto);
    }


    //CODIGO ANTES DE LA CAPA SERVICE
    /*
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/categorias")
    public Page<CategoriaResponseDTO> listar(Pageable pageable) {

        return categoriaRepository.findAll(pageable).map(this::convertirADTO);
    }

    @PostMapping("/categorias")
    public CategoriaResponseDTO crear(@Valid @RequestBody CategoriaRequestDTO dto) {

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

     */
}
