package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.Carrito;

import java.util.Optional;

public interface carritoRepository extends JpaRepository<Carrito, Integer> {
        Optional<Carrito> findByUsuarioId(Integer usuarioId);
}
