package com.serena.serena_shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "productos")

public class Producto {
    @Id
    @Column(name = "id_producto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "disponible", insertable=false, updatable=false)
    private Boolean disponible;

    // getters & setters

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}
