package com.example.ui.screens.workshop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.WorkOrder
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.GarageSearchBar
import com.example.ui.components.OrderStatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun OrdersListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val orders by repository.workOrders.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedTab by remember { mutableStateOf("Activas") }
  val tabs = listOf("Todas", "Activas", "Listas", "Entregadas")
  var orderToDelete by remember { mutableStateOf<WorkOrder?>(null) }

  orderToDelete?.let { targetOrder ->
    ConfirmDeleteDialog(
      title = "Eliminar Orden de Trabajo",
      message = "Esta acción eliminará la orden de trabajo permanentemente del sistema. Quedará registrada en la auditoría.",
      itemName = "${targetOrder.id} - ${targetOrder.vehiclePlate} (${targetOrder.vehicleModel})",
      onConfirm = {
        repository.deleteWorkOrder(targetOrder.id)
        orderToDelete = null
      },
      onDismiss = { orderToDelete = null }
    )
  }

  val filteredOrders = remember(orders, searchQuery, selectedTab) {
    orders.filter { order ->
      val matchesTab = when (selectedTab) {
        "Activas" -> order.status != OrderStatus.ENTREGADO && order.status != OrderStatus.LISTO
        "Listas" -> order.status == OrderStatus.LISTO
        "Entregadas" -> order.status == OrderStatus.ENTREGADO
        else -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
        order.id.contains(searchQuery, ignoreCase = true) ||
        order.vehiclePlate.contains(searchQuery, ignoreCase = true) ||
        order.clientName.contains(searchQuery, ignoreCase = true) ||
        order.vehicleModel.contains(searchQuery, ignoreCase = true)

      matchesTab && matchesSearch
    }
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      GarageSearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholderText = "Buscar por OT, placa o cliente..."
      )

      Spacer(modifier = Modifier.height(10.dp))

      // FILTER CHIPS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        tabs.forEach { tab ->
          val isSelected = selectedTab == tab
          FilterChip(
            selected = isSelected,
            onClick = { selectedTab = tab },
            label = { Text(tab, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GarageRed,
              selectedLabelColor = GarageTextPrimary,
              containerColor = GarageCard,
              labelColor = GarageTextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) GarageRed else GarageBorder
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "ÓRDENES DE TRABAJO (${filteredOrders.size})",
        color = GarageTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredOrders) { order ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.OrderDetail(order.id)) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = order.id,
                    color = GarageRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                  )
                  OrderStatusBadge(status = order.status)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = order.vehicleModel,
                  color = GarageTextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )

                Text(
                  text = "Placa: ${order.vehiclePlate} • ${order.clientName}",
                  color = GarageTextSecondary,
                  fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Total: S/ ${String.format("%.2f", order.total)}",
                    color = GarageTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                  )
                  if (order.pending > 0.01) {
                    Text(
                      text = "Pendiente: S/ ${String.format("%.2f", order.pending)}",
                      color = GarageRed,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                  } else {
                    Text(
                      text = "✓ PAGADO",
                      color = StatusReady,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                IconButton(
                  onClick = { orderToDelete = order },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar orden",
                    tint = GarageRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                  contentDescription = "Ver detalle",
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
