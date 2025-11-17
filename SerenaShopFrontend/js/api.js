// Funciones genéricas para llamadas HTTP al backend

async function get(url) {
    try {
        console.log('GET:', url);
        const response = await fetch(url, {
            method: 'GET',
            headers: {
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
        console.log('POST:', url);
        console.log('Data:', data);
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        });
        
        console.log('Response status:', response.status);
        console.log('Response headers:', response.headers.get('content-type'));
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        console.log('✅ Success:', result);
        return result;
    } catch (error) {
        console.error('Error en POST:', error);
        throw error;
    }
}

async function put(url, data) {
    try {
        console.log('PUT:', url);
        console.log('Data:', data);
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) throw new Error(`Error ${response.status}: ${response.statusText}`);
        return await response.json();
    } catch (error) {
        console.error('Error en PUT:', error);
        throw error;
    }
}

async function del(url) {
    try {
        console.log('DELETE:', url);
        const response = await fetch(url, {
            method: 'DELETE'
        });
        
        console.log('Delete response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        // Intentar parsear como JSON, si falla devolver éxito
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            // Si es texto plano, solo retornar éxito
            const text = await response.text();
            console.log('Delete response text:', text);
            return { success: true, message: text };
        }
    } catch (error) {
        console.error('Error en DELETE:', error);
        throw error;
    }
}