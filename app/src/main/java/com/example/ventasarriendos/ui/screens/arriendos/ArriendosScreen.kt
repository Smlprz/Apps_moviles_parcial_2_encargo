package com.example.ventasarriendos.ui.screens.arriendos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ventasarriendos.data.model.ItemArriendo
import com.example.ventasarriendos.data.repository.ArriendoRepository
import com.example.ventasarriendos.data.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArriendosScreen(navController: NavController) {
    var arriendos by remember { mutableStateOf(ArriendoRepository.getArriendos()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingArriendo by remember { mutableStateOf<ItemArriendo?>(null) }

    val esAdmin = UsuarioRepository.esAdministrador()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ítems en Arriendo") },
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
                        editingArriendo = null
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("+")
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(arriendos) { arriendo ->
                TarjetaArriendo(
                    arriendo = arriendo,
                    esAdmin = esAdmin,
                    onEdit = {
                        editingArriendo = arriendo
                        showDialog = true
                    },
                    onDelete = {
                        ArriendoRepository.removeArriendo(arriendo)
                        arriendos = ArriendoRepository.getArriendos()
                    }
                )
            }
        }

        if (showDialog) {
            DialogoArriendo(
                arriendo = editingArriendo,
                onDismiss = { showDialog = false },
                onConfirm = { arriendo ->
                    if (editingArriendo == null) {
                        ArriendoRepository.addArriendo(arriendo)
                    } else {
                        ArriendoRepository.updateArriendo(arriendo)
                    }
                    arriendos = ArriendoRepository.getArriendos()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TarjetaArriendo(
    arriendo: ItemArriendo,
    esAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(arriendo.nombre, style = MaterialTheme.typography.titleMedium)
            Text("$${arriendo.precioPorDia} por día", style = MaterialTheme.typography.bodyMedium)
            Text(arriendo.descripcion, style = MaterialTheme.typography.bodySmall)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (arriendo.disponible) "Disponible" else "No disponible",
                    color = if (arriendo.disponible) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            if (esAdmin) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
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

@Composable
fun DialogoArriendo(
    arriendo: ItemArriendo?,
    onDismiss: () -> Unit,
    onConfirm: (ItemArriendo) -> Unit
) {
    var nombre by remember { mutableStateOf(arriendo?.nombre ?: "") }
    var precio by remember { mutableStateOf(arriendo?.precioPorDia.toString() ?: "") }
    var descripcion by remember { mutableStateOf(arriendo?.descripcion ?: "") }
    var disponible by remember { mutableStateOf(arriendo?.disponible ?: true) }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (arriendo == null) "Nuevo Ítem" else "Editar Ítem") },
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
                    label = { Text("Precio por día") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descripcion, onValueChange = { descripcion = it; error = "" },
                    label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = disponible,
                        onCheckedChange = { disponible = it }
                    )
                    Text("Disponible")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                when {
                    nombre.isBlank() -> error = "Nombre requerido"
                    precio.isBlank() -> error = "Precio requerido"
                    precio.toDoubleOrNull() == null -> error = "Precio debe ser número"
                    descripcion.isBlank() -> error = "Descripción requerida"
                    else -> {
                        val newArriendo = ItemArriendo(
                            id = arriendo?.id ?: ArriendoRepository.getNextId(),
                            nombre = nombre,
                            precioPorDia = precio.toDouble(),
                            descripcion = descripcion,
                            disponible = disponible
                        )
                        onConfirm(newArriendo)
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