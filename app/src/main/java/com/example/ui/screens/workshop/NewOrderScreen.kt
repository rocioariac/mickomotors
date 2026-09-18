package com.example.ui.screens.workshop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkOrder
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.UnsavedChangesDialog
import com.example.ui.theme.*

@Composable
fun NewOrderScreen(
  repository: GarageRepository,
  onOrderCreated: (String) -> Unit,
  onNavigateBack: () -> Unit
) {
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var vehicleModel by remember { mutableStateOf("") }
  var vehiclePlate by remember { mutableStateOf("") }
  var mileageKm by remember { mutableStateOf("") }
  var reportedProblem by remember { mutableStateOf("") }
  var technician by remember { mutableStateOf("Marcos Peña") }
  var laborCost by remember { mutableStateOf("150.0") }

  var currentStep by remember { mutableIntStateOf(1) }
  var showUnsavedDialog by remember { mutableStateOf(false) }

  val hasChanges = clientName.isNotBlank() || vehiclePlate.isNotBlank() || reportedProblem.isNotBlank()

  fun performSave() {
    if (clientName.isBlank() || vehiclePlate.isBlank()) return
    val newId = "OT-00${(219..999).random()}"
    val createdOrder = WorkOrder(
      id = newId,
      clientId = "CLI-${(100..999).random()}",
      clientName = clientName,
      clientPhone = clientPhone.ifBlank { "999-000-000" },
      vehicleId = "VEH-${(100..999).random()}",
      vehiclePlate = vehiclePlate.uppercase(),
      vehicleModel = vehicleModel.ifBlank { "Vehículo Particular" },
      mileageKm = mileageKm.toIntOrNull() ?: 50000,
      reportedProblem = reportedProblem.ifBlank { "Revisión general del sistema" },
      technician = technician,
      laborCost = laborCost.toDoubleOrNull() ?: 150.0,
      services = emptyList(),
      parts = emptyList(),
      payments = emptyList(),
      status = com.example.data.model.OrderStatus.RECIBIDO
    )
    repository.saveWorkOrder(createdOrder)
    onOrderCreated(newId)
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // STEP INDICATOR (1 to 5)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        listOf("1. Cliente", "2. Auto", "3. Fallas", "4. Mano obra", "5. Fin").forEachIndexed { index, stepName ->
          val stepNumber = index + 1
          val isActive = currentStep == stepNumber
          val isDone = currentStep > stepNumber

          Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            Surface(
              shape = androidx.compose.foundation.shape.CircleShape,
              color = when {
                isActive -> GarageRed
                isDone -> StatusReady
                else -> GarageCardElevated
              },
              modifier = Modifier.size(24.dp)
            ) {
              Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                if (isDone) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = GarageTextPrimary, modifier = Modifier.size(14.dp))
                } else {
                  Text("$stepNumber", color = GarageTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(stepName, color = if (isActive) GarageRed else GarageTextMuted, fontSize = 9.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // STEP 1: CLIENTE
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("PASO 1: DATOS DEL CLIENTE", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)

          OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Nombre completo del cliente *") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = clientPhone,
            onValueChange = { clientPhone = it },
            label = { Text("Teléfono de contacto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // STEP 2: VEHÍCULO
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("PASO 2: VEHÍCULO", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)

          OutlinedTextField(
            value = vehiclePlate,
            onValueChange = { vehiclePlate = it.uppercase() },
            label = { Text("Placa del vehículo (ej: V8X-321) *") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = vehicleModel,
            onValueChange = { vehicleModel = it },
            label = { Text("Marca y Modelo (ej: Toyota RAV4 2020)") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = mileageKm,
            onValueChange = { mileageKm = it },
            label = { Text("Kilometraje actual (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // STEP 3: RECEPCIÓN Y FALLAS
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("PASO 3: RECEPCIÓN Y MOTIVO DE INGRESO", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)

          OutlinedTextField(
            value = reportedProblem,
            onValueChange = { reportedProblem = it },
            label = { Text("Problema reportado por el cliente *") },
            minLines = 3,
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = technician,
            onValueChange = { technician = it },
            label = { Text("Mecánico o técnico asignado") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // STEP 4: MANO DE OBRA
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("PASO 4: MANO DE OBRA BASE", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)

          OutlinedTextField(
            value = laborCost,
            onValueChange = { laborCost = it },
            label = { Text("Costo estimado de mano de obra (S/)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // SUBMIT BUTTON
      Button(
        onClick = { performSave() },
        enabled = clientName.isNotBlank() && vehiclePlate.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
      ) {
        Text(
          text = "CREAR ORDEN DE TRABAJO",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  if (showUnsavedDialog) {
    UnsavedChangesDialog(
      onContinueEditing = { showUnsavedDialog = false },
      onSaveAndExit = {
        showUnsavedDialog = false
        performSave()
      },
      onDiscardChanges = {
        showUnsavedDialog = false
        onNavigateBack()
      }
    )
  }
}

@Composable
fun formFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedContainerColor = GarageCardElevated,
  unfocusedContainerColor = GarageCardElevated,
  focusedBorderColor = GarageRed,
  unfocusedBorderColor = GarageBorder,
  focusedTextColor = GarageTextPrimary,
  unfocusedTextColor = GarageTextPrimary,
  cursorColor = GarageRed
)
