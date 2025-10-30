package com.example.ventasarriendos.data.repository

import com.example.ventasarriendos.data.model.ItemArriendo

object ArriendoRepository {
    private val arriendos = mutableListOf(
        ItemArriendo(1, "Proyector HD", 15.0, "Proyector de alta definición para presentaciones"),
        ItemArriendo(2, "Tablet Gráfica", 10.0, "Tablet para diseño gráfico y digital"),
        ItemArriendo(3, "Cámara Profesional", 25.0, "Cámara DSLR para fotografía profesional"),
        ItemArriendo(4, "Equipo Sonido", 20.0, "Sistema de sonido para eventos")
    )

    fun getArriendos(): List<ItemArriendo> = arriendos.toList()
    fun addArriendo(arriendo: ItemArriendo) = arriendos.add(arriendo)
    fun updateArriendo(arriendo: ItemArriendo) {
        val index = arriendos.indexOfFirst { it.id == arriendo.id }
        if (index != -1) {
            arriendos[index] = arriendo
        }
    }
    fun removeArriendo(arriendo: ItemArriendo) = arriendos.remove(arriendo)
    fun getNextId(): Int = (arriendos.maxOfOrNull { it.id } ?: 0) + 1
}