package com.serena.serena_shop.controller;

import com.serena.serena_shop.model.Venta;
import com.serena.serena_shop.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/{pedidoId}/confirm")
    public ResponseEntity<Venta> confirmarPago(@PathVariable Integer pedidoId, @RequestParam("metodo") Integer idMetodo) {
        Venta venta = checkoutService.confirmarPago(pedidoId, idMetodo);
        return ResponseEntity.ok(venta);
    }
}
