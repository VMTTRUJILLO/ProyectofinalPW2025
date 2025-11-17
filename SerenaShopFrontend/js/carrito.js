// Funciones para manejo del carrito

async function obtenerOCrearCarrito() {
    const usuarioId = session.getUsuarioId();
    let carritoId = session.getCarritoId();

    console.log('🛒 Obteniendo o creando carrito para usuario:', usuarioId);
    console.log('CarritoId en sesión:', carritoId);

    // Si ya tenemos un carritoId guardado, verificar que existe
    if (carritoId) {
        try {
            const carrito = await get(`${ENDPOINTS.carrito}/${carritoId}`);
            if (carrito && carrito.carritoId) {
                console.log('✅ Usando carrito existente desde sesión:', carritoId);
                return carritoId;
            }
        } catch (error) {
            console.log('⚠️ Carrito en sesión no existe en BD, buscando otro...');
            carritoId = null;
            session.setCarritoId(null);
        }
    }

    // Si no hay carritoId en sesión, buscar en el backend por usuario
    if (!carritoId) {
        try {
            console.log('🔍 Buscando carrito existente en BD para usuario:', usuarioId);
            
            const carritoExistente = await get(`${ENDPOINTS.carrito}/usuario/${usuarioId}`);
            
            if (carritoExistente && carritoExistente.carritoId) {
                console.log('✅ Carrito encontrado en BD:', carritoExistente.carritoId);
                carritoId = carritoExistente.carritoId;
                session.setCarritoId(carritoId);
                return carritoId;
            }
        } catch (error) {
            console.log('ℹ️ No hay carrito existente para este usuario');
        }
        
        // Si no existe ningún carrito, crear uno nuevo
        console.log('➕ Creando nuevo carrito para usuario:', usuarioId);
        
        try {
            const nuevoCarrito = await post(ENDPOINTS.carrito, {
                usuarioId: usuarioId,
                sessionId: null,
                creadoAt: new Date().toISOString()
            });
            
            console.log('✅ Carrito creado exitosamente:', nuevoCarrito.carritoId);
            carritoId = nuevoCarrito.carritoId;
            session.setCarritoId(carritoId);
        } catch (error) {
            console.error('❌ Error al crear carrito:', error);
            throw new Error('No se pudo crear el carrito');
        }
    }

    return carritoId;
}

async function agregarAlCarrito(idProducto, cantidad = 1) {
    try {
        console.log('=== Iniciando agregar al carrito ===');
        console.log('ID Producto:', idProducto);
        console.log('Cantidad:', cantidad);
        
        const carritoId = await obtenerOCrearCarrito();
        console.log('Carrito ID obtenido:', carritoId);
        
        // Obtener el producto para validar stock y precio
        const producto = await obtenerProducto(idProducto);
        console.log('Producto obtenido:', producto);
        
        if (!producto) {
            throw new Error('Producto no encontrado');
        }
        
        if (producto.stock < cantidad) {
            throw new Error(`Stock insuficiente. Disponible: ${producto.stock}`);
        }

        // Agregar item al carrito usando el endpoint correcto
        // Tu backend usa POST /api/detallecarrito
        const detalleCarrito = {
            carrito: {
                carritoId: carritoId
            },
            idProducto: idProducto,
            cantidad: cantidad,
            precioUnitario: producto.precio
        };

        console.log('Enviando a:', ENDPOINTS.detalleCarrito);
        console.log('Datos a enviar:', JSON.stringify(detalleCarrito, null, 2));
        
        const resultado = await post(ENDPOINTS.detalleCarrito, detalleCarrito);
        console.log('Respuesta del servidor:', resultado);
        console.log('=== Producto agregado exitosamente ===');
        
        return resultado;
    } catch (error) {
        console.error('❌ Error al agregar al carrito:', error);
        console.error('Tipo de error:', error.name);
        console.error('Mensaje:', error.message);
        console.error('Stack:', error.stack);
        throw error;
    }
}

async function obtenerDetallesCarrito() {
    try {
        const carritoId = session.getCarritoId();
        console.log('📋 Obteniendo detalles para carrito:', carritoId);
        
        if (!carritoId) {
            console.log('⚠️ No hay carritoId');
            return [];
        }

        const detalles = await get(`${ENDPOINTS.detalleCarrito}/carrito/${carritoId}`);
        console.log('📦 Detalles obtenidos:', detalles);
        
        // Normalizar los datos (soportar camelCase y snake_case)
        const detallesNormalizados = (detalles || []).map(d => ({
            idDetalleCarrito: d.idDetalleCarrito || d.id_detalle_carrito,
            idProducto: d.idProducto || d.id_producto,
            cantidad: d.cantidad,
            precioUnitario: d.precioUnitario || d.precio_unitario
        }));
        
        return detallesNormalizados;
    } catch (error) {
        console.error('❌ Error al obtener detalles:', error);
        return [];
    }
}

async function actualizarCantidadItem(idDetalleCarrito, nuevaCantidad, precioUnitario) {
    try {
        const datos = {
            cantidad: nuevaCantidad,
            precioUnitario: precioUnitario
        };
        return await put(`${ENDPOINTS.detalleCarrito}/${idDetalleCarrito}`, datos);
    } catch (error) {
        console.error('Error al actualizar cantidad:', error);
        throw error;
    }
}

