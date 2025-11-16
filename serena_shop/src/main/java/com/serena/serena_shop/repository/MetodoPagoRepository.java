package com.serena.serena_shop.repository;

import com.serena.serena_shop.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago,Integer> {
}
