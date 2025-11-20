const API_ADMIN = 'http://localhost:8086/api/admin';
const modalProductoElement = document.getElementById('modalProducto');

let productoActual = null;
let modalProducto = null;

if (modalProductoElement) {
    // Asegúrate de tener la librería JS de Bootstrap cargada antes que este script
    modalProducto = new bootstrap.Modal(modalProductoElement); 
} else {
    console.error("Elemento 'modalProducto' no encontrado en el DOM.");
}

// ============== FUNCIÓN AUXILIAR: Headers con Token ==============
function getAdminHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`
    };
}

// ============== INICIALIZACIÓN ==============
window.addEventListener('load', function() {
    console.log('🔄 Página cargada, inicializando admin panel...');
    
    // Dar tiempo a que todos los scripts se carguen
    setTimeout(inicializarAdmin, 200);
});

function inicializarAdmin() {
    console.log('🔐 Verificando acceso admin...');
    
    // Verificar que existan las funciones de auth
    if (typeof isAuthenticated !== 'function') {
        console.error('❌ isAuthenticated no está disponible');
        console.log('Esperando 1 segundo más...');
        setTimeout(inicializarAdmin, 1000);
        return;
    }
    
    if (typeof isAdmin !== 'function') {
        console.error('❌ isAdmin no está disponible');
        console.log('Esperando 1 segundo más...');
        setTimeout(inicializarAdmin, 1000);
        return;
    }
    
    console.log('✅ Funciones de autenticación disponibles');
    
    // Verificar que esté logueado
    if (!isAuthenticated()) {
        alert('⛔ Debes iniciar sesión para acceder.');
        window.location.href = 'login.html';
        return;
    }
    
    // Verificar que sea admin
    if (!isAdmin()) {
        alert('⛔ Acceso denegado. Solo administradores pueden acceder.');
        window.location.href = 'index.html';
        return;
    }

    console.log('✅ Usuario es administrador');

    // Mostrar nombre del admin
    const userData = getUserData();
    const nombreElement = document.getElementById('adminName');
    if (nombreElement) {
        nombreElement.textContent = `¡Hola ${userData.nombreCompleto}! Eres administrador 👑`;
    }

    // Inicializar modal después de un delay
    setTimeout(() => {
        const modalElement = document.getElementById('modalProducto');
        if (modalElement) {
            modalProducto = new bootstrap.Modal(modalElement);
            console.log('✅ Modal inicializado');
        } else {
            console.error('❌ Modal element not found');
        }
    }, 500);

    // Cargar dashboard
    cargarDashboard();
    cargarProductosAdmin();
}

// ============== NAVEGACIÓN ==============
function mostrarSeccion(seccion) {
    document.querySelectorAll('.seccion-admin').forEach(s => s.style.display = 'none');
    document.querySelectorAll('.admin-sidebar .nav-link').forEach(link => {
        link.classList.remove('active');
    });
    
    document.getElementById(`seccion-${seccion}`).style.display = 'block';
    if (event && event.target) {
        event.target.classList.add('active');
    }
    
    if (seccion === 'dashboard') {
        cargarDashboard();
    } else if (seccion === 'productos') {
        cargarProductosAdmin();
    } else if (seccion === 'pedidos') {
        cargarPedidosAdmin();
    }
}

// ============== DASHBOARD ==============
async function cargarDashboard() {
    try {
        const token = localStorage.getItem('token');
        
        const response = await fetch(`${API_ADMIN}/dashboard/stats`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}`);
        }
        
        const stats = await response.json();
        
        document.getElementById('stat-total-productos').textContent = stats.totalProductos || 0;
        document.getElementById('stat-disponibles').textContent = stats.productosDisponibles || 0;
        
    } catch (error) {
        console.error('Error al cargar dashboard:', error);
        document.getElementById('stat-total-productos').textContent = 'Error';
        document.getElementById('stat-disponibles').textContent = 'Error';
    }
}

