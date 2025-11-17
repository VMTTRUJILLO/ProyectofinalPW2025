package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.MetodoPago;
import com.serena.serena_shop.repository.MetodoPagoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/metodos")
public class MetodoPagoController {
    private final MetodoPagoRepository metodoRepo;

    public MetodoPagoController(MetodoPagoRepository metodoRepo) {
        this.metodoRepo = metodoRepo;
    }

    @GetMapping
    public List<MetodoPago> listar() {
        return metodoRepo.findAll();
    }

    @PostMapping
    public ResponseEntity<MetodoPago> crear(@RequestBody MetodoPago metodo) {
        MetodoPago nuevo = metodoRepo.save(metodo);
        return ResponseEntity.status(201).body(nuevo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> obtener(@PathVariable Integer id) {
        return metodoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
