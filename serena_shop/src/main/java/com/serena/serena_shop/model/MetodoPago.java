package com.serena.serena_shop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "metodos_pago")
public class MetodoPago {
    @Id
    @Column(name = "id_metodo_pago")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMetodoPago;

    @Column(name = "nombre_metodo")
    private String nombreMetodo;

    @Column(name = "proveedor")
    private String proveedor;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // getters & setters
}
