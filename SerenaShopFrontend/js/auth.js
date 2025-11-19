// Sistema de Autenticación JWT
(function() {
    'use strict';
    
    const API_URL = 'http://localhost:8086/api';

    // REGISTRO
    window.register = async function(userData) {
        try {
            console.log('📝 Intentando registrar:', userData);
            
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            const data = await response.json();
            console.log('Respuesta servidor:', data);

            if (response.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('usuarioId', data.usuarioId);
                localStorage.setItem('correo', data.correo);
                localStorage.setItem('nombreCompleto', data.nombreCompleto);
                localStorage.setItem('rol', data.rol);
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Error al registrar' };
            }
        } catch (error) {
            console.error('Error:', error);
            return { success: false, error: 'Error de conexión. ¿El backend está corriendo?' };
        }
    };

    // LOGIN
    window.login = async function(correo, contrasena) {
        try {
            console.log('🔐 Login:', correo);
            
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ correo, contrasena })
            });

            const data = await response.json();
            console.log('Respuesta servidor:', data);

            if (response.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('usuarioId', data.usuarioId);
                localStorage.setItem('correo', data.correo);
                localStorage.setItem('nombreCompleto', data.nombreCompleto);
                localStorage.setItem('rol', data.rol);
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Credenciales incorrectas' };
            }
        } catch (error) {
            console.error('Error:', error);
            return { success: false, error: 'Error de conexión. ¿El backend está corriendo?' };
        }
    };

    // LOGOUT
    window.logout = function() {
        localStorage.clear();
        window.location.href = 'login.html';
    };

    // VERIFICAR AUTENTICACIÓN
    window.isAuthenticated = function() {
        return localStorage.getItem('token') !== null;
    };

    // OBTENER DATOS
    window.getUserData = function() {
        return {
            usuarioId: parseInt(localStorage.getItem('usuarioId')),
            correo: localStorage.getItem('correo'),
            nombreCompleto: localStorage.getItem('nombreCompleto'),
            rol: localStorage.getItem('rol')
        };
    };

    // OBTENER PRIMER NOMBRE
    window.getPrimerNombre = function() {
        const nombre = localStorage.getItem('nombreCompleto');
        return nombre ? nombre.split(' ')[0] : 'Usuario';
    };

    console.log('✅ auth.js cargado');
})();