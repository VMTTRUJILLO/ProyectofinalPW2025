package com.serena.serena_shop.config;

import com.serena.serena_shop.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements Filter {
    @Autowired
    private JwtService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String authHeader = httpRequest.getHeader("Authorization");

            // ✅ IMPORTANTE: Solo procesar si HAY token
            // Si no hay token, simplemente dejar pasar la petición
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);

                    // Extraer información del token
                    String correo = jwtService.extractCorreo(token);
                    String rol = jwtService.extractRol(token);

                    // Solo autenticar si no hay autenticación previa
                    if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Validar el token
                        if (jwtService.validateToken(token, correo)) {
                            // Crear autenticación con el rol
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            correo,
                                            null,
                                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                                    );

                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(httpRequest)
                            );

                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            System.out.println("✅ Usuario autenticado: " + correo + " | Rol: " + rol);
                        } else {
                            System.out.println("⚠️ Token inválido o expirado");
                        }
                    }
                } catch (Exception tokenError) {
                    // ✅ Si hay error al procesar el token, logear pero NO bloquear
                    System.err.println("⚠️ Error al procesar token: " + tokenError.getMessage());
                }
            } else {
                // ✅ No hay token - usuario no logueado (esto es NORMAL para rutas públicas)
                System.out.println("ℹ️ Petición sin token: " + httpRequest.getRequestURI());
            }
        } catch (Exception e) {
            // ✅ Cualquier error general - logear pero NO bloquear
            System.err.println("❌ Error general en JWT Filter: " + e.getMessage());
        }

        // ✅ CRÍTICO: SIEMPRE continuar con la cadena de filtros
        // Esto permite que las rutas públicas funcionen sin token
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("🔐 JWT Filter inicializado");
    }

    @Override
    public void destroy() {
        System.out.println("🔐 JWT Filter destruido");
    }
}
