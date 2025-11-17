// Funciones para checkout y finalización de compra

async function crearPedido() {
    try {
        const usuarioId = session.getUsuarioId();
        const datos = { idUsuario: usuarioId };
        
        console.log('📝 Creando pedido para usuario:', usuarioId);
        const resultado = await post(`${ENDPOINTS.pedidos}/crear`, datos);
        console.log('✅ Pedido creado:', resultado);
        return resultado;
    } catch (error) {
        console.error('❌ Error al crear pedido:', error);
        throw error;
    }
}

async function procesarCheckout() {
    try {
        const usuarioId = session.getUsuarioId();
        const idMetodoPago = 1; // Nequi (único método disponible)
        
        const datos = {
            idUsuario: usuarioId,
            idMetodoPago: idMetodoPago
        };
        
        console.log('💳 Procesando checkout:', datos);
        const resultado = await post(`${ENDPOINTS.pedidos}/checkout`, datos);
        console.log('✅ Checkout procesado:', resultado);
        return resultado;
    } catch (error) {
        console.error('❌ Error en checkout:', error);
        throw error;
    }
}

async function mostrarResumenCheckout() {
    const contenedor = document.getElementById('resumen-checkout');
    const totalElement = document.getElementById('total-checkout');
    
    if (!contenedor) return;

    try {
        const detalles = await obtenerDetallesCarrito();
        
        if (detalles.length === 0) {
            window.location.href = 'carrito.html';
            return;
        }

        let total = 0;
        
        const productosPromises = detalles.map(d => obtenerProducto(d.idProducto));
        const productos = await Promise.all(productosPromises);

        contenedor.innerHTML = `
            <div class="list-group mb-3">
                ${detalles.map((detalle, index) => {
                    const producto = productos[index];
                    const subtotal = detalle.cantidad * detalle.precioUnitario;
                    total += subtotal;

                    return `
                        <div class="list-group-item">
                            <div class="row align-items-center">
                                <div class="col-md-2">
                                    <img src="img/${producto.imagen}" class="img-fluid rounded" alt="${producto.nombreProducto}" onerror="this.src='img/placeholder.jpg'">
                                </div>
                                <div class="col-md-5">
                                    <h6 class="mb-0">${producto.nombreProducto}</h6>
                                    <small class="text-muted">${obtenerNombreCategoria(producto.idCategoria)}</small>
                                </div>
                                <div class="col-md-2 text-center">
                                    <span class="badge bg-secondary">x${detalle.cantidad}</span>
                                </div>
                                <div class="col-md-3 text-end">
                                    <strong>${formatearPrecio(subtotal)}</strong>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;

        totalElement.textContent = `${formatearPrecio(total)}`;

    } catch (error) {
        console.error('Error al mostrar resumen:', error);
        contenedor.innerHTML = '<div class="alert alert-danger">Error al cargar el resumen</div>';
    }
}

async function finalizarCompra() {
    const btnFinalizar = document.getElementById('btn-finalizar');
    btnFinalizar.disabled = true;
    btnFinalizar.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {
        console.log('🛒 Iniciando finalización de compra...');
        
        // 1. Crear el pedido
        const pedido = await crearPedido();
        console.log('✅ Pedido creado:', pedido);
        
        // 2. Procesar el checkout (esto genera la venta y vacía el carrito)
        const checkout = await procesarCheckout();
        console.log('✅ Checkout procesado:', checkout);
        
        // 3. Limpiar el carritoId de la sesión para que se cree uno nuevo
        session.setCarritoId(null);
        
        // 4. Mostrar mensaje de éxito y redirigir
        mostrarModalExito();
        
    } catch (error) {
        console.error('❌ Error al finalizar compra:', error);
        mostrarNotificacion('Error al procesar la compra. Intenta nuevamente.', 'error');
        btnFinalizar.disabled = false;
        btnFinalizar.innerHTML = '<i class="bi bi-check-circle"></i> Finalizar Compra';
    }
}

function mostrarModalExito() {
    const modal = `
        <div class="modal fade show" id="modalExito" style="display: block; background: rgba(0,0,0,0.5);">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title">¡Compra Exitosa!</h5>
                    </div>
                    <div class="modal-body text-center">
                        <i class="bi bi-check-circle text-success" style="font-size: 5rem;"></i>
                        <h4 class="mt-3">¡Gracias por tu compra!</h4>
                        <p class="text-muted">Tu pedido ha sido procesado exitosamente.</p>
                        <p class="text-muted">Recibirás un mensaje en tu Nequi con los detalles del pago.</p>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" onclick="irAMisPedidos()">Ver mis pedidos</button>
                        <button class="btn btn-secondary" onclick="irAInicio()">Volver al inicio</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', modal);
}

function irAMisPedidos() {
    window.location.href = 'mis-pedidos.html';
}

function irAInicio() {
    window.location.href = 'index.html';
}