package com.example.ventasarriendos.data.repository

import com.example.ventasarriendos.data.model.Servicio

object ServicioRepository {
    private val servicios = mutableListOf(
        Servicio(1, "Reparación Laptop", 50.0, "Reparación y mantenimiento de laptops", "2-3 horas"),
        Servicio(2, "Instalación Software", 30.0, "Instalación y configuración de software", "1 hora"),
        Servicio(3, "Asesoría Técnica", 25.0, "Asesoramiento personalizado en tecnología", "1 hora"),
        Servicio(4, "Limpieza Equipos", 20.0, "Limpieza interna y externa de equipos", "1-2 horas")
    )

    fun getServicios(): List<Servicio> = servicios.toList()
    fun addServicio(servicio: Servicio) = servicios.add(servicio)
    fun updateServicio(servicio: Servicio) {
        val index = servicios.indexOfFirst { it.id == servicio.id }
        if (index != -1) {
            servicios[index] = servicio
        }
    }
    fun removeServicio(servicio: Servicio) = servicios.remove(servicio)
    fun getNextId(): Int = (servicios.maxOfOrNull { it.id } ?: 0) + 1
}