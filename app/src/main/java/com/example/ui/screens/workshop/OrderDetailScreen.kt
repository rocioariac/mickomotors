package com.example.ui.screens.workshop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.Part
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.QuickPaymentSheet
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
  orderId: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val orders by repository.workOrders.collectAsState()
  val allParts by repository.parts.collectAsState()
  val order = orders.find { it.id == orderId }

  var selectedTab by remember { mutableStateOf("RESUMEN") }
  val tabs = listOf("RESUMEN", "TRABAJO", "REPUESTOS", "PAGOS")

  // Modals inside detail
  var showPaymentSheet by remember { mutableStateOf(false) }
  var showAddServiceDialog by remember { mutableStateOf(false) }
  var showAddPartDialog by remember { mutableStateOf(false) }
  var showStatusMenu by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  if (order == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Orden no encontrada: $orderId", color = GarageTextSecondary)
      }
    }
    return
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      // ORDER TOP SUMMARY HEADER
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = order.id,
                color = GarageRed,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = order.vehicleModel,
                color = GarageTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GarageCardElevated,
                  modifier = Modifier.clickable { showStatusMenu = true }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OrderStatusBadge(status = order.status)
                    Icon(
                      imageVector = Icons.Default.ArrowDropDown,
                      contentDescription = "Cambiar estado",
                      tint = GarageTextSecondary,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }

                DropdownMenu(
                  expanded = showStatusMenu,
                  onDismissRequest = { showStatusMenu = false },
                  modifier = Modifier.background(GarageCard)
                ) {
                  OrderStatus.entries.forEach { status ->
                    DropdownMenuItem(
                      text = { Text(status.label, color = GarageTextPrimary) },
                      onClick = {
                        repository.updateOrderStatus(order.id, status)
                        showStatusMenu = false
                      }
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.width(4.dp))

              IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Eliminar orden",
                  tint = GarageRed,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          if (showDeleteDialog) {
            ConfirmDeleteDialog(
              title = "Eliminar Orden de Trabajo",
              message = "Esta acción eliminará la orden de trabajo permanentemente del sistema. Quedará registrada en la auditoría.",
              itemName = "${order.id} - ${order.vehiclePlate} (${order.vehicleModel})",
              onConfirm = {
                repository.deleteWorkOrder(order.id)
                onNavigateToScreen(Screen.OrdersList)
              },
              onDismiss = { showDeleteDialog = false }
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Placa: ${order.vehiclePlate}",
              color = GarageTextSecondary,
              fontSize = 13.sp
            )
            Text(
              text = "Km: ${order.mileageKm} km",
              color = GarageTextSecondary,
              fontSize = 13.sp
            )
            Text(
              text = "Cliente: ${order.clientName}",
              color = GarageTextSecondary,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // TAB NAVIGATION ROW
      TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor = GarageCard,
        contentColor = GarageRed,
        divider = { HorizontalDivider(color = GarageBorder) }
      ) {
        tabs.forEach { tabTitle ->
          Tab(
            selected = selectedTab == tabTitle,
            onClick = { selectedTab = tabTitle },
            text = {
              Text(
                text = tabTitle,
                fontSize = 12.sp,
                fontWeight = if (selectedTab == tabTitle) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == tabTitle) GarageRed else GarageTextSecondary
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // TAB CONTENT
      when (selectedTab) {
        "RESUMEN" -> {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            item {
              SectionCard(title = "INFORMACIÓN DEL CLIENTE Y VEHÍCULO") {
                InfoRow("Cliente", order.clientName)
                InfoRow("Teléfono", order.clientPhone)
                InfoRow("Vehículo", order.vehicleModel)
                InfoRow("Placa", order.vehiclePlate)
                InfoRow("Kilometraje", "${order.mileageKm} km")
                InfoRow("Técnico asignado", order.technician)
                InfoRow("Fecha creación", order.createdAt)
              }
            }

            item {
              SectionCard(title = "PROBLEMA REPORTADO") {
                Text(
                  text = order.reportedProblem,
                  color = GarageTextPrimary,
                  fontSize = 14.sp,
                  lineHeight = 18.sp
                )
              }
            }

            if (order.diagnostic.isNotBlank()) {
              item {
                SectionCard(title = "DIAGNÓSTICO TÉCNICO") {
                  Text(
                    text = order.diagnostic,
                    color = GarageTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                  )
                }
              }
            }
          }
        }

        "TRABAJO" -> {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "SERVICIOS (${order.services.size})",
                  color = GarageTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Button(
                  onClick = { showAddServiceDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Agregar servicio", fontSize = 12.sp)
                }
              }
            }

            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = GarageCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Mano de obra base", color = GarageTextPrimary, fontSize = 14.sp)
                  Text("S/ ${String.format("%.2f", order.laborCost)}", color = GarageTextPrimary, fontWeight = FontWeight.Bold)
                }
              }
            }

            items(order.services) { srv ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = GarageCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = srv.description,
                    color = GarageTextPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                  )
                  Text(
                    text = "S/ ${String.format("%.2f", srv.cost)}",
                    color = GarageRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
              }
            }
          }
        }

        "REPUESTOS" -> {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "REPUESTOS INSTALADOS (${order.parts.size})",
                  color = GarageTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Button(
                  onClick = { showAddPartDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Agregar repuesto", fontSize = 12.sp)
                }
              }
            }

            if (order.parts.isEmpty()) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("No se han cargado repuestos a esta orden todavía", color = GarageTextMuted, fontSize = 13.sp)
                }
              }
            } else {
              items(order.parts) { partItem ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = GarageCard),
                  border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(partItem.name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                      Text("N° ${partItem.partNumber} • Cant: ${partItem.quantity} x S/ ${partItem.unitPrice}", color = GarageTextSecondary, fontSize = 12.sp)
                    }
                    Text("S/ ${String.format("%.2f", partItem.total)}", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  }
                }
              }
            }
          }
        }

        "PAGOS" -> {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
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
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("TOTAL DE LA ORDEN:", color = GarageTextSecondary, fontSize = 13.sp)
                    Text("S/ ${String.format("%.2f", order.total)}", color = GarageTextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
                  }

                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("MONTO PAGADO:", color = GarageTextSecondary, fontSize = 13.sp)
                    Text("S/ ${String.format("%.2f", order.paid)}", color = StatusReady, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  HorizontalDivider(color = GarageBorder)
                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("SALDO PENDIENTE:", color = GarageTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("S/ ${String.format("%.2f", order.pending)}", color = GarageRed, fontWeight = FontWeight.Black, fontSize = 17.sp)
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  if (order.pending > 0.01) {
                    Button(
                      onClick = { showPaymentSheet = true },
                      colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Icon(Icons.Default.Payment, contentDescription = null)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("REGISTRAR PAGO", fontWeight = FontWeight.Bold)
                    }
                  } else {
                    Surface(
                      color = StatusReady.copy(alpha = 0.15f),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(
                        text = "✓ ORDEN TOTALMENTE PAGADA",
                        color = StatusReady,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                      )
                    }
                  }
                }
              }
            }

            item {
              Text(
                text = "HISTORIAL DE PAGOS (${order.payments.size})",
                color = GarageTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            items(order.payments) { pay ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = GarageCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(pay.method, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${pay.date} • ${pay.reference}", color = GarageTextSecondary, fontSize = 11.sp)
                  }
                  Text("S/ ${String.format("%.2f", pay.amount)}", color = StatusReady, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  // DIALOG: AGREGAR SERVICIO
  if (showAddServiceDialog) {
    var serviceDesc by remember { mutableStateOf("") }
    var serviceCost by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddServiceDialog = false },
      containerColor = GarageCard,
      title = { Text("Agregar servicio", color = GarageTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = serviceDesc,
            onValueChange = { serviceDesc = it },
            label = { Text("Descripción del servicio") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = GarageCardElevated,
              unfocusedContainerColor = GarageCardElevated,
              focusedBorderColor = GarageRed,
              unfocusedBorderColor = GarageBorder,
              focusedTextColor = GarageTextPrimary,
              unfocusedTextColor = GarageTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = serviceCost,
            onValueChange = { serviceCost = it },
            label = { Text("Costo (S/)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = GarageCardElevated,
              unfocusedContainerColor = GarageCardElevated,
              focusedBorderColor = GarageRed,
              unfocusedBorderColor = GarageBorder,
              focusedTextColor = GarageTextPrimary,
              unfocusedTextColor = GarageTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val cost = serviceCost.toDoubleOrNull() ?: 0.0
            if (serviceDesc.isNotBlank()) {
              repository.addServiceToOrder(order.id, serviceDesc, cost)
              showAddServiceDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GarageRed)
        ) {
          Text("Agregar")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddServiceDialog = false }) {
          Text("Cancelar", color = GarageTextSecondary)
        }
      }
    )
  }

  // DIALOG: AGREGAR REPUESTO
  if (showAddPartDialog) {
    var selectedPart by remember { mutableStateOf(allParts.firstOrNull()) }
    var quantityInput by remember { mutableStateOf("1") }
    var expandedPartSelect by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showAddPartDialog = false },
      containerColor = GarageCard,
      title = { Text("Agregar repuesto de catálogo", color = GarageTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Seleccionar repuesto:", color = GarageTextSecondary, fontSize = 12.sp)

          Box {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GarageCardElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedPartSelect = true }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = selectedPart?.let { "${it.name} (Stock: ${it.stock})" } ?: "Seleccionar",
                  color = GarageTextPrimary,
                  fontSize = 13.sp
                )
                Text("▼", color = GarageTextSecondary, fontSize = 10.sp)
              }
            }

            DropdownMenu(
              expanded = expandedPartSelect,
              onDismissRequest = { expandedPartSelect = false },
              modifier = Modifier.background(GarageCard)
            ) {
              allParts.forEach { part ->
                DropdownMenuItem(
                  text = { Text("${part.name} - Stock: ${part.stock} (S/ ${part.salePrice})", color = GarageTextPrimary) },
                  onClick = {
                    selectedPart = part
                    expandedPartSelect = false
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = quantityInput,
            onValueChange = { quantityInput = it },
            label = { Text("Cantidad a instalar") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = GarageCardElevated,
              unfocusedContainerColor = GarageCardElevated,
              focusedBorderColor = GarageRed,
              unfocusedBorderColor = GarageBorder,
              focusedTextColor = GarageTextPrimary,
              unfocusedTextColor = GarageTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val part = selectedPart
            val qty = quantityInput.toIntOrNull() ?: 1
            if (part != null) {
              repository.addPartToOrder(order.id, part, qty, part.salePrice)
              showAddPartDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GarageRed)
        ) {
          Text("Instalar")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddPartDialog = false }) {
          Text("Cancelar", color = GarageTextSecondary)
        }
      }
    )
  }

  // BOTTOM SHEET: REGISTRAR PAGO
  if (showPaymentSheet) {
    QuickPaymentSheet(
      operationRef = order.id,
      entityName = order.clientName,
      pendingAmount = order.pending,
      onDismiss = { showPaymentSheet = false },
      onConfirmPayment = { opRef, name, amt, method ->
        repository.registerPayment(opRef, name, amt, method)
      }
    )
  }
}

@Composable
private fun SectionCard(
  title: String,
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = GarageCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        color = GarageRed,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )
      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

@Composable
private fun InfoRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, color = GarageTextSecondary, fontSize = 13.sp)
    Text(value, color = GarageTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
  }
}
