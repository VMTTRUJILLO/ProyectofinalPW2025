package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.Carrito;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface carritoRepository extends JpaRepository<Carrito, Integer> {
    // Método correcto para buscar por usuario_id
    @Query("SELECT c FROM Carrito c WHERE c.usuarioId = :usuarioId")
    Optional<Carrito> findByUsuarioId(@Param("usuarioId") Integer usuarioId);
}
