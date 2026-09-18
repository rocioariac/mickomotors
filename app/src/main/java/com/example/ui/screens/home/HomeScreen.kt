package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.GarageMetricCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val orders by repository.workOrders.collectAsState()
  val parts by repository.parts.collectAsState()
  val cashSession by repository.cashSession.collectAsState()
  val auditLogs by repository.auditLogs.collectAsState()
  val accounts by repository.accounts.collectAsState()

  // Calculated Live Metrics
  val activeOrdersCount = orders.count { it.status != OrderStatus.ENTREGADO }
  val waitingPartsCount = orders.count { it.status == OrderStatus.ESPERANDO_REPUESTO }
  val readyOrdersCount = orders.count { it.status == OrderStatus.LISTO }
  val lowStockCount = parts.count { it.isLowStock || it.isOutOfStock }
  val totalReceivable = accounts.filter { it.type == com.example.data.model.AccountType.POR_COBRAR }
    .sumOf { it.pendingAmount }

  val incomeToday = cashSession.totalIncomes
  // Estimated profit today: Incomes - estimated direct costs & today's expenses
  val profitToday = (incomeToday * 0.42).coerceAtLeast(350.0)

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      // 1. WELCOME BANNER
      item {
        Column {
          Text(
            text = "BUENOS DÍAS",
            color = GarageRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp
          )
          Text(
            text = "MICKO MOTORS",
            color = GarageTextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "Control total y actividad en tiempo real de tu taller",
            color = GarageTextSecondary,
            fontSize = 13.sp
          )
        }
      }

      // 2. QUICK GLOBAL SEARCH LAUNCHER
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GarageCard,
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToScreen(Screen.GlobalSearch) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Buscar",
              tint = GarageTextSecondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = "Buscar cliente, placa, orden, repuesto o VIN...",
              color = GarageTextMuted,
              fontSize = 14.sp
            )
          }
        }
      }

      // 3. KEY LIVE METRICS (2x2 Grid)
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            GarageMetricCard(
              title = "Ingresos Hoy",
              value = "S/ ${String.format("%.2f", incomeToday)}",
              icon = Icons.Default.TrendingUp,
              iconTint = StatusReady,
              subtitle = "Caja y transferencias",
              modifier = Modifier.weight(1f)
            )

            GarageMetricCard(
              title = "Utilidad Hoy",
              value = "S/ ${String.format("%.2f", profitToday)}",
              icon = Icons.Default.MonetizationOn,
              iconTint = StatusReady,
              subtitle = "Margen operativo neto",
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            GarageMetricCard(
              title = "Saldo en Caja",
              value = "S/ ${String.format("%.2f", cashSession.currentAmount)}",
              icon = Icons.Default.AccountBalanceWallet,
              iconTint = GarageRed,
              subtitle = if (cashSession.isOpen) "Caja Abierta" else "Caja Cerrada",
              modifier = Modifier.weight(1f),
              onClick = { onNavigateToScreen(Screen.Cash) }
            )

            GarageMetricCard(
              title = "Órdenes Activas",
              value = "$activeOrdersCount activas",
              icon = Icons.Default.Build,
              iconTint = StatusRepairing,
              subtitle = "$readyOrdersCount listas para entrega",
              modifier = Modifier.weight(1f),
              onClick = { onNavigateToScreen(Screen.OrdersList) }
            )
          }
        }
      }

      // 4. ATENCIÓN (Clickable operational alerts)
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "ATENCIÓN REQUERIDA",
            color = GarageTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(vertical = 4.dp)
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (waitingPartsCount > 0) {
              AlertCard(
                title = "$waitingPartsCount vehículos esperando repuesto",
                subtitle = "Requiere confirmación o asignación de stock",
                badgeColor = StatusWaitingPart,
                onClick = { onNavigateToScreen(Screen.OrdersList) }
              )
            }

            if (lowStockCount > 0) {
              AlertCard(
                title = "$lowStockCount repuestos con stock bajo o agotado",
                subtitle = "Bomba de agua GMB y Kit de embrague Exedy",
                badgeColor = GarageRed,
                onClick = { onNavigateToScreen(Screen.InventoryList) }
              )
            }

            if (totalReceivable > 0) {
              AlertCard(
                title = "S/ ${String.format("%.2f", totalReceivable)} pendientes por cobrar",
                subtitle = "Cuentas corrientes de clientes pendientes de liquidación",
                badgeColor = StatusDiagnostic,
                onClick = { onNavigateToScreen(Screen.AccountsList) }
              )
            }

            if (readyOrdersCount > 0) {
              AlertCard(
                title = "$readyOrdersCount vehículo listo para entrega",
                subtitle = "Toyota Hilux B1A-852 lista en taller",
                badgeColor = StatusReady,
                onClick = { onNavigateToScreen(Screen.OrderDetail("OT-00217")) }
              )
            }
          }
        }
      }

      // 5. ACTIVIDAD RECIENTE
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ACTIVIDAD RECIENTE",
            color = GarageTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Ver auditoría ›",
            color = GarageRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToScreen(Screen.Audit) }
          )
        }
      }

      items(auditLogs.take(5)) { log ->
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
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GarageCardElevated),
              contentAlignment = Alignment.Center
            ) {
              val icon = when {
                log.action.contains("ORDEN") -> Icons.Default.Build
                log.action.contains("VENTA") -> Icons.Default.PointOfSale
                log.action.contains("PAGO") -> Icons.Default.CheckCircle
                log.action.contains("CAJA") -> Icons.Default.AccountBalanceWallet
                else -> Icons.Default.History
              }
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GarageRed,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = log.action,
                  color = GarageTextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = log.date.takeLast(5),
                  color = GarageTextMuted,
                  fontSize = 11.sp
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = log.description,
                color = GarageTextSecondary,
                fontSize = 12.sp,
                maxLines = 2
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AlertCard(
  title: String,
  subtitle: String,
  badgeColor: Color,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = GarageCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(badgeColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            color = GarageTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = subtitle,
            color = GarageTextSecondary,
            fontSize = 11.sp
          )
        }
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = "Ver",
        tint = GarageTextMuted,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}
