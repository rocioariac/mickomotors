package com.example.ui.screens.workshop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Client
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.GarageSearchBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun ClientsListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val clients by repository.clients.collectAsState()
  val allVehicles by repository.vehicles.collectAsState()
  var query by remember { mutableStateOf("") }
  var clientToDelete by remember { mutableStateOf<Client?>(null) }

  clientToDelete?.let { targetClient ->
    ConfirmDeleteDialog(
      title = "Eliminar Cliente",
      message = "Esta acción eliminará el cliente de los registros. Esta acción no se puede deshacer.",
      itemName = "${targetClient.name} (DNI/RUC: ${targetClient.dniRuc})",
      onConfirm = {
        repository.deleteClient(targetClient.id)
        clientToDelete = null
      },
      onDismiss = { clientToDelete = null }
    )
  }

  val filteredClients = remember(clients, query) {
    if (query.isBlank()) clients
    else clients.filter {
      it.name.contains(query, ignoreCase = true) ||
      it.phone.contains(query, ignoreCase = true) ||
      it.dniRuc.contains(query, ignoreCase = true)
    }
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      GarageSearchBar(
        query = query,
        onQueryChange = { query = it },
        placeholderText = "Buscar cliente por nombre, DNI o teléfono..."
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "CLIENTES REGISTRADOS (${filteredClients.size})",
        color = GarageTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredClients) { client ->
          val clientVehicles = allVehicles.filter { it.ownerClientId == client.id || it.ownerName.equals(client.name, ignoreCase = true) }

          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.ClientDetail(client.id)) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = client.name,
                  color = GarageTextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "DNI/RUC: ${client.dniRuc} • Tel: ${client.phone}",
                  color = GarageTextSecondary,
                  fontSize = 12.sp
                )
                if (clientVehicles.isNotEmpty()) {
                  Text(
                    text = "Vehículos: ${clientVehicles.joinToString { it.plate }}",
                    color = GarageRed,
                    fontSize = 12.sp
                  )
                }
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                IconButton(
                  onClick = { clientToDelete = client },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar cliente",
                    tint = GarageRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                  contentDescription = "Ver ficha",
                  tint = GarageTextMuted,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ClientDetailScreen(
  clientId: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val clients by repository.clients.collectAsState()
  val orders by repository.workOrders.collectAsState()
  val allVehicles by repository.vehicles.collectAsState()
  var showDeleteDialog by remember { mutableStateOf(false) }

  val client = clients.find { it.id == clientId }
  val clientOrders = orders.filter { it.clientId == clientId || it.clientName.equals(client?.name, ignoreCase = true) }
  val clientVehicles = allVehicles.filter { it.ownerClientId == clientId || it.ownerName.equals(client?.name, ignoreCase = true) }

  if (client == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cliente no encontrado: $clientId", color = GarageTextSecondary)
      }
    }
    return
  }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "Eliminar Cliente",
      message = "Esta acción eliminará permanentemente la ficha del cliente y sus datos de contacto. No se puede deshacer.",
      itemName = "${client.name} (DNI/RUC: ${client.dniRuc})",
      onConfirm = {
        repository.deleteClient(client.id)
        onNavigateToScreen(Screen.ClientsList)
      },
      onDismiss = { showDeleteDialog = false }
    )
  }

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(client.name, color = GarageTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
              IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Eliminar cliente",
                  tint = GarageRed,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Documento: ${client.dniRuc}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Teléfono: ${client.phone}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Email: ${client.email}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Dirección: ${client.address}", color = GarageTextSecondary, fontSize = 13.sp)
          }
        }
      }

      item {
        Text("VEHÍCULOS ASOCIADOS", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(clientVehicles) { veh ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCardElevated),
          modifier = Modifier.fillMaxWidth().clickable { onNavigateToScreen(Screen.VehicleDetail(veh.id)) }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GarageRed, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Placa: ${veh.plate} (${veh.brand} ${veh.model})", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = GarageTextMuted, modifier = Modifier.size(12.dp))
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text("HISTORIAL DE ÓRDENES EN TALLER (${clientOrders.size})", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(clientOrders) { ord ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth().clickable { onNavigateToScreen(Screen.OrderDetail(ord.id)) }
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(ord.id, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("S/ ${String.format("%.2f", ord.total)}", color = GarageTextPrimary, fontWeight = FontWeight.Bold)
            }
            Text(ord.vehicleModel, color = GarageTextSecondary, fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@Composable
fun NewClientScreen(
  repository: GarageRepository,
  onClientSaved: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var doc by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("DATOS DEL NUEVO CLIENTE", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nombre o Razón Social *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = doc,
        onValueChange = { doc = it },
        label = { Text("DNI o RUC *") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = phone,
        onValueChange = { phone = it },
        label = { Text("Teléfono / WhatsApp *") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Correo electrónico") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        label = { Text("Dirección fiscal o domicilio") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          if (name.isNotBlank() && doc.isNotBlank()) {
            val newClient = Client(
              id = "CLI-00${(10..99).random()}",
              name = name,
              dniRuc = doc,
              phone = phone,
              email = email,
              address = address
            )
            repository.saveClient(newClient)
            onClientSaved()
          }
        },
        enabled = name.isNotBlank() && doc.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("GUARDAR CLIENTE", fontWeight = FontWeight.Bold)
      }
    }
  }
}
