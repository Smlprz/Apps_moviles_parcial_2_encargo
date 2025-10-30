package com.example.ventasarriendos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ventasarriendos.ui.screens.splash.SplashScreen
import com.example.ventasarriendos.ui.screens.arriendos.ArriendosScreen
import com.example.ventasarriendos.ui.screens.carrito.CarritoScreen
import com.example.ventasarriendos.ui.screens.detalle.DetalleProductoScreen
import com.example.ventasarriendos.ui.screens.historial.HistorialScreen
import com.example.ventasarriendos.ui.screens.home.HomeScreen
import com.example.ventasarriendos.ui.screens.login.LoginScreen
import com.example.ventasarriendos.ui.screens.perfil.PerfilScreen
import com.example.ventasarriendos.ui.screens.productos.ProductosScreen
import com.example.ventasarriendos.ui.screens.servicios.ServiciosScreen
import com.example.ventasarriendos.data.repository.UsuarioRepository
import com.example.ventasarriendos.ui.theme.VentasArriendosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VentasArriendosTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen {
            showSplash = false
        }
    } else {
        val navController = rememberNavController()
        val startDestination = if (UsuarioRepository.getUsuarioLogueado() != null) {
            "home"
        } else {
            "login"
        }

        NavHost(navController, startDestination = startDestination) {
            composable("login") { LoginScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("productos") { ProductosScreen(navController) }
            composable("servicios") { ServiciosScreen(navController) }
            composable("arriendos") { ArriendosScreen(navController) }
            composable("carrito") { CarritoScreen(navController) }
            composable("historial") { HistorialScreen(navController) }
            composable("detalleProducto/{productoId}") { backStackEntry ->
                val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull()
                DetalleProductoScreen(navController, productoId)
            }
            composable("perfil") { PerfilScreen(navController) }
        }
    }
}