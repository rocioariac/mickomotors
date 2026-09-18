package com.example.ui.screens.workshop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.DrillDownItemRow
import com.example.ui.components.GarageMetricCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun WorkshopHomeScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val orders by repository.workOrders.collectAsState()
  val clients by repository.clients.collectAsState()
  val vehicles by repository.vehicles.collectAsState()

  val activeOrdersCount = orders.count { it.status != OrderStatus.ENTREGADO }
  val readyOrdersCount = orders.count { it.status == OrderStatus.LISTO }
  val waitingPartsCount = orders.count { it.status == OrderStatus.ESPERANDO_REPUESTO }

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      // 1. WORKSHOP ACTIVITY METRICS
      item {
        Text(
          text = "ACTIVIDAD DEL TALLER",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          GarageMetricCard(
            title = "En Taller",
            value = "$activeOrdersCount",
            icon = Icons.Default.Build,
            iconTint = StatusRepairing,
            subtitle = "Órdenes en curso",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.OrdersList) }
          )

          GarageMetricCard(
            title = "Listos",
            value = "$readyOrdersCount",
            icon = Icons.Default.CheckCircle,
            iconTint = StatusReady,
            subtitle = "Para entrega inmediata",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.OrdersList) }
          )

          GarageMetricCard(
            title = "Por Repuesto",
            value = "$waitingPartsCount",
            icon = Icons.Default.HourglassEmpty,
            iconTint = StatusWaitingPart,
            subtitle = "Pendientes pieza",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.OrdersList) }
          )
        }
      }

      // 2. MÓDULOS DEL TALLER (Level 2 Drill-downs)
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "MÓDULOS DE REPARACIÓN",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DrillDownItemRow(
            icon = Icons.Default.Build,
            title = "Órdenes de Trabajo",
            badgeText = "${orders.size} OTs",
            onClick = { onNavigateToScreen(Screen.OrdersList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.People,
            title = "Clientes",
            badgeText = "${clients.size} registrados",
            onClick = { onNavigateToScreen(Screen.ClientsList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.DirectionsCar,
            title = "Vehículos",
            badgeText = "${vehicles.size} en ficha",
            onClick = { onNavigateToScreen(Screen.VehiclesList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.HomeRepairService,
            title = "Servicios y Mano de Obra",
            badgeText = "Catálogo taller",
            onClick = { onNavigateToScreen(Screen.ServicesList) }
          )
        }
      }

      // 3. ACTIVIDAD RECIENTE EN TALLER
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "ÓRDENES ACTIVAS DESTACADAS",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      items(orders.take(3)) { order ->
        Card(
          shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          onClick = { onNavigateToScreen(Screen.OrderDetail(order.id)) }
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
              Text(
                text = order.id,
                color = GarageRed,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              )
              OrderStatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "${order.vehicleModel} (${order.vehiclePlate})",
              color = GarageTextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Cliente: ${order.clientName} • Mecánico: ${order.technician}",
              color = GarageTextSecondary,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}
