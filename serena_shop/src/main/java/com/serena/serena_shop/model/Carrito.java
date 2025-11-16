package com.serena.serena_shop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carrito")
public class Carrito {
    @Id
    @Column(name = "carrito_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carritoId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "creado_at")
    private LocalDateTime creadoAt;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> items;

    // getters & setters


    public Integer getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Integer carritoId) {
        this.carritoId = carritoId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreadoAt() {
        return creadoAt;
    }

    public void setCreadoAt(LocalDateTime creadoAt) {
        this.creadoAt = creadoAt;
    }

    public List<DetalleCarrito> getItems() {
        return items;
    }

    public void setItems(List<DetalleCarrito> items) {
        this.items = items;
    }
}
