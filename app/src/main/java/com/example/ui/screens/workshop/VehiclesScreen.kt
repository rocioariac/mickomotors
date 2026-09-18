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
import com.example.data.model.Vehicle
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.GarageSearchBar
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun VehiclesListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val vehicles by repository.vehicles.collectAsState()
  var query by remember { mutableStateOf("") }
  var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

  vehicleToDelete?.let { targetVeh ->
    ConfirmDeleteDialog(
      title = "Eliminar Vehículo",
      message = "Esta acción eliminará el vehículo del registro y de las búsquedas del taller. No se puede deshacer.",
      itemName = "${targetVeh.plate} - ${targetVeh.brand} ${targetVeh.model} (${targetVeh.ownerName})",
      onConfirm = {
        repository.deleteVehicle(targetVeh.id)
        vehicleToDelete = null
      },
      onDismiss = { vehicleToDelete = null }
    )
  }

  val filteredVehicles = remember(vehicles, query) {
    if (query.isBlank()) vehicles
    else vehicles.filter {
      it.plate.contains(query, ignoreCase = true) ||
      it.model.contains(query, ignoreCase = true) ||
      it.brand.contains(query, ignoreCase = true) ||
      it.vin.contains(query, ignoreCase = true) ||
      it.ownerName.contains(query, ignoreCase = true)
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
        placeholderText = "Buscar por placa, modelo, VIN o dueño..."
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "PARQUE AUTOMOTOR REGISTRADO (${filteredVehicles.size})",
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
        items(filteredVehicles) { vehicle ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.VehicleDetail(vehicle.plate)) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GarageRed.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 8.dp)
                  ) {
                    Text(
                      text = vehicle.plate,
                      color = GarageRed,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(
                    text = "${vehicle.brand} ${vehicle.model} (${vehicle.year})",
                    color = GarageTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Dueño: ${vehicle.ownerName} • VIN: ${vehicle.vin}",
                  color = GarageTextSecondary,
                  fontSize = 12.sp
                )
                Text(
                  text = "Motor: ${vehicle.engine} • Km: ${vehicle.mileageKm} km",
                  color = GarageTextMuted,
                  fontSize = 11.sp
                )
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                IconButton(
                  onClick = { vehicleToDelete = vehicle },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar vehículo",
                    tint = GarageRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                  contentDescription = "Ver vehículo",
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
fun VehicleDetailScreen(
  plate: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val vehicles by repository.vehicles.collectAsState()
  val orders by repository.workOrders.collectAsState()
  var showDeleteDialog by remember { mutableStateOf(false) }

  val vehicle = vehicles.find { it.plate.equals(plate, ignoreCase = true) }

  if (vehicle == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Vehículo no encontrado: $plate", color = GarageTextSecondary)
      }
    }
    return
  }

  val vehicleOrders = orders.filter { it.vehiclePlate.equals(vehicle.plate, ignoreCase = true) }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "Eliminar Vehículo",
      message = "Esta acción eliminará permanentemente la ficha del vehículo de la base de datos.",
      itemName = "${vehicle.plate} - ${vehicle.brand} ${vehicle.model}",
      onConfirm = {
        repository.deleteVehicle(vehicle.id)
        onNavigateToScreen(Screen.VehiclesList)
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
              Text(vehicle.plate, color = GarageRed, fontSize = 22.sp, fontWeight = FontWeight.Black)
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${vehicle.year}", color = GarageTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                  onClick = { showDeleteDialog = true },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar vehículo",
                    tint = GarageRed,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${vehicle.brand} ${vehicle.model}", color = GarageTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = GarageBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Propietario: ${vehicle.ownerName}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("VIN / N° Chasis: ${vehicle.vin}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Motor: ${vehicle.engine} • ${vehicle.transmission}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Kilometraje actual: ${vehicle.mileageKm} km", color = GarageTextSecondary, fontSize = 13.sp)
          }
        }
      }

      item {
        Text("HISTORIAL DE INGRESOS A TALLER (${vehicleOrders.size})", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(vehicleOrders) { ord ->
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
            Spacer(modifier = Modifier.height(4.dp))
            Text("Falla: ${ord.reportedProblem}", color = GarageTextSecondary, fontSize = 12.sp, maxLines = 2)
          }
        }
      }
    }
  }
}

@Composable
fun NewVehicleScreen(
  repository: GarageRepository,
  onVehicleSaved: () -> Unit
) {
  var plate by remember { mutableStateOf("") }
  var brand by remember { mutableStateOf("") }
  var model by remember { mutableStateOf("") }
  var year by remember { mutableStateOf("2021") }
  var vin by remember { mutableStateOf("") }
  var color by remember { mutableStateOf("") }
  var ownerName by remember { mutableStateOf("") }
  var mileageKm by remember { mutableStateOf("") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("FICHA TÉCNICA DEL VEHÍCULO", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = plate,
        onValueChange = { plate = it.uppercase() },
        label = { Text("Placa del vehículo *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = brand,
          onValueChange = { brand = it },
          label = { Text("Marca *") },
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = model,
          onValueChange = { model = it },
          label = { Text("Modelo *") },
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = year,
          onValueChange = { year = it },
          label = { Text("Año") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = color,
          onValueChange = { color = it },
          label = { Text("Color") },
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      OutlinedTextField(
        value = vin,
        onValueChange = { vin = it.uppercase() },
        label = { Text("VIN / Número de Serie") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = ownerName,
        onValueChange = { ownerName = it },
        label = { Text("Nombre del Propietario *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = mileageKm,
        onValueChange = { mileageKm = it },
        label = { Text("Kilometraje (km)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          if (plate.isNotBlank() && brand.isNotBlank() && ownerName.isNotBlank()) {
            val newV = Vehicle(
              id = "VEH-${(100..999).random()}",
              plate = plate.uppercase(),
              brand = brand,
              model = model.ifBlank { "Sedan" },
              year = year.toIntOrNull() ?: 2020,
              vin = vin.ifBlank { "N/D" },
              ownerName = ownerName,
              mileageKm = mileageKm.toIntOrNull() ?: 45000
            )
            repository.saveVehicle(newV)
            onVehicleSaved()
          }
        },
        enabled = plate.isNotBlank() && brand.isNotBlank() && ownerName.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("REGISTRAR VEHÍCULO", fontWeight = FontWeight.Bold)
      }
    }
  }
}