async function eliminarItemCarrito(idDetalleCarrito) {
    try {
        await del(`${ENDPOINTS.detalleCarrito}/${idDetalleCarrito}`);
    } catch (error) {
        console.error('Error al eliminar item:', error);
        throw error;
    }
}

async function mostrarCarrito() {
    const contenedor = document.getElementById('carrito-items');
    const totalElement = document.getElementById('total-carrito');
    
    if (!contenedor) {
        console.error('No se encontró el contenedor carrito-items');
        return;
    }

    contenedor.innerHTML = '<div class="text-center"><div class="spinner-border"></div><p>Cargando carrito...</p></div>';

    try {
        console.log('=== 📋 MOSTRAR CARRITO ===');
        
        const detalles = await obtenerDetallesCarrito();
        console.log('Detalles recibidos:', detalles);
        
        if (!detalles || detalles.length === 0) {
            contenedor.innerHTML = `
                <div class="alert alert-info">
                    <h5>Tu carrito está vacío</h5>
                    <p>Agrega productos desde la <a href="productos.html">tienda</a></p>
                </div>
            `;
            totalElement.textContent = '$0';
            document.getElementById('btn-checkout').disabled = true;
            return;
        }

        let total = 0;
        
        console.log('Obteniendo información de productos...');
        console.log('IDs de productos:', detalles.map(d => d.idProducto));
        
        const productosPromises = detalles.map(d => obtenerProducto(d.idProducto));
        const productos = await Promise.all(productosPromises);
        console.log('Productos obtenidos:', productos);

        const itemsHTML = detalles.map((detalle, index) => {
            const producto = productos[index];
            
            if (!producto) {
                console.warn('⚠️ Producto no encontrado:', detalle.idProducto);
                return '';
            }
            
            const subtotal = detalle.cantidad * detalle.precioUnitario;
            total += subtotal;

            return `
                <div class="card mb-3">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-2">
                                <img src="img/${producto.imagen}" class="img-fluid rounded" alt="${producto.nombreProducto}" onerror="this.src='img/placeholder.jpg'">
                            </div>
                            <div class="col-md-3">
                                <h5>${producto.nombreProducto}</h5>
                                <p class="text-muted mb-0">${obtenerNombreCategoria(producto.idCategoria)}</p>
                            </div>
                            <div class="col-md-2">
                                <p class="mb-0 fw-bold">${formatearPrecio(detalle.precioUnitario)}</p>
                            </div>
                            <div class="col-md-2">
                                <div class="input-group">
                                    <button class="btn btn-outline-secondary btn-sm" onclick="cambiarCantidad(${detalle.idDetalleCarrito}, ${detalle.cantidad - 1}, ${detalle.precioUnitario})">-</button>
                                    <input type="number" class="form-control form-control-sm text-center" value="${detalle.cantidad}" min="1" readonly>
                                    <button class="btn btn-outline-secondary btn-sm" onclick="cambiarCantidad(${detalle.idDetalleCarrito}, ${detalle.cantidad + 1}, ${detalle.precioUnitario})">+</button>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <p class="mb-0 fw-bold text-primary">${formatearPrecio(subtotal)}</p>
                            </div>
                            <div class="col-md-1">
                                <button class="btn btn-danger btn-sm" onclick="eliminarItem(${detalle.idDetalleCarrito})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).filter(html => html).join('');

        if (itemsHTML) {
            contenedor.innerHTML = itemsHTML;
        } else {
            contenedor.innerHTML = '<div class="alert alert-warning">No se pudieron cargar los productos</div>';
        }

        totalElement.textContent = `${formatearPrecio(total)}`;
        document.getElementById('btn-checkout').disabled = false;
        
        console.log('✅ Carrito mostrado. Total:', total);

    } catch (error) {
        console.error('❌ Error al mostrar carrito:', error);
        contenedor.innerHTML = `
            <div class="alert alert-danger">
                <h5>Error al cargar el carrito</h5>
                <p>${error.message}</p>
                <button class="btn btn-primary" onclick="window.location.reload()">Recargar página</button>
            </div>
        `;
    }
}

async function cambiarCantidad(idDetalleCarrito, nuevaCantidad, precioUnitario) {
    if (nuevaCantidad < 1) return;
    
    try {
        await actualizarCantidadItem(idDetalleCarrito, nuevaCantidad, precioUnitario);
        await mostrarCarrito();
        mostrarNotificacion('Cantidad actualizada', 'success');
    } catch (error) {
        mostrarNotificacion('Error al actualizar cantidad', 'error');
    }
}

async function eliminarItem(idDetalleCarrito) {
    if (!confirm('¿Estás seguro de eliminar este producto del carrito?')) return;
    
    try {
        await eliminarItemCarrito(idDetalleCarrito);
        await mostrarCarrito();
        actualizarContadorCarrito();
        mostrarNotificacion('Producto eliminado del carrito', 'success');
    } catch (error) {
        mostrarNotificacion('Error al eliminar producto', 'error');
    }
}

async function actualizarContadorCarrito() {
    try {
        const detalles = await obtenerDetallesCarrito();
        const totalItems = detalles.reduce((sum, d) => sum + d.cantidad, 0);
        
        const badges = document.querySelectorAll('.carrito-badge');
        badges.forEach(badge => {
            badge.textContent = totalItems;
            badge.style.display = totalItems > 0 ? 'inline' : 'none';
        });
    } catch (error) {
        console.error('Error al actualizar contador:', error);
    }
}