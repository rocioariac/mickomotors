package com.example.ui.screens.workshop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AutomotiveBackground
import com.example.ui.theme.*

data class WorkshopServiceItem(
  val id: String,
  val name: String,
  val category: String,
  val standardCost: Double,
  val estimatedHours: Double
)

@Composable
fun ServicesScreen() {
  val services = listOf(
    WorkshopServiceItem("SRV-01", "Mantenimiento Preventivo 10k km", "Mantenimiento", 180.0, 2.5),
    WorkshopServiceItem("SRV-02", "Cambio de Pastillas de Freno Delanteras", "Frenos", 90.0, 1.0),
    WorkshopServiceItem("SRV-03", "Rectificación de Discos de Freno", "Frenos", 120.0, 2.0),
    WorkshopServiceItem("SRV-04", "Cambio de Kit de Embrague Completo", "Transmisión", 350.0, 5.0),
    WorkshopServiceItem("SRV-05", "Cambio de Amortiguadores Delanteros", "Suspensión", 160.0, 2.5),
    WorkshopServiceItem("SRV-06", "Alineación y Balanceo Láser 4 Ruedas", "Dirección", 80.0, 1.0),
    WorkshopServiceItem("SRV-07", "Diagnóstico Computarizado OBD2 y Reset", "Electrónica", 70.0, 0.8),
    WorkshopServiceItem("SRV-08", "Limpieza de Inyectores por Ultrasonido", "Motor", 140.0, 2.0),
    WorkshopServiceItem("SRV-09", "Cambio de Bomba de Agua y Refrigerante", "Refrigeración", 180.0, 3.0),
    WorkshopServiceItem("SRV-10", "Reparación y Carga de Aire Acondicionado", "Climatización", 150.0, 1.5)
  )

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "CATÁLOGO DE SERVICIOS Y TARIFAS DE MANO DE OBRA",
        color = GarageRed,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(services) { item ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(item.name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text("Categoría: ${item.category} • Tiempo estimado: ${item.estimatedHours} hrs", color = GarageTextSecondary, fontSize = 12.sp)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text("S/ ${String.format("%.2f", item.standardCost)}", color = GarageRed, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("Tarifa base", color = GarageTextMuted, fontSize = 10.sp)
              }
            }
          }
        }
      }
    }
  }
}
