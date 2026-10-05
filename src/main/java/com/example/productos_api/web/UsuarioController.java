package com.example.productos_api.web;

import com.example.productos_api.dto.UsuarioRequestDTO;
import com.example.productos_api.dto.UsuarioResponseDTO;
import com.example.productos_api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuarios/registro")
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.crear(dto);
    }

    //CODIGO ANTES DE LA CAPA SERVICE
    /*

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/usuarios/registro")
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())){
            throw new IllegalArgumentException("El username ya existe: " + dto.getUsername());
        }

        Usuario usuario = convertirAEntidad(dto);
        Usuario guardado = usuarioRepository.save(usuario);

        return convertirADTO(guardado);
    }

    private UsuarioResponseDTO convertirADTO(Usuario u) {
        return new UsuarioResponseDTO(u.getUsername());
    }

    private Usuario convertirAEntidad(UsuarioRequestDTO dto) {

        Usuario usuario = new Usuario();

        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.USER);

        return usuario;
    }

     */

}
