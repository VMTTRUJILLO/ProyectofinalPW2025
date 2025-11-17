package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Venta;
import com.serena.serena_shop.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    @Autowired
    private VentaRepository ventaRepo;

    @GetMapping
    public List<Venta> listar() {
        return ventaRepo.findAll();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Venta>> obtenerVentasPorUsuario(@PathVariable Integer idUsuario) {
        List<Venta> ventas = ventaRepo.findByUsuarioId(idUsuario);
        if (ventas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtener(@PathVariable Integer id) {
        return ventaRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
