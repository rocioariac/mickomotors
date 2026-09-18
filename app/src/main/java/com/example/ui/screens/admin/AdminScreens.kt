package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.GarageMetricCard
import com.example.ui.theme.*

// --- REPORTS SCREEN ---
@Composable
fun ReportsScreen(repository: GarageRepository) {
  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      item {
        Text("REPORTES FINANCIEROS Y DE OPERACIONES", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
      }

      item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          GarageMetricCard(title = "Facturación Mes", value = "S/ 38,450", icon = Icons.Default.TrendingUp, iconTint = StatusReady, subtitle = "+14% vs mes anterior", modifier = Modifier.weight(1f))
          GarageMetricCard(title = "Margen Operativo", value = "42.8%", icon = Icons.Default.PieChart, iconTint = StatusReady, subtitle = "Utilidad neta estimada", modifier = Modifier.weight(1f))
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("REPUESTOS MÁS VENDIDOS DEL MES", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            listOf(
              "Filtro de Aceite Mann W712" to "48 unidades vendidas",
              "Aceite Sintético 5W-30 Mobil1" to "36 galones",
              "Pastillas de Freno Brembo P83" to "24 juegos",
              "Bujías Iridium NGK" to "72 unidades"
            ).forEach { (item, qty) ->
              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item, color = GarageTextPrimary, fontSize = 13.sp)
                Text(qty, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("PRODUCTIVIDAD DE TÉCNICOS", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            listOf(
              "Marcos Peña" to "14 órdenes completadas (98% a tiempo)",
              "Raúl Quispe" to "11 órdenes completadas (92% a tiempo)",
              "Carlos Díaz" to "18 ventas de mostrador cerradas"
            ).forEach { (tech, stat) ->
              Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tech, color = GarageTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(stat, color = GarageTextSecondary, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

// --- PERSONNEL SCREEN ---
@Composable
fun PersonnelScreen() {
  val staff = listOf(
    Triple("Marcos Peña", "Mecánico Senior / Jefe de Taller", "Activo • Turno Mañana"),
    Triple("Raúl Quispe", "Técnico Electrónico y Diagnóstico", "Activo • Turno Completo"),
    Triple("Carlos Díaz", "Asesor de Ventas y Repuestos", "Activo • Mostrador"),
    Triple("Ana Beltrán", "Cajera y Facturación", "Activo • Administración")
  )

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text("PLANTEL Y PERSONAL TÉCNICO (${staff.size})", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        items(staff) { (name, role, status) ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(GarageRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Text(name.take(2).uppercase(), color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(role, color = GarageTextSecondary, fontSize = 12.sp)
                Text(status, color = StatusReady, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }
  }
}

// --- USERS & ROLES SCREEN ---
@Composable
fun UsersAdminScreen() {
  val users = listOf(
    Triple("micko_admin", "Micko Motors Admin", UserRole.ADMINISTRADOR),
    Triple("mpena", "Marcos Peña (Técnico)", UserRole.TECNICO),
    Triple("cdiaz", "Carlos Díaz (Ventas)", UserRole.VENDEDOR)
  )

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text("CONTROL DE USUARIOS Y PERMISOS", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(users) { (usr, name, role) ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
              Column {
                Text(name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("@$usr", color = GarageTextMuted, fontSize = 12.sp)
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (role) {
                  UserRole.ADMINISTRADOR -> GarageRed.copy(alpha = 0.2f)
                  UserRole.TECNICO -> StatusRepairing.copy(alpha = 0.2f)
                  UserRole.VENDEDOR -> StatusReady.copy(alpha = 0.2f)
                }
              ) {
                Text(
                  text = role.label,
                  color = when (role) {
                    UserRole.ADMINISTRADOR -> GarageRed
                    UserRole.TECNICO -> StatusRepairing
                    UserRole.VENDEDOR -> StatusReady
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

// --- AUDIT LOG SCREEN ---
@Composable
fun AuditScreen(repository: GarageRepository) {
  val logs by repository.auditLogs.collectAsState()

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text("REGISTRO AUDITORÍA DEL SISTEMA (${logs.size})", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(logs) { log ->
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.action, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(log.date, color = GarageTextMuted, fontSize = 11.sp)
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(log.description, color = GarageTextPrimary, fontSize = 12.sp)
              Spacer(modifier = Modifier.height(2.dp))
              Text("Usuario: ${log.user}", color = GarageTextSecondary, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

// --- SETTINGS SCREEN ---
@Composable
fun SettingsScreen() {
  var pushEnabled by remember { mutableStateOf(true) }
  var autoCloseCash by remember { mutableStateOf(false) }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("CONFIGURACIÓN DE MICKO GARAGE OS", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)

      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Notificaciones en tiempo real", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Alertas de stock, órdenes listas y pagos", color = GarageTextSecondary, fontSize = 12.sp)
            }
            Switch(
              checked = pushEnabled,
              onCheckedChange = { pushEnabled = it },
              colors = SwitchDefaults.colors(checkedThumbColor = GarageTextPrimary, checkedTrackColor = GarageRed)
            )
          }

          HorizontalDivider(color = GarageBorder)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Modo Taller Nocturno", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Automotive Dark + Crimson Red activo", color = GarageTextSecondary, fontSize = 12.sp)
            }
            Switch(
              checked = true,
              onCheckedChange = {},
              enabled = false,
              colors = SwitchDefaults.colors(checkedThumbColor = GarageTextPrimary, checkedTrackColor = GarageRed)
            )
          }

          HorizontalDivider(color = GarageBorder)

          Text("MICKO GARAGE OS Versión 2.4.0 (Build 2026)", color = GarageTextMuted, fontSize = 11.sp)
        }
      }
    }
  }
}

// --- PROFILE SCREEN ---
@Composable
fun ProfileScreen(
  onLogout: () -> Unit
) {
  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(GarageRed.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = GarageRed, modifier = Modifier.size(48.dp))
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Micko Motors Admin", color = GarageTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Text("admin@mickomotors.pe", color = GarageTextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = GarageRed.copy(alpha = 0.2f)
        ) {
          Text("ADMINISTRADOR GENERAL", color = GarageRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }

      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("INFORMACIÓN DE LA SEDE", color = GarageRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Text("Taller Central Micko Motors", color = GarageTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Text("RUC: 20608541290", color = GarageTextSecondary, fontSize = 12.sp)
          Text("Dirección: Av. Separadora Industrial 1450, Lima", color = GarageTextSecondary, fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(containerColor = GarageCardElevated),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("CERRAR SESIÓN", color = GarageRed, fontWeight = FontWeight.Bold)
      }
    }
  }
}
