package com.serena.serena_shop.DTOs;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Integer usuarioId;
    private String correo;
    private String nombreCompleto;
    private String rol;

    public AuthResponse(String token, Integer usuarioId, String correo, String nombreCompleto, String rol) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
