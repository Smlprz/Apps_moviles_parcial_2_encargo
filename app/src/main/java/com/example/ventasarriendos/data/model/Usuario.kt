package com.example.ventasarriendos.data.model

data class Usuario(
    val id: Int,
    val email: String,
    val password: String,
    val nombre: String,
    val rol: RolUsuario
)

enum class RolUsuario {
    CLIENTE, ADMINISTRADOR
}