package com.example.ventasarriendos.ui.screens.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ventasarriendos.data.model.Producto
import com.example.ventasarriendos.data.repository.ProductoRepository
import com.example.ventasarriendos.data.repository.PedidoRepository
import com.example.ventasarriendos.data.repository.UsuarioRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController) {
    var carrito by remember { mutableStateOf(ProductoRepository.getCarrito()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            delay(2000)
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Carrito de Compras") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("✅ Pedido confirmado exitosamente")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (carrito.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "El carrito está vacío",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(carrito) { producto ->
                        CarritoItem(
                            producto = producto,
                            onRemove = {
                                ProductoRepository.removeFromCarrito(producto)
                                carrito = ProductoRepository.getCarrito()
                            }
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Total: $${ProductoRepository.getTotalCarrito()}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val productosSinStock = carrito.any { it.stock <= 0 }
                                if (productosSinStock) {
                                    errorMessage = "Algunos productos no tienen stock disponible"
                                } else {
                                    showConfirmDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirmar Pedido")
                        }
                    }
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar Pedido") },
                text = {
                    Column {
                        Text("¿Estás seguro de confirmar el pedido?")
                        Spacer(Modifier.height(8.dp))
                        Text("Total: $${ProductoRepository.getTotalCarrito()}")
                        Spacer(Modifier.height(8.dp))
                        Text("Productos: ${carrito.size}")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        carrito.forEach { producto ->
                            ProductoRepository.reducirStock(producto.id)
                        }

                        val usuario = UsuarioRepository.getUsuarioLogueado()
                        if (usuario != null) {
                            PedidoRepository.crearPedido(carrito, usuario.email)
                        }

                        ProductoRepository.clearCarrito()
                        carrito = ProductoRepository.getCarrito()
                        showConfirmDialog = false
                        showSnackbar = true
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CarritoItem(
    producto: Producto,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text("$${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Stock: ${producto.stock}",
                    color = if (producto.stock == 0) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        }
    }
}