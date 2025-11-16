package com.serena.serena_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.serena.serena_shop.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findBycorreo(String correo);
}
