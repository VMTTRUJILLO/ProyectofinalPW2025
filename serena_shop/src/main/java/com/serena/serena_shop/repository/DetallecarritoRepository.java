package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.DetalleCarrito;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface DetallecarritoRepository extends JpaRepository<DetalleCarrito,Integer> {
    // Buscar por id_carrito (no por carrito_id)
    @Query("SELECT dc FROM DetalleCarrito dc WHERE dc.carrito.carritoId = :idCarrito")
    List<DetalleCarrito> findByIdCarrito(@Param("idCarrito") Integer idCarrito);
}
