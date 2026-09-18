package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
  onDismiss: () -> Unit,
  onNavigateToScreen: (Screen) -> Unit,
  onOpenQuickPaymentModal: () -> Unit
) {
  var showMoreActions by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = GarageCard,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 10.dp)
          .width(40.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(GarageBorder)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // HEADER WITH ✕
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "NUEVO REGISTRO",
            color = GarageTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "Selecciona la operación que deseas iniciar",
            color = GarageTextSecondary,
            fontSize = 12.sp
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar ventana temporal",
            tint = GarageTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PRIMARY ACTIONS
      QuickActionRow(
        icon = Icons.Default.Build,
        title = "Nueva orden de trabajo",
        subtitle = "Recepción de vehículo y diagnóstico inicial",
        iconColor = GarageRed,
        onClick = {
          onDismiss()
          onNavigateToScreen(Screen.NewOrder)
        }
      )

      QuickActionRow(
        icon = Icons.Default.PointOfSale,
        title = "Nueva venta de repuestos",
        subtitle = "Venta directa de mostrador o pedido",
        iconColor = GarageRed,
        onClick = {
          onDismiss()
          onNavigateToScreen(Screen.NewSale)
        }
      )

      QuickActionRow(
        icon = Icons.Default.MoneyOff,
        title = "Registrar gasto",
        subtitle = "Servicios, herramientas, insumos o fletes",
        iconColor = StatusWaitingPart,
        onClick = {
          onDismiss()
          onNavigateToScreen(Screen.NewExpense)
        }
      )

      QuickActionRow(
        icon = Icons.Default.Payment,
        title = "Registrar pago",
        subtitle = "Abono a orden, venta o cuenta pendiente",
        iconColor = StatusReady,
        onClick = {
          onDismiss()
          onOpenQuickPaymentModal()
        }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // TOGGLE MORE ACTIONS
      HorizontalDivider(color = GarageBorder, thickness = 1.dp)

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showMoreActions = !showMoreActions }
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (showMoreActions) "Menos acciones ▲" else "Más acciones ▼",
          color = GarageRed,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      AnimatedVisibility(visible = showMoreActions) {
        Column {
          QuickActionRow(
            icon = Icons.Default.PersonAdd,
            title = "Nuevo cliente",
            subtitle = "Registrar contacto en la base central",
            iconColor = GarageTextPrimary,
            onClick = {
              onDismiss()
              onNavigateToScreen(Screen.NewClient)
            }
          )

          QuickActionRow(
            icon = Icons.Default.DirectionsCar,
            title = "Nuevo vehículo",
            subtitle = "Ficha técnica, placa y VIN",
            iconColor = GarageTextPrimary,
            onClick = {
              onDismiss()
              onNavigateToScreen(Screen.NewVehicle)
            }
          )

          QuickActionRow(
            icon = Icons.Default.Inventory,
            title = "Nuevo repuesto en catálogo",
            subtitle = "Alta de producto y stock inicial",
            iconColor = GarageTextPrimary,
            onClick = {
              onDismiss()
              onNavigateToScreen(Screen.NewPart)
            }
          )

          QuickActionRow(
            icon = Icons.Default.ShoppingCart,
            title = "Nueva compra a proveedor",
            subtitle = "Entrada de mercadería nacional",
            iconColor = GarageTextPrimary,
            onClick = {
              onDismiss()
              onNavigateToScreen(Screen.NewPurchase)
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun QuickActionRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  iconColor: androidx.compose.ui.graphics.Color,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = GarageCardElevated,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(iconColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(
          text = title,
          color = GarageTextPrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = subtitle,
          color = GarageTextSecondary,
          fontSize = 11.sp
        )
      }
    }
  }
}
