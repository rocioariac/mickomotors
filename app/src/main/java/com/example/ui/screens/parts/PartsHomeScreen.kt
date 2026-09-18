package com.example.ui.screens.parts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ImportStatus
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.DrillDownItemRow
import com.example.ui.components.GarageMetricCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun PartsHomeScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val parts by repository.parts.collectAsState()
  val sales by repository.sales.collectAsState()
  val purchases by repository.purchases.collectAsState()
  val imports by repository.imports.collectAsState()

  val totalStockCount = parts.sumOf { it.stock }
  val lowStockCount = parts.count { it.isLowStock }
  val outOfStockCount = parts.count { it.isOutOfStock }
  val inTransitImports = imports.count { it.status == ImportStatus.EN_TRANSITO || it.status == ImportStatus.EN_ADUANA }

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      // 1. INDICADORES DE INVENTARIO
      item {
        Text(
          text = "ESTADO DE ALMACÉN Y SUMINISTRO",
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
            title = "Unidades",
            value = "$totalStockCount un.",
            icon = Icons.Default.Inventory2,
            iconTint = GarageTextPrimary,
            subtitle = "${parts.size} SKUs activos",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.InventoryList) }
          )

          GarageMetricCard(
            title = "Stock Bajo",
            value = "$lowStockCount",
            icon = Icons.Default.Warning,
            iconTint = StatusWaitingPart,
            subtitle = "Reponer pronto",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.InventoryList) }
          )

          GarageMetricCard(
            title = "Sin Stock",
            value = "$outOfStockCount",
            icon = Icons.Default.Error,
            iconTint = GarageRed,
            subtitle = "Agotados",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.InventoryList) }
          )
        }
      }

      // 2. MÓDULOS DEL ÁREA DE REPUESTOS
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "MÓDULOS COMERCIALES Y LOGÍSTICA",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DrillDownItemRow(
            icon = Icons.Default.Inventory,
            title = "Inventario de Repuestos",
            badgeText = "${parts.size} ítems",
            onClick = { onNavigateToScreen(Screen.InventoryList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.PointOfSale,
            title = "Ventas de Mostrador y Pedidos",
            badgeText = "${sales.size} ventas",
            onClick = { onNavigateToScreen(Screen.SalesList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.ShoppingCart,
            title = "Compras Nacionales",
            badgeText = "${purchases.size} compras",
            onClick = { onNavigateToScreen(Screen.PurchasesList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.LocalShipping,
            title = "Importaciones Internacionales",
            badgeText = "$inTransitImports en tránsito",
            onClick = { onNavigateToScreen(Screen.ImportsList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.Factory,
            title = "Proveedores",
            badgeText = "Directorio",
            onClick = { onNavigateToScreen(Screen.SuppliersList) }
          )
        }
      }

      // 3. PRODUCTOS CON STOCK CRÍTICO
      item {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "REPUESTOS EN ALERTA DE STOCK",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      items(parts.filter { it.isLowStock || it.isOutOfStock }) { part ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth(),
          onClick = { onNavigateToScreen(Screen.PartDetail(part.id)) }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(part.name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("N° ${part.partNumber} • Marca: ${part.brand} (${part.location})", color = GarageTextSecondary, fontSize = 12.sp)
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (part.isOutOfStock) GarageRed.copy(alpha = 0.2f) else StatusWaitingPart.copy(alpha = 0.2f)
            ) {
              Text(
                text = if (part.isOutOfStock) "AGOTADO" else "STOCK: ${part.stock}",
                color = if (part.isOutOfStock) GarageRed else StatusWaitingPart,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}
