package com.example.ventasarriendos.ui.screens.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ventasarriendos.data.repository.PedidoRepository
import com.example.ventasarriendos.data.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController) {
    val usuario = UsuarioRepository.getUsuarioLogueado()
    val pedidos = remember {
        mutableStateOf(
            if (UsuarioRepository.esAdministrador()) {
                PedidoRepository.getTodosLosPedidos()
            } else {
                usuario?.email?.let { PedidoRepository.getPedidosPorUsuario(it) } ?: emptyList()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(if (UsuarioRepository.esAdministrador()) "Todos los Pedidos" else "Mis Compras")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (pedidos.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos registrados")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(pedidos.value) { pedido ->
                    PedidoItem(pedido = pedido)
                }
            }
        }
    }
}

@Composable
fun PedidoItem(pedido: com.example.ventasarriendos.data.model.Pedido) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleMedium)
            Text("Fecha: ${pedido.fecha}", style = MaterialTheme.typography.bodySmall)
            if (UsuarioRepository.esAdministrador()) {
                Text("Cliente: ${pedido.usuarioEmail}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Productos: ${pedido.productos.size}", style = MaterialTheme.typography.bodySmall)
            Text("Total: $${pedido.total}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}