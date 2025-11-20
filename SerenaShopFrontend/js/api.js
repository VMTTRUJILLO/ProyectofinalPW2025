// Funciones genéricas para llamadas HTTP al backend




function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return token
        ? { 'Authorization': 'Bearer ' + token }
        : {};
}

async function get(url) {
    try {
        console.log('GET:', url);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                ...getAuthHeaders(),
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);
        return await response.json();

    } catch (error) {
        console.error('Error en GET:', error);
        throw error;
    }
}

 async function post(url, data) {
    try {
        console.log('POST:', url, data);

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${text}`);
        }

        return text ? JSON.parse(text) : {};

    } catch (error) {
        console.error('❌ Error en POST:', error);
        throw error;
    }
}

async function put(url, data) {
    try {
        console.log('PUT:', url);

        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                ...getAuthHeaders(),
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const text = await response.text();

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${text}`);
        }

        return text ? JSON.parse(text) : {};

    } catch (error) {
        console.error('❌ Error en PUT:', error);
        throw error;
    }
}

async function del(url) {
    try {
        console.log('DELETE:', url);

        const response = await fetch(url, {
            method: 'DELETE',
            headers: {
                ...getAuthHeaders(),
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);

        const contentType = response.headers.get('content-type');

        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            return { success: true, message: await response.text() };
        }

    } catch (error) {
        console.error('Error en DELETE:', error);
        throw error;
    }
}
// Crear producto (USANDO TU MISMO POST)
 const api = {
    createProducto: (producto) => post(`${ENDPOINTS.productos}`, producto),
    getProductos: () => get(ENDPOINTS.productos),

    // ⭐ Asegúrate de que esta función exista:
    getProductoById: (id) => get(`${ENDPOINTS.productos}/${id}`),
    
    // ⭐ Asegúrate de que esta función exista:
    updateProducto: (producto) => put(`${ENDPOINTS.productos}/${producto.id}`, producto)
};
