// Funciones para manejo de pedidos y ventas

async function obtenerMisPedidos() {
    try {
        const usuarioId = session.getUsuarioId();
        const pedidos = await get(`${ENDPOINTS.pedidos}/usuario/${usuarioId}`);
        return pedidos;
    } catch (error) {
        console.error('Error al obtener pedidos:', error);
        return [];
    }
}

async function obtenerMisVentas() {
    try {
        const usuarioId = session.getUsuarioId();
        const ventas = await get(`${ENDPOINTS.ventas}/usuario/${usuarioId}`);
        return ventas;
    } catch (error) {
        console.error('Error al obtener ventas:', error);
        return [];
    }
}

async function mostrarMisPedidos() {
    const contenedor = document.getElementById('pedidos-lista');
    if (!contenedor) return;

    contenedor.innerHTML = '<div class="text-center"><div class="spinner-border"></div><p>Cargando pedidos...</p></div>';

    try {
        const ventas = await obtenerMisVentas();
        
        if (ventas.length === 0) {
            contenedor.innerHTML = `
                <div class="alert alert-info">
                    <h5>No tienes pedidos aún</h5>
                    <p>Cuando realices tu primera compra, aparecerá aquí.</p>
                    <a href="productos.html" class="btn btn-primary">Ir a comprar</a>
                </div>
            `;
            return;
        }

        contenedor.innerHTML = ventas.map(venta => {
            const fecha = new Date(venta.fechaVenta).toLocaleDateString('es-CO', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });

            return `
                <div class="card mb-3 shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <div class="row align-items-center">
                            <div class="col-md-6">
                                <h5 class="mb-0">Pedido #${venta.idVenta}</h5>
                            </div>
                            <div class="col-md-6 text-md-end">
                                <span class="badge bg-success">${venta.estado}</span>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p class="mb-1"><strong>Fecha:</strong> ${fecha}</p>
                                <p class="mb-1"><strong>Método de pago:</strong> ${obtenerNombreMetodoPago(venta.idMetodoPago)}</p>
                            </div>
                            <div class="col-md-6 text-md-end">
                                <h4 class="text-primary mb-0">$${formatearPrecio(venta.totalVenta)}</h4>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Error al mostrar pedidos:', error);
        contenedor.innerHTML = '<div class="alert alert-danger">Error al cargar los pedidos</div>';
    }
}

function obtenerNombreMetodoPago(idMetodoPago) {
    const metodo = METODOS_PAGO.find(m => m.id === idMetodoPago);
    return metodo ? metodo.nombre : 'Desconocido';
}