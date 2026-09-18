package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val notifications by repository.notifications.collectAsState()
  var selectedCategory by remember { mutableStateOf("Todas") }
  val categories = listOf("Todas", "Taller", "Stock", "Finanzas")

  val filteredNotifications = remember(selectedCategory, notifications) {
    if (selectedCategory == "Todas") notifications
    else notifications.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // CATEGORY FILTER TABS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.forEach { cat ->
          val isSelected = selectedCategory == cat
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 12.sp) },
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

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredNotifications) { item ->
          NotificationCard(
            item = item,
            onClick = {
              when (item.targetType) {
                "ORDER" -> onNavigateToScreen(Screen.OrderDetail(item.targetRef))
                "PART" -> onNavigateToScreen(Screen.PartDetail(item.targetRef))
                "IMPORT" -> onNavigateToScreen(Screen.ImportDetail(item.targetRef))
                else -> onNavigateToScreen(Screen.OrdersList)
              }
            }
          )
        }
      }
    }
  }
}

@Composable
private fun NotificationCard(
  item: NotificationItem,
  onClick: () -> Unit
) {
  val icon = when (item.category) {
    "Taller" -> Icons.Default.Build
    "Stock" -> Icons.Default.Inventory2
    "Finanzas" -> Icons.Default.AttachMoney
    else -> Icons.Default.Notifications
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = GarageCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .background(GarageCardElevated, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = GarageRed,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = item.title,
            color = GarageTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = item.date,
            color = GarageTextMuted,
            fontSize = 11.sp
          )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = item.message,
          color = GarageTextSecondary,
          fontSize = 12.sp,
          lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Abrir ${item.targetRef} ›",
          color = GarageRed,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
