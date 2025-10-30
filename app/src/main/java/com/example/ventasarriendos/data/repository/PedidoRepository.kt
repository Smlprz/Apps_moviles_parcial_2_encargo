package com.example.ventasarriendos.data.repository

import com.example.ventasarriendos.data.model.Pedido
import com.example.ventasarriendos.data.model.Producto
import java.text.SimpleDateFormat
import java.util.*

object PedidoRepository {
    private val pedidos = mutableListOf<Pedido>()

    fun crearPedido(productos: List<Producto>, usuarioEmail: String) {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val pedido = Pedido(
            id = (pedidos.maxOfOrNull { it.id } ?: 0) + 1,
            usuarioEmail = usuarioEmail,
            productos = productos.toList(),
            total = productos.sumOf { it.precio },
            fecha = fecha
        )
        pedidos.add(pedido)
    }

    fun getPedidosPorUsuario(email: String): List<Pedido> {
        return pedidos.filter { it.usuarioEmail == email }
    }

    fun getTodosLosPedidos(): List<Pedido> {
        return pedidos.toList()
    }
}