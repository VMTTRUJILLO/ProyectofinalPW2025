package com.serena.serena_shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {
    @Id
    @Column(name = "id_detalle")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;

    @Column(name = "id_venta")
    private Integer idVenta;

    @ManyToOne(fetch = FetchType.EAGER) // Asegura que el Producto se cargue con el detalle
    @JoinColumn(name = "id_producto", insertable = false, updatable = false)
    private Producto producto;

    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "subtotal", insertable=false, updatable=false)
    private Integer subtotal;

    // getters & setters
}
