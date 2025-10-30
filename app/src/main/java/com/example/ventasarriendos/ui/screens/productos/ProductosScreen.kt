package com.example.ventasarriendos.ui.screens.productos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ventasarriendos.data.model.Producto
import com.example.ventasarriendos.data.repository.ProductoRepository
import com.example.ventasarriendos.data.repository.UsuarioRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(navController: NavController) {
    var productos by remember { mutableStateOf(ProductoRepository.getProductos()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingProducto by remember { mutableStateOf<Producto?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val esAdmin = UsuarioRepository.esAdministrador()

    // Snackbar
    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            delay(2000)
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Productos") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (esAdmin) {
                FloatingActionButton(
                    onClick = {
                        editingProducto = null
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("+")
                }
            }
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(productos) { producto ->
                TarjetaProducto(
                    producto = producto,
                    esAdmin = esAdmin,
                    onAddToCart = {
                        ProductoRepository.addToCarrito(producto)
                        snackbarMessage = "✅ ${producto.nombre} agregado al carrito"
                        showSnackbar = true
                    },
                    onEdit = {
                        editingProducto = producto
                        showDialog = true
                    },
                    onDelete = {
                        ProductoRepository.removeProducto(producto)
                        productos = ProductoRepository.getProductos()
                        snackbarMessage = "🗑️ ${producto.nombre} eliminado"
                        showSnackbar = true
                    },
                    navController = navController
                )
            }
        }

        if (showDialog) {
            DialogoProducto(
                producto = editingProducto,
                onDismiss = { showDialog = false },
                onConfirm = { producto ->
                    if (editingProducto == null) {
                        ProductoRepository.addProducto(producto)
                        snackbarMessage = "✅ ${producto.nombre} agregado"
                    } else {
                        ProductoRepository.updateProducto(producto)
                        snackbarMessage = "✏️ ${producto.nombre} actualizado"
                    }
                    productos = ProductoRepository.getProductos()
                    showDialog = false
                    showSnackbar = true
                }
            )
        }
    }
}

@Composable
fun TarjetaProducto(
    producto: Producto,
    esAdmin: Boolean,
    onAddToCart: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("detalleProducto/${producto.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text("$${producto.precio}", style = MaterialTheme.typography.bodyMedium)
            Text(producto.descripcion, style = MaterialTheme.typography.bodySmall)

            Text(
                "Stock: ${producto.stock}",
                color = if (producto.stock == 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        onAddToCart()
                    },
                    enabled = producto.stock > 0
                ) {
                    Text(if (producto.stock == 0) "Agotado" else "Agregar")
                }

                if (esAdmin) {
                    Row {
                        TextButton(onClick = onEdit) {
                            Text("Editar")
                        }
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoProducto(
    producto: Producto?,
    onDismiss: () -> Unit,
    onConfirm: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio.toString() ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var stock by remember { mutableStateOf(producto?.stock.toString() ?: "10") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (producto == null) "Nuevo Producto" else "Editar Producto") },
        text = {
            Column {
                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it; error = "" },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = precio, onValueChange = { precio = it; error = "" },
                    label = { Text("Precio") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = stock, onValueChange = { stock = it; error = "" },
                    label = { Text("Stock") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it; error = "" },
                    label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    nombre.isBlank() -> error = "Nombre requerido"
                    precio.isBlank() -> error = "Precio requerido"
                    precio.toDoubleOrNull() == null -> error = "Precio debe ser número"
                    stock.isBlank() -> error = "Stock requerido"
                    stock.toIntOrNull() == null -> error = "Stock debe ser número"
                    descripcion.isBlank() -> error = "Descripción requerida"
                    else -> {
                        val newProducto = Producto(
                            id = producto?.id ?: ProductoRepository.getNextId(),
                            nombre = nombre,
                            precio = precio.toDouble(),
                            descripcion = descripcion,
                            stock = stock.toInt()
                        )
                        onConfirm(newProducto)
                    }
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}