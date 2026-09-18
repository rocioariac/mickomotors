package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun AutomotiveBackground(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GarageBackground)
  ) {
    // Hardware-accelerated automotive dark carbon texture with ambient vignette
    androidx.compose.foundation.Canvas(
      modifier = Modifier.fillMaxSize()
    ) {
      // 1. Ambient lighting from top
      drawRect(
        brush = Brush.radialGradient(
          colors = listOf(
            GarageCardElevated.copy(alpha = 0.35f),
            GarageBackground.copy(alpha = 0.75f),
            GarageBackground
          ),
          center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, 0f),
          radius = size.maxDimension * 0.75f
        )
      )

      // 2. Subtle technical automotive hatch lines (carbon fiber texture)
      val step = 32.dp.toPx()
      val hatchColor = GarageBorder.copy(alpha = 0.15f)
      var x = -size.height
      while (x < size.width) {
        drawLine(
          color = hatchColor,
          start = androidx.compose.ui.geometry.Offset(x, 0f),
          end = androidx.compose.ui.geometry.Offset(x + size.height, size.height),
          strokeWidth = 1f
        )
        x += step
      }

      // 3. Vignette vertical falloff
      drawRect(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.Transparent,
            GarageBackground.copy(alpha = 0.6f),
            GarageBackground
          )
        )
      )
    }

    content()
  }
}

@Composable
fun GarageMetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  iconTint: Color = GarageRed,
  subtitle: String? = null,
  onClick: (() -> Unit)? = null
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = GarageCard
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = modifier
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title.uppercase(),
          color = GarageTextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(iconTint.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        color = GarageTextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          color = GarageTextMuted,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun OrderStatusBadge(
  status: OrderStatus,
  modifier: Modifier = Modifier
) {
  val (color, label) = when (status) {
    OrderStatus.RECIBIDO -> StatusReceived to "RECIBIDO"
    OrderStatus.DIAGNOSTICO -> StatusDiagnostic to "DIAGNÓSTICO"
    OrderStatus.ESPERANDO_REPUESTO -> StatusWaitingPart to "ESP. REPUESTO"
    OrderStatus.EN_REPARACION -> StatusRepairing to "EN REPARACIÓN"
    OrderStatus.LISTO -> StatusReady to "LISTO"
    OrderStatus.ENTREGADO -> StatusDelivered to "ENTREGADO"
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = color.copy(alpha = 0.18f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
    }
  }
}

@Composable
fun DrillDownItemRow(
  icon: ImageVector,
  title: String,
  badgeText: String? = null,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = GarageCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(GarageCardElevated),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GarageRed,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
          text = title,
          color = GarageTextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (badgeText != null) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GarageCardElevated,
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Text(
              text = badgeText,
              color = GarageTextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = "Ver",
          tint = GarageTextMuted,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}

@Composable
fun GarageSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholderText: String,
  modifier: Modifier = Modifier,
  onSearch: (() -> Unit)? = null
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    placeholder = {
      Text(text = placeholderText, color = GarageTextMuted, fontSize = 14.sp)
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Buscar",
        tint = GarageTextSecondary,
        modifier = Modifier.size(20.dp)
      )
    },
    trailingIcon = {
      if (query.isNotEmpty()) {
        IconButton(onClick = { onQueryChange("") }) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Limpiar",
            tint = GarageTextSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(12.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedContainerColor = GarageCard,
      unfocusedContainerColor = GarageCard,
      focusedBorderColor = GarageRed,
      unfocusedBorderColor = GarageBorder,
      focusedTextColor = GarageTextPrimary,
      unfocusedTextColor = GarageTextPrimary,
      cursorColor = GarageRed
    ),
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
  )
}
