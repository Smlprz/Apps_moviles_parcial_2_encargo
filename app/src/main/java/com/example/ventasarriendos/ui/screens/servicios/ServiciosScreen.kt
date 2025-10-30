package com.example.ventasarriendos.ui.screens.servicios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ventasarriendos.data.model.Servicio
import com.example.ventasarriendos.data.repository.ServicioRepository
import com.example.ventasarriendos.data.repository.UsuarioRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(navController: NavController) {
    var servicios by remember { mutableStateOf(ServicioRepository.getServicios()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingServicio by remember { mutableStateOf<Servicio?>(null) }

    val esAdmin = UsuarioRepository.esAdministrador()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Servicios") },
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
                        editingServicio = null
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
            items(servicios) { servicio ->
                TarjetaServicio(
                    servicio = servicio,
                    esAdmin = esAdmin,
                    onEdit = {
                        editingServicio = servicio
                        showDialog = true
                    },
                    onDelete = {
                        ServicioRepository.removeServicio(servicio)
                        servicios = ServicioRepository.getServicios()
                    }
                )
            }
        }

        if (showDialog) {
            DialogoServicio(
                servicio = editingServicio,
                onDismiss = { showDialog = false },
                onConfirm = { servicio ->
                    if (editingServicio == null) {
                        ServicioRepository.addServicio(servicio)
                    } else {
                        ServicioRepository.updateServicio(servicio)
                    }
                    servicios = ServicioRepository.getServicios()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TarjetaServicio(
    servicio: Servicio,
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
            Text(servicio.nombre, style = MaterialTheme.typography.titleMedium)
            Text("$${servicio.precio}", style = MaterialTheme.typography.bodyMedium)
            Text("Duración: ${servicio.duracion}", style = MaterialTheme.typography.bodySmall)
            Text(servicio.descripcion, style = MaterialTheme.typography.bodySmall)

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
fun DialogoServicio(
    servicio: Servicio?,
    onDismiss: () -> Unit,
    onConfirm: (Servicio) -> Unit
) {
    var nombre by remember { mutableStateOf(servicio?.nombre ?: "") }
    var precio by remember { mutableStateOf(servicio?.precio.toString() ?: "") }
    var descripcion by remember { mutableStateOf(servicio?.descripcion ?: "") }
    var duracion by remember { mutableStateOf(servicio?.duracion ?: "") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (servicio == null) "Nuevo Servicio" else "Editar Servicio") },
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
                    value = duracion, onValueChange = { duracion = it; error = "" },
                    label = { Text("Duración") }, modifier = Modifier.fillMaxWidth()
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
                    duracion.isBlank() -> error = "Duración requerida"
                    descripcion.isBlank() -> error = "Descripción requerida"
                    else -> {
                        val newServicio = Servicio(
                            id = servicio?.id ?: ServicioRepository.getNextId(),
                            nombre = nombre,
                            precio = precio.toDouble(),
                            descripcion = descripcion,
                            duracion = duracion
                        )
                        onConfirm(newServicio)
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