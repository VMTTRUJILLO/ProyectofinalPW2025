package com.serena.serena_shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.serena.serena_shop.repository.*;
import com.serena.serena_shop.model.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {
        private final PedidoRepository pedidoRepo;
        private final DetallecarritoRepository detalleCarritoRepo;
        private final ProductoRepository productoRepo;
        private final VentaRepository ventaRepo;
        private final DetalleVentaRepository detalleVentaRepo;
        private final carritoRepository carritoRepo;

    public CheckoutService(PedidoRepository pedidoRepo,
                DetallecarritoRepository detalleCarritoRepo,
                ProductoRepository productoRepo,
                VentaRepository ventaRepo,
                DetalleVentaRepository detalleVentaRepo,
                carritoRepository carritoRepo) {
            this.pedidoRepo = pedidoRepo;
            this.detalleCarritoRepo = detalleCarritoRepo;
            this.productoRepo = productoRepo;
            this.ventaRepo = ventaRepo;
            this.detalleVentaRepo = detalleVentaRepo;
            this.carritoRepo = carritoRepo;
        }

    @Transactional
    public Venta procesarCheckout(Integer idUsuario, Integer idMetodoPago) {
        // Intentar obtener un pedido pendiente del usuario
        Pedido pedidoPendiente = pedidoRepo.findByUsuarioIdAndEstado(idUsuario, "PENDIENTE")
                .orElseGet(() -> {
                    // Si no existe, lo creamos a partir del carrito
                    Carrito carrito = carritoRepo.findByUsuarioId(idUsuario)
                            .orElseThrow(() -> new RuntimeException("El usuario no tiene un carrito activo."));

                    List<DetalleCarrito> items = detalleCarritoRepo.findByIdCarrito(carrito.getCarritoId());
                    if (items.isEmpty()) throw new RuntimeException("El carrito está vacío.");

                    // Calcular total del pedido
                    double totalPedido = items.stream()
                            .mapToDouble(i -> i.getCantidad() * i.getPrecioUnitario())
                            .sum();

                    // Crear el pedido
                    Pedido nuevoPedido = new Pedido();
                    nuevoPedido.setUsuarioId(idUsuario);
                    nuevoPedido.setCarritoId(carrito.getCarritoId());
                    nuevoPedido.setEstado("PENDIENTE");
                    nuevoPedido.setTotalPedido( totalPedido);
                    nuevoPedido.setCreadoAt(LocalDateTime.now());

                    return pedidoRepo.save(nuevoPedido);
                });

        // Confirmar el pago (tu método original)
        return confirmarPago(pedidoPendiente.getIdPedido(), idMetodoPago);
    }

        @Transactional
        public Venta confirmarPago (Integer idPedido, Integer idMetodoPago){
            Pedido pedido = pedidoRepo.findById(idPedido).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())) {
                throw new RuntimeException("Pedido no pendiente");
            }

            List<DetalleCarrito> items = detalleCarritoRepo.findByIdCarrito(pedido.getCarritoId());
            if (items.isEmpty()) throw new RuntimeException("Carrito vacío");

            // validar y decrementar stock con bloqueo
            for (DetalleCarrito item : items) {
                Producto p = productoRepo.findByIdForUpdate(item.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getIdProducto()));
                if (p.getStock() < item.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para " + p.getNombreProducto());
                }
                p.setStock(p.getStock() - item.getCantidad());
                productoRepo.save(p);
            }

            // crear venta
            Venta venta = new Venta();
            venta.setUsuarioId(pedido.getUsuarioId());
            venta.setTotalVenta(pedido.getTotalPedido());
            venta.setEstadoVenta("completada");
            venta.setIdMetodoPago(idMetodoPago);
            venta = ventaRepo.save(venta);

            // mover items a detalle_ventas
            for (DetalleCarrito item : items) {
                DetalleVenta dv = new DetalleVenta();
                dv.setIdVenta(venta.getIdVenta());
                dv.setIdProducto(item.getIdProducto());
                dv.setCantidad(item.getCantidad());
                dv.setPrecioUnitario(item.getPrecioUnitario());
                detalleVentaRepo.save(dv);
            }

            // actualizar pedido
            pedido.setEstado("PAGADO");
            pedido.setIdVenta(venta.getIdVenta());
            pedidoRepo.save(pedido);

            // vaciar carrito
            detalleCarritoRepo.deleteAll(items);

            return venta;

        }
    }

