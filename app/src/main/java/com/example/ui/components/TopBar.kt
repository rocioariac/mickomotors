package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class TopBarType {
  MAIN_AREA,      // ☰ TITLE             🔔 👤
  MODULE_LIST,    // ← TITLE             ＋
  DETAIL,         // ← TITLE             ⋮
  FORM,           // ← TITLE
  MODAL           // TITLE               ✕
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageTopBar(
  title: String,
  type: TopBarType,
  onMenuClick: () -> Unit = {},
  onBackClick: () -> Unit = {},
  onNotificationsClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  onAddClick: (() -> Unit)? = null,
  onMoreClick: (() -> Unit)? = null,
  onCloseClick: (() -> Unit)? = null,
  subtitle: String? = null,
  hasUnreadNotifications: Boolean = true
) {
  Surface(
    color = GarageCard,
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(64.dp)
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // LEFT ACTION
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        when (type) {
          TopBarType.MAIN_AREA -> {
            IconButton(onClick = onMenuClick) {
              Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Abrir menú general",
                tint = GarageTextPrimary
              )
            }
          }
          TopBarType.MODULE_LIST, TopBarType.DETAIL, TopBarType.FORM -> {
            IconButton(onClick = onBackClick) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = GarageTextPrimary
              )
            }
          }
          TopBarType.MODAL -> {
            Spacer(modifier = Modifier.width(12.dp))
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column {
          Text(
            text = title.uppercase(),
            color = GarageTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            maxLines = 1
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              color = GarageTextSecondary,
              fontSize = 11.sp,
              maxLines = 1
            )
          }
        }
      }

      // RIGHT ACTIONS
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        when (type) {
          TopBarType.MAIN_AREA -> {
            Box {
              IconButton(onClick = onNotificationsClick) {
                Icon(
                  imageVector = Icons.Default.Notifications,
                  contentDescription = "Notificaciones",
                  tint = GarageTextPrimary
                )
              }
              if (hasUnreadNotifications) {
                Box(
                  modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .size(8.dp)
                    .background(GarageRed, shape = androidx.compose.foundation.shape.CircleShape)
                    .align(Alignment.TopEnd)
                )
              }
            }

            IconButton(onClick = onProfileClick) {
              Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Mi Perfil",
                tint = GarageTextPrimary
              )
            }
          }

          TopBarType.MODULE_LIST -> {
            if (onAddClick != null) {
              IconButton(onClick = onAddClick) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Crear nuevo",
                  tint = GarageRed
                )
              }
            }
          }

          TopBarType.DETAIL -> {
            if (onMoreClick != null) {
              IconButton(onClick = onMoreClick) {
                Icon(
                  imageVector = Icons.Default.MoreVert,
                  contentDescription = "Acciones adicionales",
                  tint = GarageTextPrimary
                )
              }
            }
          }

          TopBarType.FORM -> {
            // Forms do not have additional top bar buttons, back is on left
          }

          TopBarType.MODAL -> {
            if (onCloseClick != null) {
              IconButton(onClick = onCloseClick) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Cerrar ventana temporal",
                  tint = GarageTextPrimary
                )
              }
            }
          }
        }
      }
    }
  }
}
