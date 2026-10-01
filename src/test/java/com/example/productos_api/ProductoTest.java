package com.example.productos_api;

import com.example.productos_api.model.Producto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductoTest {

    @Test
    void alCrearProductoConContructor_losCamposQuedanCorrectos() {

        Producto producto = new Producto("Mouse gamer", 29990.0);

        assertEquals("Mouse gamer", producto.getNombre());
        assertEquals(29990.0, producto.getPrecio());
    }

}
