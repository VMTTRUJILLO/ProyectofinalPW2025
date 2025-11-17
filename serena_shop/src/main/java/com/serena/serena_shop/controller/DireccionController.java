package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.DireccionUsuario;
import com.serena.serena_shop.repository.DireccionUsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {
    private final DireccionUsuarioRepository direccionRepo;

    public DireccionController(DireccionUsuarioRepository direccionRepo) {
        this.direccionRepo = direccionRepo;
    }

    @PostMapping
    public ResponseEntity<DireccionUsuario> crear(@RequestBody DireccionUsuario dir) {
        DireccionUsuario nueva = direccionRepo.save(dir);
        return ResponseEntity.status(201).body(nueva);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<DireccionUsuario> listarPorUsuario(@PathVariable Integer usuarioId) {
        return direccionRepo.findByUsuarioId(usuarioId);
    }
}
