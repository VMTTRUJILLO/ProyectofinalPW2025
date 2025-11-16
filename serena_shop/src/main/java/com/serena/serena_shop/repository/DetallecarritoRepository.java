package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.DetalleCarrito;
import java.util.List;
public interface DetallecarritoRepository extends JpaRepository<DetalleCarrito,Integer> {
    List<DetalleCarrito> findByCarrito_CarritoId(Integer carritoId);
}
