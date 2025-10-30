package com.example.ventasarriendos.data.model

interface CarritoItem {
    val id: Int
    val nombre: String
    val precio: Double
    val descripcion: String
}
data class ItemArriendo(
    override val id: Int,
    override var nombre: String,
    var precioPorDia: Double,
    override var descripcion: String,
    var disponible: Boolean = true
) : CarritoItem {
    override val precio: Double
        get() = precioPorDia
}