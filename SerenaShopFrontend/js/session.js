// Manejo de sesión temporal para Fase 1 (sin autenticación)

class Session {
    constructor() {
        this.storageKey = 'serene_shop_session';
        this.init();
    }

    init() {
        let session = this.getSession();
        if (!session) {
            // Crear sesión temporal
            session = {
                usuarioId: USUARIO_TEMPORAL_ID,
                carritoId: null, // Se obtendrá del backend
                nombre: 'Usuario Temporal'
            };
            this.saveSession(session);
        }
    }

    getSession() {
        const data = localStorage.getItem(this.storageKey);
        return data ? JSON.parse(data) : null;
    }

    saveSession(session) {
        localStorage.setItem(this.storageKey, JSON.stringify(session));
    }

    getUsuarioId() {
        const session = this.getSession();
        return session ? session.usuarioId : null;
    }

    getCarritoId() {
        const session = this.getSession();
        return session ? session.carritoId : null;
    }

    setCarritoId(carritoId) {
        const session = this.getSession();
        if (session) {
            session.carritoId = carritoId;
            this.saveSession(session);
            console.log('CarritoId guardado en sesión:', carritoId);
        }
    }

    clear() {
        localStorage.removeItem(this.storageKey);
    }
}

// Instancia global
const session = new Session();