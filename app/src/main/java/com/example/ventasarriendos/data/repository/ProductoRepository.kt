package com.example.ventasarriendos.data.repository

import com.example.ventasarriendos.data.model.Producto

object ProductoRepository {
    private val productos = mutableListOf(
        Producto(1, "Laptop Gamer", 1200.0, "Laptop potente para gaming", 5),
        Producto(2, "Smartphone 5G", 800.0, "Teléfono con conectividad 5G", 8),
        Producto(3, "Monitor 4K", 500.0, "Monitor UHD para trabajo y juegos", 3),
        Producto(4, "Teclado Mecánico", 150.0, "Teclado con switches mecánicos", 10)
    )

    private val carrito = mutableListOf<Producto>()

    fun getProductos(): List<Producto> = productos.toList()
    fun addProducto(producto: Producto) = productos.add(producto)
    fun updateProducto(producto: Producto) {
        val index = productos.indexOfFirst { it.id == producto.id }
        if (index != -1) {
            productos[index] = producto
        }
    }
    fun removeProducto(producto: Producto) = productos.remove(producto)
    fun getNextId(): Int = (productos.maxOfOrNull { it.id } ?: 0) + 1

    fun reducirStock(productoId: Int): Boolean {
        val producto = productos.find { it.id == productoId }
        return if (producto != null && producto.stock > 0) {
            producto.stock -= 1
            true
        } else {
            false
        }
    }

    fun getCarrito(): List<Producto> = carrito.toList()
    fun addToCarrito(producto: Producto) = carrito.add(producto)
    fun removeFromCarrito(producto: Producto) = carrito.remove(producto)
    fun clearCarrito() = carrito.clear()
    fun getTotalCarrito(): Double = carrito.sumOf { it.precio }
}