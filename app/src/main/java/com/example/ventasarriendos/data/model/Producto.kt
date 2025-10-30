package com.example.ventasarriendos.data.model
data class Producto(
    val id: Int,
    var nombre: String,
    var precio: Double,
    var descripcion: String,

    var stock: Int = 10
)