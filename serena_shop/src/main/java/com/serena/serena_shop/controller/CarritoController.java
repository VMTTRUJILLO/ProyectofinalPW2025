package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Carrito;
import com.serena.serena_shop.model.DetalleCarrito;
import com.serena.serena_shop.repository.carritoRepository;
import com.serena.serena_shop.repository.DetallecarritoRepository;
import com.serena.serena_shop.repository.ProductoRepository;
import com.serena.serena_shop.model.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    private final carritoRepository carritoRepo;
    private final DetallecarritoRepository detalleRepo;
    private final ProductoRepository productoRepo;

    public CarritoController(carritoRepository carritoRepo, DetallecarritoRepository detalleRepo, ProductoRepository productoRepo) {
        this.carritoRepo = carritoRepo;
        this.detalleRepo = detalleRepo;
        this.productoRepo = productoRepo;
    }

    @PostMapping
    public ResponseEntity<Carrito> crearCarrito(@RequestBody Carrito c) {
        Carrito nuevo = carritoRepo.save(c);
        return ResponseEntity.status(201).body(nuevo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Integer id) {
        return carritoRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar carrito por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Carrito> obtenerCarritoPorUsuario(@PathVariable Integer usuarioId) {
        Optional<Carrito> carrito = carritoRepo.findByUsuarioId(usuarioId);
        return carrito
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{idCarrito}/items")
    public ResponseEntity<DetalleCarrito> agregarItem(@PathVariable Integer idCarrito, @RequestBody DetalleCarrito item) {
        Carrito carrito = carritoRepo.findById(idCarrito).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        Producto producto = productoRepo.findById(item.getIdProducto()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        item.setCarrito(carrito);
        item.setPrecioUnitario(producto.getPrecio());
        DetalleCarrito nuevo = detalleRepo.save(item);
        return ResponseEntity.status(201).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Integer id, @RequestBody Carrito datosActualizados) {
        return carritoRepo.findById(id)
                .map(carrito -> {
                    carrito.setUsuarioId(datosActualizados.getUsuarioId());
                    carrito.setSessionId(datosActualizados.getSessionId());
                    carrito.setCreadoAt(datosActualizados.getCreadoAt());
                    Carrito actualizado = carritoRepo.save(carrito);
                    return ResponseEntity.ok(actualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
