package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.DireccionUsuario;
import java.util.List;
public interface DireccionUsuarioRepository extends JpaRepository<DireccionUsuario,Integer> {
    List<DireccionUsuario> findByUsuarioId(Integer usuarioId);
}