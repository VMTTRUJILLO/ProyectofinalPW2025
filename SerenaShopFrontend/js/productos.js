// Funciones para manejo de productos

async function cargarProductos(categoriaId = null) {
    try {
        const productos = await get(ENDPOINTS.productos);
        
        // Filtrar por categoría si se especifica
        const productosFiltrados = categoriaId 
            ? productos.filter(p => p.idCategoria === categoriaId)
            : productos;
        
        return productosFiltrados;
    } catch (error) {
        console.error('Error al cargar productos:', error);
        return [];
    }
}

async function obtenerProducto(idProducto) {
    try {
        return await get(`${ENDPOINTS.productos}/${idProducto}`);
    } catch (error) {
        console.error('Error al obtener producto:', error);
        return null;
    }
}

function mostrarProductos(productos, contenedorId) {
    const contenedor = document.getElementById(contenedorId);
    if (!contenedor) return;

    if (productos.length === 0) {
        contenedor.innerHTML = '<div class="col-12 text-center"><p class="text-muted">No hay productos disponibles</p></div>';
        return;
    }

    contenedor.innerHTML = productos.map(producto => `
        <div class="col-md-4 col-lg-3 mb-4">
            <div class="card h-100 shadow-sm producto-card">
                <img src="img/${producto.imagen}" class="card-img-top" alt="${producto.nombreProducto}" onerror="this.src='img/Logoserena.jpg'">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${producto.nombreProducto}</h5>
                    <p class="text-muted mb-1">${obtenerNombreCategoria(producto.idCategoria)}</p>
                    <p class="card-text fw-bold text-primary fs-5">$${formatearPrecio(producto.precio)}</p>
                    <p class="card-text text-muted small">Stock: ${producto.stock}</p>
                    <button 
                        class="btn btn-primary mt-auto" 
                        onclick="agregarAlCarritoDesdeProducto(${producto.idProducto}, '${producto.nombreProducto}', ${producto.precio})"
                        ${producto.stock === 0 ? 'disabled' : ''}>
                        ${producto.stock === 0 ? 'Agotado' : 'Agregar al carrito'}
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

function obtenerNombreCategoria(idCategoria) {
    const categoria = CATEGORIAS.find(c => c.id === idCategoria);
    return categoria ? categoria.nombre : 'Sin categoría';
}

function formatearPrecio(precio) {
    return new Intl.NumberFormat('es-CO').format(precio);
}

async function agregarAlCarritoDesdeProducto(idProducto, nombreProducto, precio) {
    try {
        await agregarAlCarrito(idProducto, 1);
        mostrarNotificacion(`${nombreProducto} agregado al carrito`, 'success');
        actualizarContadorCarrito();
    } catch (error) {
        mostrarNotificacion('Error al agregar producto al carrito', 'error');
    }
}

function mostrarNotificacion(mensaje, tipo = 'success') {
    // Crear elemento de notificación
    const notificacion = document.createElement('div');
    notificacion.className = `alert alert-${tipo === 'success' ? 'success' : 'danger'} position-fixed top-0 start-50 translate-middle-x mt-3`;
    notificacion.style.zIndex = '9999';
    notificacion.textContent = mensaje;
    
    document.body.appendChild(notificacion);
    
    // Eliminar después de 3 segundos
    setTimeout(() => {
        notificacion.remove();
    }, 3000);
}