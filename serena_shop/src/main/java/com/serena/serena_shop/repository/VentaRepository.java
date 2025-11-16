package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.Venta;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // Obtener el historial de compras de un usuario
    List<Venta> findByUsuarioId(Integer usuarioId);
}
