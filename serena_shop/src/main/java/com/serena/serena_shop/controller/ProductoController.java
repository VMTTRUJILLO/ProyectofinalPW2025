package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Producto;
import com.serena.serena_shop.repository.ProductoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoRepository productoRepo;

    public ProductoController(ProductoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return productoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoRepo.save(producto);
        return ResponseEntity.status(201).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto p) {
        return productoRepo.findById(id)
                .map(existente -> {
                    existente.setNombreProducto(p.getNombreProducto());
                    existente.setPrecio(p.getPrecio());
                    existente.setStock(p.getStock());
                    existente.setImagen(p.getImagen());
                    return ResponseEntity.ok(productoRepo.save(existente));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        if (!productoRepo.existsById(id)) return ResponseEntity.notFound().build();
        productoRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
