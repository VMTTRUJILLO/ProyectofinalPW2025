package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.DetalleCarrito;
import com.serena.serena_shop.repository.DetallecarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detallecarrito")
public class DetalleCarritoController {
    @Autowired
    private DetallecarritoRepository detalleCarritoRepo;

    @PostMapping
    public DetalleCarrito agregarProducto(@RequestBody DetalleCarrito detalle) {
        System.out.println("✅ Recibido: " + detalle);
        return detalleCarritoRepo.save(detalle);
    }



    // Listar los productos del carrito de un usuario
    @GetMapping("/carrito/{carritoId}")
    public List<DetalleCarrito> listarPorCarrito(@PathVariable Integer carritoId) {
        return detalleCarritoRepo.findByCarrito_CarritoId(carritoId);
    }

    // Actualizar cantidad de un producto en el carrito
    @PutMapping("/{id}")
    public DetalleCarrito actualizarCantidad(@PathVariable Integer id, @RequestBody DetalleCarrito detalle) {
        DetalleCarrito existente = detalleCarritoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
        existente.setCantidad(detalle.getCantidad());
        existente.setPrecioUnitario(detalle.getPrecioUnitario());
        return detalleCarritoRepo.save(existente);
    }

    // Eliminar producto del carrito
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Integer id) {
        try {
            if (!detalleCarritoRepo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            detalleCarritoRepo.deleteById(id);
            return ResponseEntity.ok("Producto eliminado del carrito");
        } catch (Exception e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return ResponseEntity.status(500).body("Error al eliminar: " + e.getMessage());
        }
    }
}