// ============== PRODUCTOS ==============
async function cargarProductosAdmin() {
    const tabla = document.getElementById('tabla-productos');
    if (!tabla) return;
    
    tabla.innerHTML = '<tr><td colspan="8" class="text-center">Cargando...</td></tr>';

    try {
        const token = localStorage.getItem('token');
        
        const response = await fetch(`${API_ADMIN}/productos`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}`);
        }

        const productos = await response.json();

        if (productos.length === 0) {
            tabla.innerHTML = '<tr><td colspan="8" class="text-center">No hay productos registrados</td></tr>';
            return;
        }

        tabla.innerHTML = productos.map(p => `
            <tr>
                <td>${p.idProducto}</td>
                <td>
                    <img src="img/${p.imagen || 'default.jpg'}" class="producto-img-preview" 
                         onerror="this.src='img/default.jpg'" alt="${p.nombreProducto}">
                </td>
                <td>${p.nombreProducto}</td>
                <td>${obtenerNombreCategoria(p.idCategoria)}</td>
                <td>$${formatearNumero(p.precio)}</td>
                <td>${p.stock}</td>
                <td>
                    <span class="badge ${p.disponible ? 'bg-success' : 'bg-secondary'}">
                        ${p.disponible ? 'Disponible' : 'No disponible'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-warning btn-action" onclick="editarProducto(${p.idProducto})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-danger btn-action" onclick="eliminarProducto(${p.idProducto})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error al cargar productos:', error);
        tabla.innerHTML = '<tr><td colspan="8" class="text-center text-danger">Error al cargar productos</td></tr>';
    }
}

// ============== CREAR PRODUCTO ==============
function abrirModalCrear() {
    if (!modalProducto) {
        console.error('Modal no inicializado'); 
        return;
    }
    
    productoActual = null;
    document.getElementById('modalProductoTitulo').textContent = 'Nuevo Producto';
    document.getElementById('formProducto').reset();
    document.getElementById('productoId').value = '';
    document.getElementById('preview-imagen').innerHTML = '';
    modalProducto.show();
}

// ============== EDITAR PRODUCTO ==============
// admin.js (Nueva y simplificada función)
// ============== REDIRIGIR A PÁGINA DE EDICIÓN ==============
function editarProducto(idProducto) {
    // Redirige a la página de edición, pasando el ID como parámetro de URL
    window.location.href = `editar-producto.html?id=${idProducto}`;
}

// ============== GUARDAR PRODUCTO ==============
async function guardarProducto() {
    const id = document.getElementById('productoId').value;
    const nombre = document.getElementById('productoNombre').value.trim();
    const categoria = document.getElementById('productoCategoria').value;
    const precio = document.getElementById('productoPrecio').value;
    const stock = document.getElementById('productoStock').value;
    const disponible = document.getElementById('productoDisponible').value;
    const imagenInput = document.getElementById('productoImagen');

    if (!nombre || !categoria || !precio || !stock) {
        alert('Por favor completa todos los campos obligatorios');
        return;
    }

    const formData = new FormData();
    formData.append('nombreProducto', nombre);
    formData.append('idCategoria', categoria);
    formData.append('precio', precio);
    formData.append('stock', stock);
    formData.append('disponible', disponible);

    if (imagenInput.files.length > 0) {
        formData.append('imagen', imagenInput.files[0]);
    }

    try {
        const url = id ? `${API_ADMIN}/productos/${id}` : `${API_ADMIN}/productos`;
        const method = id ? 'PUT' : 'POST';
        const token = localStorage.getItem('token');
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (response.ok) {
            alert(id ? 'Producto actualizado exitosamente' : 'Producto creado exitosamente');
            modalProducto.hide();
            cargarProductosAdmin();
            cargarDashboard();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.error || 'Error desconocido'));
        }

    } catch (error) {
        console.error('Error al guardar producto:', error);
        alert('Error al guardar el producto');
    }
}

