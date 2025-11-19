package com.serena.serena_shop.service;

import com.serena.serena_shop.DTOs.AuthResponse;
import com.serena.serena_shop.DTOs.LoginRequest;
import com.serena.serena_shop.DTOs.RegisterRequest;
import com.serena.serena_shop.model.Usuario;
import com.serena.serena_shop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Registrar nuevo usuario
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el correo ya existe
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(request.getCorreo());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setPrimerNombre(request.getPrimerNombre());
        usuario.setSegundoNombre(request.getSegundoNombre());
        usuario.setPrimerApellido(request.getPrimerApellido());
        usuario.setSegundoApellido(request.getSegundoApellido());
        usuario.setCorreo(request.getCorreo());

        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));

        // Asignar rol CLIENTE por defecto
        usuario.setRol("CLIENTE");

        // Fecha de registro
        usuario.setFechaRegistro(LocalDateTime.now());

        // Guardar en base de datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generateToken(
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getUsuarioId(),
                usuarioGuardado.getRol()
        );

        // Retornar respuesta con token
        return new AuthResponse(
                token,
                usuarioGuardado.getUsuarioId(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getNombreCompleto(),
                usuarioGuardado.getRol()
        );
    }

    // Login de usuario
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Correo o contraseña incorrectos");
        }

        // Generar token JWT
        String token = jwtService.generateToken(
                usuario.getCorreo(),
                usuario.getUsuarioId(),
                usuario.getRol()
        );

        // Retornar respuesta con token
        return new AuthResponse(
                token,
                usuario.getUsuarioId(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getRol()
        );
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            String correo = jwtService.extractCorreo(token);
            return jwtService.validateToken(token, correo);
        } catch (Exception e) {
            return false;
        }
    }

    // Obtener usuario desde token
    public Usuario getUserFromToken(String token) {
        String correo = jwtService.extractCorreo(token);
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
