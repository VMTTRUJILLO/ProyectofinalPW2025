package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Carrito;
import com.serena.serena_shop.model.DetalleCarrito;
import com.serena.serena_shop.model.Producto;
import com.serena.serena_shop.repository.DetallecarritoRepository;
import com.serena.serena_shop.repository.ProductoRepository;
import com.serena.serena_shop.repository.carritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detallecarrito")
public class DetalleCarritoController {
    @Autowired
    private DetallecarritoRepository detalleCarritoRepo;
    @Autowired
    private carritoRepository carritoRepo;



    @Autowired
    private DetallecarritoRepository detalleCarritoRepository;

    @Autowired
    private carritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping
    public ResponseEntity<?> agregarDetalle(@RequestBody Map<String, Object> body) {

        try {
            // === 1. OBTENER CARRITO ID ===
            Map<String, Object> carritoMap = (Map<String, Object>) body.get("carrito");
            Integer carritoId = (Integer) carritoMap.get("carritoId");

            // === 2. OBTENER PRODUCTO ID ===
            Integer idProducto = (Integer) body.get("idProducto");

            // === 3. OBTENER CANTIDAD ===
            Integer cantidad = (Integer) body.get("cantidad");

            // === 4. OBTENER PRECIO UNITARIO ===
            Double precioUnitario = Double.valueOf(body.get("precioUnitario").toString());

            // === 5. VALIDACIONES ===
            Carrito carrito = carritoRepository.findById(carritoId)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < cantidad) {
                return ResponseEntity.badRequest().body("Stock insuficiente");
            }

            // === 6. CREAR DETALLE ===
            DetalleCarrito detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);

            // === 7. GUARDAR ===
            detalleCarritoRepository.save(detalle);

            return ResponseEntity.ok(detalle);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Error al agregar producto al carrito: " + e.getMessage());
        }
    }



    // Listar los productos del carrito de un usuario
    @GetMapping("/carrito/{carritoId}")
    public List<DetalleCarrito> listarPorCarrito(@PathVariable Integer carritoId) {
        return detalleCarritoRepo.findByIdCarrito(carritoId);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDetalle(@PathVariable Integer id) {

        if (!detalleCarritoRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "El detalle no existe"
            ));
        }

        detalleCarritoRepository.deleteById(id);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto eliminado del carrito"
        ));
    }
}
