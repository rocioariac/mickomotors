package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserSession
import com.example.ui.navigation.MainTab
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@Composable
fun GarageDrawerContent(
  currentUser: UserSession,
  currentTab: MainTab,
  onSelectTab: (MainTab) -> Unit,
  onNavigateToScreen: (Screen) -> Unit,
  onCloseDrawer: () -> Unit,
  onLogout: () -> Unit
) {
  Surface(
    color = GarageBackground,
    modifier = Modifier
      .fillMaxHeight()
      .width(320.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
    ) {
      // DRAWER HEADER
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(GarageCard)
          .padding(20.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(GarageRed),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.DirectionsCar,
                  contentDescription = null,
                  tint = GarageTextPrimary,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "MICKO GARAGE OS",
                  color = GarageTextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "MICKO MOTORS",
                  color = GarageRed,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            IconButton(
              onClick = onCloseDrawer,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar menú",
                tint = GarageTextSecondary,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // User info card inside header
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GarageCardElevated,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(GarageRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = currentUser.name.take(2).uppercase(),
                  color = GarageRed,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = currentUser.name,
                  color = GarageTextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = currentUser.role.label,
                  color = GarageTextSecondary,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      HorizontalDivider(color = GarageBorder, thickness = 1.dp)

      Spacer(modifier = Modifier.height(10.dp))

      // SECTION: ÁREAS PRINCIPALES
      DrawerSectionTitle("ÁREAS PRINCIPALES")

      DrawerItem(
        icon = Icons.Default.Home,
        title = "Inicio",
        isSelected = currentTab == MainTab.INICIO,
        hasChevron = false,
        onClick = {
          onSelectTab(MainTab.INICIO)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.Build,
        title = "Taller",
        isSelected = currentTab == MainTab.TALLER,
        hasChevron = true,
        onClick = {
          onSelectTab(MainTab.TALLER)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.Inventory2,
        title = "Repuestos",
        isSelected = currentTab == MainTab.REPUESTOS,
        hasChevron = true,
        onClick = {
          onSelectTab(MainTab.REPUESTOS)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.AttachMoney,
        title = "Finanzas",
        isSelected = currentTab == MainTab.FINANZAS,
        hasChevron = true,
        onClick = {
          onSelectTab(MainTab.FINANZAS)
          onCloseDrawer()
        }
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = GarageBorder, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
      Spacer(modifier = Modifier.height(8.dp))

      // SECTION: GESTIÓN
      DrawerSectionTitle("GESTIÓN")

      DrawerItem(
        icon = Icons.Default.Assessment,
        title = "Reportes",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.Reports)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.Badge,
        title = "Personal",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.Personnel)
          onCloseDrawer()
        }
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = GarageBorder, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
      Spacer(modifier = Modifier.height(8.dp))

      // SECTION: ADMINISTRACIÓN
      DrawerSectionTitle("ADMINISTRACIÓN")

      DrawerItem(
        icon = Icons.Default.Group,
        title = "Usuarios y permisos",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.UsersAdmin)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.History,
        title = "Auditoría",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.Audit)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.Default.Settings,
        title = "Configuración",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.Settings)
          onCloseDrawer()
        }
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = GarageBorder, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
      Spacer(modifier = Modifier.height(8.dp))

      // SECTION: USUARIO
      DrawerItem(
        icon = Icons.Default.Person,
        title = "Mi perfil",
        hasChevron = true,
        onClick = {
          onNavigateToScreen(Screen.Profile)
          onCloseDrawer()
        }
      )

      DrawerItem(
        icon = Icons.AutoMirrored.Filled.ExitToApp,
        title = "Cerrar sesión",
        iconColor = GarageRed,
        hasChevron = false,
        onClick = {
          onLogout()
          onCloseDrawer()
        }
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun DrawerSectionTitle(title: String) {
  Text(
    text = title,
    color = GarageTextMuted,
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
  )
}

@Composable
private fun DrawerItem(
  icon: ImageVector,
  title: String,
  isSelected: Boolean = false,
  hasChevron: Boolean = false,
  iconColor: androidx.compose.ui.graphics.Color = GarageTextSecondary,
  onClick: () -> Unit
) {
  val background = if (isSelected) GarageCardElevated else androidx.compose.ui.graphics.Color.Transparent
  val textColor = if (isSelected) GarageRed else GarageTextPrimary
  val finalIconColor = if (isSelected) GarageRed else iconColor

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 2.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(background)
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = finalIconColor,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(14.dp))
      Text(
        text = title,
        color = textColor,
        fontSize = 14.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }

    if (hasChevron) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = null,
        tint = GarageTextMuted,
        modifier = Modifier.size(12.dp)
      )
    }
  }
}
