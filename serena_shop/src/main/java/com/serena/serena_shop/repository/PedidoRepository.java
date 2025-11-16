package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.Pedido;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Integer> {

    // 🔹 Buscar pedidos por usuario
    List<Pedido> findByUsuarioId(Integer usuarioId);

    Optional<Pedido> findByUsuarioIdAndEstado(Integer usuarioId, String estado);
    // 🔹 Buscar pedido por carrito (por si lo usas en crearPedido)
    Optional<Pedido> findByCarritoId(Integer carritoId);

}
