package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Carrito;
import com.serena.serena_shop.model.DetalleCarrito;
import com.serena.serena_shop.model.Pedido;
import com.serena.serena_shop.model.Usuario;
import com.serena.serena_shop.repository.DetallecarritoRepository;
import com.serena.serena_shop.repository.PedidoRepository;
import com.serena.serena_shop.repository.UsuarioRepository;
import com.serena.serena_shop.repository.carritoRepository;
import com.serena.serena_shop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private carritoRepository carritoRepo;

    @Autowired
    private DetallecarritoRepository detalleCarritoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    private final   CheckoutService checkoutService;

    public PedidoController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }


    // Método 1: Crear pedido - Debe devolver JSON
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Integer> body) {
        try {
            Integer idUsuario = body.get("idUsuario");
            Usuario usuario = usuarioRepo.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Carrito carrito = carritoRepo.findByUsuarioId(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

            List<DetalleCarrito> detalles = detalleCarritoRepo.findByCarrito_CarritoId(carrito.getCarritoId());
            if (detalles.isEmpty()) {
                throw new RuntimeException("El carrito está vacío");
            }

            double totalpedido = detalles.stream()
                    .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                    .sum();

            Pedido pedido = new Pedido();
            pedido.setUsuarioId(idUsuario);
            pedido.setCarritoId(carrito.getCarritoId());
            pedido.setTotalPedido(totalpedido);
            pedido.setEstado("PENDIENTE");
            pedido.setCreadoAt(LocalDateTime.now());

            Pedido pedidoGuardado = pedidoRepo.save(pedido);

            // Devolver el pedido como JSON
            return ResponseEntity.ok(pedidoGuardado);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // 1️⃣ Listar todos los pedidos
    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoRepo.findAll();
    }

    //listar por usuario id usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pedido>> listarPedidosPorUsuario(@PathVariable Integer idUsuario) {
        List<Pedido> pedidos = pedidoRepo.findByUsuarioId(idUsuario);
        if (pedidos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(pedidos);
    }

    // 2️⃣ Buscar un pedido por ID
    @GetMapping("/{id}")
    public Optional<Pedido> obtenerPedido(@PathVariable Integer id) {
        return pedidoRepo.findById(id);
    }

    // Método 2: Checkout - Debe devolver JSON
    @PostMapping("/checkout")
    public ResponseEntity<?> procesarCheckout(@RequestBody Map<String, Object> request) {
        try {
            Integer idUsuario = (Integer) request.get("idUsuario");
            Integer idMetodoPago = (Integer) request.get("idMetodoPago");

            checkoutService.procesarCheckout(idUsuario, idMetodoPago);

            // Devolver JSON en vez de String
            Map<String, String> response = new HashMap<>();
            response.put("message", "Compra procesada correctamente");
            response.put("usuarioId", idUsuario.toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // 4️⃣ Eliminar un pedido (solo si es necesario)
    @DeleteMapping("/{id}")
    public String eliminarPedido(@PathVariable Integer id) {
        pedidoRepo.deleteById(id);
        return "Pedido con ID " + id + " eliminado correctamente.";
    }
}
