package com.example.productos_api.web;

import com.example.productos_api.dto.ProductoRequestDTO;
import com.example.productos_api.dto.ProductoResponseDTO;
import com.example.productos_api.model.Categoria;
import com.example.productos_api.model.Producto;
import com.example.productos_api.repository.CategoriaRepository;
import com.example.productos_api.repository.ProductoRepository;
import com.example.productos_api.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    public Page<ProductoResponseDTO> listar(Pageable pageable) {
        return productoService.listar(pageable);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoResponseDTO> get(@PathVariable Long id) {

        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productos")
    public ProductoResponseDTO crear(@Valid @RequestBody ProductoRequestDTO dto) {
        return productoService.crear(dto);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {

        return productoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){

        return productoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/productos/buscar")
    public List<ProductoResponseDTO> buscarPorNombre(@RequestParam String nombre) {

        return productoService.buscarPorNombre(nombre);
    }

    /* Código sin uso de la capa intermedia Servic */

    /*
    private ProductoResponseDTO convertirADTO(Producto p) {

        String nombreCategoria = p.getCategoria() == null ? null : p.getCategoria().getNombre();

        return new ProductoResponseDTO(p.getId(), p.getNombre(), p.getPrecio(), nombreCategoria);
    }

    private Producto convertirAEntidad(ProductoRequestDTO dto){
        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        if (dto.getCategoriaId() != null){
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + dto.getCategoriaId()));

            producto.setCategoria(categoria);
        }

        return producto;
    }

    private Categoria buscarCategoria(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));
    }
    */

    /* Código de ejemplo sin uso de DTO */

    /*
    @GetMapping("/productos")
    public Page<Producto> listar(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> get(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productos")
    public Producto crear(@Valid @RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){

        if (!productoRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        productoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto datos) {

        return productoRepository.findById(id)
                .map(productoExistente -> {
                    productoExistente.setNombre(datos.getNombre());
                    productoExistente.setPrecio(datos.getPrecio());

                    Producto actualizado = productoRepository.save(productoExistente);

                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Búsquedas

    @GetMapping("/productos/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre){
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @GetMapping("/productos/filtrar")
    public List<Producto> filtrar(@RequestParam Long categoriaId, @RequestParam double precioMax){
        return productoRepository.buscarPorCategoriaYPrecioMax(categoriaId, precioMax);
    }


     */

}
