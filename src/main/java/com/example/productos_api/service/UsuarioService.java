package com.example.productos_api.service;

import com.example.productos_api.dto.UsuarioRequestDTO;
import com.example.productos_api.dto.UsuarioResponseDTO;
import com.example.productos_api.model.Rol;
import com.example.productos_api.model.Usuario;
import com.example.productos_api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
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
}
