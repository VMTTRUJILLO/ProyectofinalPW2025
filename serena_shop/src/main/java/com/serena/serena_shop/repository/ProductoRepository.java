package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.serena.serena_shop.model.Producto;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
public interface ProductoRepository extends JpaRepository <Producto, Integer>{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Integer id);

    List<Producto> findByIdCategoria(Integer idCategoria);

    List<Producto> findByDisponibleTrue();

    List<Producto> findByNombreProductoContainingIgnoreCase(String nombreProducto);
}

