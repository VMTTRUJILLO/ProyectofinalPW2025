package com.serena.serena_shop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "direccion_usuarios")
public class DireccionUsuario implements Serializable {

    @Id
    @Column(name = "direccion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer direccionId;

    // FK hacia usuarios(usuario_id)
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "telefono")
    private String telefono;

    // Relación ManyToOne opcional para navegar desde la dirección al usuario.
    // Mapeamos por usuario_id; no hacemos cascade remove (no queremos borrar usuario al borrar dirección).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    public DireccionUsuario() {}

    // Getters & setters

    public Integer getDireccionId() {
        return direccionId;
    }
    public void setDireccionId(Integer direccionId) {
        this.direccionId = direccionId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) this.usuarioId = usuario.getUsuarioId();
    }
}
