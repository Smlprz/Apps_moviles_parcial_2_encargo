package com.example.ventasarriendos.data.model

data class Pedido(
    val id: Int,
    val usuarioEmail: String,
    val productos: List<Producto>,
    val total: Double,
    val fecha: String
)