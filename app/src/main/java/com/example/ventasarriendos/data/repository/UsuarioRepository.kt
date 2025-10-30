package com.example.ventasarriendos.data.repository

import com.example.ventasarriendos.data.model.RolUsuario
import com.example.ventasarriendos.data.model.Usuario

object UsuarioRepository {
    private val usuarios = listOf(
        Usuario(1, "admin@tienda.com", "admin123", "Administrador", RolUsuario.ADMINISTRADOR),
        Usuario(2, "cliente@tienda.com", "cliente123", "Cliente", RolUsuario.CLIENTE)
    )

    private var usuarioLogueado: Usuario? = null

    fun login(email: String, password: String): Boolean {
        val usuario = usuarios.find { it.email == email && it.password == password }
        usuarioLogueado = usuario
        return usuario != null
    }

    fun logout() {
        usuarioLogueado = null
    }

    fun getUsuarioLogueado(): Usuario? = usuarioLogueado

    fun esAdministrador(): Boolean {
        return usuarioLogueado?.rol == RolUsuario.ADMINISTRADOR
    }
}