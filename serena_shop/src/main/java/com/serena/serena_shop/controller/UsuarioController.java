package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Usuario;
import com.serena.serena_shop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    @Autowired
    private UsuarioRepository UsuarioRepository;
    private final UsuarioRepository usuarioRepo; 

    public UsuarioController(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        return usuarioRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario u) {
        Usuario nuevo = usuarioRepo.save(u);
        return ResponseEntity.status(201).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetalles) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPrimerNombre(usuarioDetalles.getPrimerNombre());
        usuario.setSegundoNombre(usuarioDetalles.getSegundoNombre());
        usuario.setPrimerApellido(usuarioDetalles.getPrimerApellido());
        usuario.setSegundoApellido(usuarioDetalles.getSegundoApellido());
        usuario.setCorreo(usuarioDetalles.getCorreo());
        usuario.setContrasena(usuarioDetalles.getContrasena());
        usuario.setRol(usuarioDetalles.getRol());

        Usuario actualizado = usuarioRepo.save(usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = UsuarioRepository.findById(id);
        if (usuario.isPresent()) {
            UsuarioRepository.deleteById(id);
            return ResponseEntity.ok("Usuario con ID " + id + " eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}
