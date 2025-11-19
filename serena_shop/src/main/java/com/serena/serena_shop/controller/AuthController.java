package com.serena.serena_shop.controller;

import com.serena.serena_shop.DTOs.AuthResponse;
import com.serena.serena_shop.DTOs.LoginRequest;
import com.serena.serena_shop.DTOs.RegisterRequest;
import com.serena.serena_shop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("=== REGISTRO DE USUARIO ===");
            System.out.println("Correo: " + request.getCorreo());
            System.out.println("Nombre: " + request.getPrimerNombre() + " " + request.getPrimerApellido());

            AuthResponse response = authService.register(request);

            System.out.println("✅ Usuario registrado exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== LOGIN DE USUARIO ===");
            System.out.println("Correo: " + request.getCorreo());

            AuthResponse response = authService.login(request);

            System.out.println("✅ Login exitoso");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Validar token
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Token no proporcionado");
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);

            if (isValid) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
