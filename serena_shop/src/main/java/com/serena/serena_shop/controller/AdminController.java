package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Producto;
import com.serena.serena_shop.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private ProductoRepository productoRepository;

    // Ruta donde se guardarán las imágenes (ajusta según tu proyecto)
    private static final String UPLOAD_DIR = "SerenaShopFrontend/img/";

    // ============== LISTAR TODOS LOS PRODUCTOS ==============
    @GetMapping("/productos")
    public ResponseEntity<?> listarProductos(
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String busqueda
    ) {
        try {
            List<Producto> productos;

            if (idCategoria != null) {
                productos = productoRepository.findByIdCategoria(idCategoria);
            } else if (busqueda != null && !busqueda.isEmpty()) {
                productos = productoRepository.findByNombreProductoContainingIgnoreCase(busqueda);
            } else {
                productos = productoRepository.findAll();
            }

            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener productos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============== OBTENER UN PRODUCTO POR ID ==============
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Integer id) {
        try {
            Optional<Producto> producto = productoRepository.findById(id);
            if (producto.isPresent()) {
                return ResponseEntity.ok(producto.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============== CREAR PRODUCTO ==============
    @PostMapping("/productos")
    public ResponseEntity<?> crearProducto(
            @RequestParam("nombreProducto") String nombreProducto,
            @RequestParam("idCategoria") Integer idCategoria,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "disponible", defaultValue = "true") Boolean disponible,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            System.out.println("=== CREAR PRODUCTO ===");
            System.out.println("Nombre: " + nombreProducto);
            System.out.println("ID Categoría: " + idCategoria);
            System.out.println("Precio: " + precio);

            Producto producto = new Producto();
            producto.setNombreProducto(nombreProducto);
            producto.setIdCategoria(idCategoria);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setDisponible(disponible);

            // Manejar imagen
            if (imagen != null && !imagen.isEmpty()) {
                String nombreImagen = guardarImagen(imagen);
                producto.setImagen(nombreImagen);
                System.out.println("Imagen guardada: " + nombreImagen);
            } else {
                producto.setImagen("default.jpg");
            }

            Producto productoGuardado = productoRepository.save(producto);
            System.out.println("✅ Producto creado con ID: " + productoGuardado.getIdProducto());

            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);

        } catch (Exception e) {
            System.err.println("❌ Error al crear producto: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al crear producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============== ACTUALIZAR PRODUCTO ==============
    @PutMapping("/productos/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Integer id,
            @RequestParam("nombreProducto") String nombreProducto,
            @RequestParam("idCategoria") Integer idCategoria,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "disponible", defaultValue = "true") Boolean disponible,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        try {
            System.out.println("=== ACTUALIZAR PRODUCTO ID: " + id + " ===");

            Optional<Producto> productoExistente = productoRepository.findById(id);
            if (!productoExistente.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Producto producto = productoExistente.get();
            producto.setNombreProducto(nombreProducto);
            producto.setIdCategoria(idCategoria);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setDisponible(disponible);

            // Si hay nueva imagen, guardarla
            if (imagen != null && !imagen.isEmpty()) {
                String nombreImagen = guardarImagen(imagen);
                producto.setImagen(nombreImagen);
                System.out.println("Nueva imagen guardada: " + nombreImagen);
            }

            Producto productoActualizado = productoRepository.save(producto);
            System.out.println("✅ Producto actualizado exitosamente");

            return ResponseEntity.ok(productoActualizado);

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============== ELIMINAR PRODUCTO ==============
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        try {
            System.out.println("=== ELIMINAR PRODUCTO ID: " + id + " ===");

            Optional<Producto> producto = productoRepository.findById(id);
            if (!producto.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Producto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            productoRepository.deleteById(id);
            System.out.println("✅ Producto eliminado exitosamente");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Producto eliminado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ============== MÉTODO AUXILIAR: GUARDAR IMAGEN ==============
    private String guardarImagen(MultipartFile imagen) throws IOException {
        // Generar nombre único para la imagen
        String nombreOriginal = imagen.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = System.currentTimeMillis() + extension;

        // Crear directorio si no existe
        File directorio = new File(UPLOAD_DIR);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        // Guardar archivo
        Path rutaArchivo = Paths.get(UPLOAD_DIR + nombreUnico);
        Files.write(rutaArchivo, imagen.getBytes());

        return nombreUnico;
    }

    // ============== ESTADÍSTICAS BÁSICAS ==============
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProductos", productoRepository.count());
            stats.put("productosDisponibles", productoRepository.findByDisponibleTrue().size());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