// ============== ELIMINAR PRODUCTO ==============
async function eliminarProducto(id) {
    if (!confirm('¿Estás seguro de eliminar este producto?')) {
        return;
    }

    try {
        const token = localStorage.getItem('token');
        
        const response = await fetch(`${API_ADMIN}/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            alert('Producto eliminado exitosamente');
            cargarProductosAdmin();
            cargarDashboard();
        } else {
            const error = await response.json();
            alert('Error: ' + (error.error || 'Error desconocido'));
        }

    } catch (error) {
        console.error('Error al eliminar producto:', error);
        alert('Error al eliminar el producto');
    }
}

// ============== FILTRAR PRODUCTOS ==============
function filtrarProductos() {
    const busqueda = document.getElementById('buscarProducto').value.toLowerCase();
    const categoria = document.getElementById('filtroCategoria').value;

    const filas = document.querySelectorAll('#tabla-productos tr');
    
    filas.forEach(fila => {
        if (fila.cells.length < 3) return;
        
        const nombre = fila.cells[2]?.textContent.toLowerCase() || '';
        const categoriaProducto = fila.cells[3]?.textContent || '';
        const idCategoria = obtenerIdCategoria(categoriaProducto);

        const coincideNombre = nombre.includes(busqueda);
        const coincideCategoria = !categoria || idCategoria == categoria;

        fila.style.display = (coincideNombre && coincideCategoria) ? '' : 'none';
    });
}

// ============== PEDIDOS ==============
async function cargarPedidosAdmin() {
    const tabla = document.getElementById('tabla-pedidos');
    if (!tabla) return;
    
    tabla.innerHTML = '<tr><td colspan="6" class="text-center">Cargando...</td></tr>';

    try {
        const token = localStorage.getItem('token');
        
        const response = await fetch('http://localhost:8086/api/pedidos', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}`);
        }

        const pedidos = await response.json();

        if (pedidos.length === 0) {
            tabla.innerHTML = '<tr><td colspan="6" class="text-center">No hay pedidos</td></tr>';
            return;
        }

        tabla.innerHTML = pedidos.map(p => `
            <tr>
                <td>#${p.idPedido}</td>
                <td>Usuario ID: ${p.usuarioId}</td>
                <td>$${formatearNumero(p.totalPedido)}</td>
                <td><span class="badge bg-info">${p.estado}</span></td>
                <td>${formatearFecha(p.creadoAt)}</td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error al cargar pedidos:', error);
        tabla.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error al cargar pedidos</td></tr>';
    }
}

function verDetallePedido(id) {
    alert('Ver detalle del pedido #' + id);
}

// ============== UTILIDADES ==============
function obtenerNombreCategoria(id) {
    const categorias = {
        1: 'Aretes', 2: 'Collares', 3: 'Pulseras',
        4: 'Anillos', 5: 'Conjuntos', 6: 'Tobilleras', 7: 'Accesorios'
    };
    return categorias[id] || 'Sin categoría';
}

function obtenerIdCategoria(nombre) {
    const categorias = {
        'Aretes': 1, 'Collares': 2, 'Pulseras': 3,
        'Anillos': 4, 'Conjuntos': 5, 'Tobilleras': 6, 'Accesorios': 7
    };
    return categorias[nombre] || '';
}

function formatearNumero(numero) {
    return new Intl.NumberFormat('es-CO').format(numero);
}

function formatearFecha(fecha) {
    return new Date(fecha).toLocaleString('es-CO');
}


// Preview de imagen
document.addEventListener('DOMContentLoaded', function() {
    const inputImagen = document.getElementById('productoImagen');
    if (inputImagen) {
        inputImagen.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('preview-imagen').innerHTML = `
                        <img src="${e.target.result}" class="img-thumbnail mt-2" style="max-width: 200px;">
                        <p class="small text-muted mt-2">Nueva imagen seleccionada</p>
                    `;
                };
                reader.readAsDataURL(file);
            }
        });
    }
});