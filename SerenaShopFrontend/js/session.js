// Manejo de sesión temporal para Fase 1 (sin autenticación)

class Session {
    constructor() {
        this.storageKey = 'serene_shop_session';
        this.init();
    }

    init() {
        // Migrar de sistema temporal a JWT si es necesario
        this.migrateToJWT();
        
        // Verificar si hay sesión JWT
        if (!this.hasJWTSession()) {
            console.log('⚠️ No hay sesión JWT activa');
        } else {
            console.log('✅ Sesión JWT activa');
        }
    }

    // Verificar si hay sesión JWT
    hasJWTSession() {
        return localStorage.getItem('token') !== null;
    }

    // Migrar de sistema temporal a JWT
    migrateToJWT() {
        const oldSession = localStorage.getItem(this.storageKey);
        if (oldSession && !this.hasJWTSession()) {
            // Limpiar sesión temporal antigua
            localStorage.removeItem(this.storageKey);
            console.log('🔄 Sesión temporal eliminada, usar JWT');
        }
    }

    // Obtener ID de usuario desde JWT
    getUsuarioId() {
        if (this.hasJWTSession()) {
            const usuarioId = localStorage.getItem('usuarioId');
            return usuarioId ? parseInt(usuarioId) : null;
        }
        return null;
    }

    // Obtener carrito ID (se mantiene por compatibilidad)
    getCarritoId() {
        const carritoId = localStorage.getItem('carritoId');
        return carritoId ? parseInt(carritoId) : null;
    }

    // Guardar carrito ID
    setCarritoId(carritoId) {
        localStorage.setItem('carritoId', carritoId);
        console.log('✅ CarritoId guardado:', carritoId);
    }

    // Obtener nombre del usuario
    getNombre() {
        if (this.hasJWTSession()) {
            return localStorage.getItem('nombreCompleto') || 'Usuario';
        }
        return 'Invitado';
    }

    // Obtener primer nombre
    getPrimerNombre() {
        const nombreCompleto = this.getNombre();
        return nombreCompleto.split(' ')[0];
    }

    // Verificar si está autenticado
    isAuthenticated() {
        return this.hasJWTSession();
    }

    // Limpiar sesión
    clear() {
        localStorage.removeItem('token');
        localStorage.removeItem('usuarioId');
        localStorage.removeItem('correo');
        localStorage.removeItem('nombreCompleto');
        localStorage.removeItem('rol');
        localStorage.removeItem('carritoId');
        localStorage.removeItem(this.storageKey);
        console.log('🗑️ Sesión limpiada');
    }

    // Obtener token JWT
    getToken() {
        return localStorage.getItem('token');
    }

    // Obtener rol del usuario
    getRol() {
        return localStorage.getItem('rol') || 'INVITADO';
    }
}

// Instancia global
const session = new Session();