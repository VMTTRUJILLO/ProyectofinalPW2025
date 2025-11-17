// Configuración de la API Backend
const API_URL = 'http://localhost:8086/api';

// Endpoints
const ENDPOINTS = {
    productos: `${API_URL}/productos`,
    carrito: `${API_URL}/carrito`,
    detalleCarrito: `${API_URL}/detallecarrito`,
    pedidos: `${API_URL}/pedidos`,
    usuarios: `${API_URL}/usuarios`,
    ventas: `${API_URL}/ventas`
};

// Categorías disponibles
const CATEGORIAS = [
    { id: 1, nombre: 'Aretes' },
    { id: 2, nombre: 'Earcuff' },
    { id: 3, nombre: 'Pulseras' },
    { id: 4, nombre: 'Cadenas' },
    { id: 5, nombre: 'Chocker' },
    { id: 6, nombre: 'Tobillera' },
    { id: 7, nombre: 'Anillos' }
];

// Métodos de pago (según tu backend)
const METODOS_PAGO = [
    { id: 1, nombre: 'Nequi' }
];

// Usuario temporal para pruebas (Fase 1)
const USUARIO_TEMPORAL_ID = 1;