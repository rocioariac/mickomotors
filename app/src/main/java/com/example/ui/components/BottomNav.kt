package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.MainTab
import com.example.ui.theme.*

@Composable
fun GarageBottomNav(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  onPlusClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = GarageCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .height(72.dp)
        .padding(horizontal = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      // 🏠 INICIO
      BottomNavItem(
        label = "Inicio",
        icon = Icons.Default.Home,
        isSelected = currentTab == MainTab.INICIO,
        onClick = { onTabSelected(MainTab.INICIO) },
        modifier = Modifier.weight(1f)
      )

      // 🔧 TALLER
      BottomNavItem(
        label = "Taller",
        icon = Icons.Default.Build,
        isSelected = currentTab == MainTab.TALLER,
        onClick = { onTabSelected(MainTab.TALLER) },
        modifier = Modifier.weight(1f)
      )

      // 🔴 + CENTRAL QUICK ACTION BUTTON
      Box(
        modifier = Modifier
          .weight(1.1f)
          .fillMaxHeight(),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier
            .clickable { onPlusClick() }
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .shadow(8.dp, CircleShape)
              .clip(CircleShape)
              .background(GarageRed),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Nuevo registro rápido",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "Nuevo",
            color = GarageRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 📦 REPUESTOS
      BottomNavItem(
        label = "Repuestos",
        icon = Icons.Default.Inventory2,
        isSelected = currentTab == MainTab.REPUESTOS,
        onClick = { onTabSelected(MainTab.REPUESTOS) },
        modifier = Modifier.weight(1f)
      )

      // 💰 FINANZAS
      BottomNavItem(
        label = "Finanzas",
        icon = Icons.Default.AttachMoney,
        isSelected = currentTab == MainTab.FINANZAS,
        onClick = { onTabSelected(MainTab.FINANZAS) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun BottomNavItem(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val contentColor = if (isSelected) GarageRed else GarageTextSecondary

  Column(
    modifier = modifier
      .fillMaxHeight()
      .clickable { onClick() }
      .padding(vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = contentColor,
      modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      color = contentColor,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
    )
  }
}
