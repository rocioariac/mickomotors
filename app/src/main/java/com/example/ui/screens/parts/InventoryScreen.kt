package com.example.ui.screens.parts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Part
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.GarageSearchBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.workshop.formFieldColors
import com.example.ui.theme.*

@Composable
fun InventoryListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val parts by repository.parts.collectAsState()
  var query by remember { mutableStateOf("") }
  var filterOnlyLowStock by remember { mutableStateOf(false) }
  var partToDelete by remember { mutableStateOf<Part?>(null) }

  partToDelete?.let { targetPart ->
    ConfirmDeleteDialog(
      title = "Eliminar Repuesto",
      message = "Esta acción eliminará el repuesto del catálogo de inventario. No se puede deshacer.",
      itemName = "${targetPart.name} (${targetPart.partNumber})",
      onConfirm = {
        repository.deletePart(targetPart.id)
        partToDelete = null
      },
      onDismiss = { partToDelete = null }
    )
  }

  val filteredParts = remember(parts, query, filterOnlyLowStock) {
    parts.filter { part ->
      val matchesStock = if (filterOnlyLowStock) (part.isLowStock || part.isOutOfStock) else true
      val matchesSearch = query.isBlank() ||
        part.name.contains(query, ignoreCase = true) ||
        part.partNumber.contains(query, ignoreCase = true) ||
        part.brand.contains(query, ignoreCase = true) ||
        part.category.contains(query, ignoreCase = true)

      matchesStock && matchesSearch
    }
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      GarageSearchBar(
        query = query,
        onQueryChange = { query = it },
        placeholderText = "Buscar repuesto por nombre, código o marca..."
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = filterOnlyLowStock,
          onClick = { filterOnlyLowStock = !filterOnlyLowStock },
          label = { Text("Solo alertas de stock", fontSize = 12.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GarageRed,
            selectedLabelColor = GarageTextPrimary,
            containerColor = GarageCard,
            labelColor = GarageTextSecondary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = filterOnlyLowStock,
            borderColor = if (filterOnlyLowStock) GarageRed else GarageBorder
          )
        )

        Text(
          text = "${filteredParts.size} ítems",
          color = GarageTextMuted,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredParts) { part ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.PartDetail(part.id)) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = part.name,
                  color = GarageTextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "N°: ${part.partNumber} • Marca: ${part.brand} • Ubic: ${part.location}",
                  color = GarageTextSecondary,
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text(
                    text = "Venta: S/ ${String.format("%.2f", part.salePrice)}",
                    color = GarageRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Costo: S/ ${String.format("%.2f", part.realCost)}",
                    color = GarageTextMuted,
                    fontSize = 12.sp
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = when {
                    part.isOutOfStock -> GarageRed.copy(alpha = 0.2f)
                    part.isLowStock -> StatusWaitingPart.copy(alpha = 0.2f)
                    else -> StatusReady.copy(alpha = 0.2f)
                  }
                ) {
                  Text(
                    text = "${part.stock} un.",
                    color = when {
                      part.isOutOfStock -> GarageRed
                      part.isLowStock -> StatusWaitingPart
                      else -> StatusReady
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  IconButton(
                    onClick = { partToDelete = part },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.DeleteOutline,
                      contentDescription = "Eliminar repuesto",
                      tint = GarageRed.copy(alpha = 0.7f),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Detalle",
                    tint = GarageTextMuted,
                    modifier = Modifier.size(12.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun PartDetailScreen(
  partId: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val parts by repository.parts.collectAsState()
  val part = parts.find { it.id == partId }
  var showAdjustStockDialog by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  if (part == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Repuesto no encontrado: $partId", color = GarageTextSecondary)
      }
    }
    return
  }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "Eliminar Repuesto",
      message = "Esta acción eliminará permanentemente el repuesto del inventario.",
      itemName = "${part.name} (${part.partNumber})",
      onConfirm = {
        repository.deletePart(part.id)
        onNavigateToScreen(Screen.InventoryList)
      },
      onDismiss = { showDeleteDialog = false }
    )
  }

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(part.name, color = GarageTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
              IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Eliminar repuesto",
                  tint = GarageRed,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Número de Parte: ${part.partNumber}", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Categoría: ${part.category} • Marca: ${part.brand}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Ubicación en almacén: ${part.location}", color = GarageTextSecondary, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = GarageBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Precio de Costo", color = GarageTextSecondary, fontSize = 12.sp)
                Text("S/ ${String.format("%.2f", part.realCost)}", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
              Column {
                Text("Precio de Venta", color = GarageTextSecondary, fontSize = 12.sp)
                Text("S/ ${String.format("%.2f", part.salePrice)}", color = GarageRed, fontWeight = FontWeight.Black, fontSize = 18.sp)
              }
              Column {
                Text("Margen Bruto", color = GarageTextSecondary, fontSize = 12.sp)
                val margin = if (part.salePrice > 0) ((part.salePrice - part.realCost) / part.salePrice) * 100 else 0.0
                Text("${String.format("%.1f", margin)}%", color = StatusReady, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STOCK CARD
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GarageCardElevated,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Stock físico actual:", color = GarageTextSecondary, fontSize = 12.sp)
                  Text("${part.stock} unidades", color = GarageTextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                  Text("Mínimo requerido: ${part.minStock} un.", color = GarageTextMuted, fontSize = 11.sp)
                }

                Button(
                  onClick = { showAdjustStockDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Ajustar Stock", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }

      item {
        Text("COMPATIBILIDAD CON VEHÍCULOS", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(part.compatibility) { compat ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCardElevated),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GarageRed, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(compat, color = GarageTextPrimary, fontSize = 13.sp)
          }
        }
      }
    }
  }

  if (showAdjustStockDialog) {
    var adjustmentDelta by remember { mutableStateOf("") }
    var adjustmentReason by remember { mutableStateOf("Conteo físico de inventario") }

    AlertDialog(
      onDismissRequest = { showAdjustStockDialog = false },
      containerColor = GarageCard,
      title = { Text("Ajustar stock de repuesto", color = GarageTextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Stock actual: ${part.stock} un.", color = GarageTextSecondary, fontSize = 13.sp)
          OutlinedTextField(
            value = adjustmentDelta,
            onValueChange = { adjustmentDelta = it },
            label = { Text("Variación (+ para entrada, - para salida)") },
            placeholder = { Text("+5 o -2") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = adjustmentReason,
            onValueChange = { adjustmentReason = it },
            label = { Text("Motivo del ajuste") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val delta = adjustmentDelta.toIntOrNull() ?: 0
            if (delta != 0) {
              repository.adjustPartStock(part.id, delta, adjustmentReason)
              showAdjustStockDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GarageRed)
        ) {
          Text("Guardar Ajuste")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAdjustStockDialog = false }) {
          Text("Cancelar", color = GarageTextSecondary)
        }
      }
    )
  }
}

@Composable
fun NewPartScreen(
  repository: GarageRepository,
  onPartSaved: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var partNumber by remember { mutableStateOf("") }
  var brand by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Motor") }
  var costPrice by remember { mutableStateOf("") }
  var salePrice by remember { mutableStateOf("") }
  var stock by remember { mutableStateOf("5") }
  var minStock by remember { mutableStateOf("2") }
  var location by remember { mutableStateOf("Estante A-1") }
  var compatibleVehicles by remember { mutableStateOf("Toyota / Nissan / Hyundai") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text("NUEVO REPUESTO EN CATÁLOGO", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Descripción o Nombre del Repuesto *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = partNumber,
          onValueChange = { partNumber = it.uppercase() },
          label = { Text("N° de Parte OEM *") },
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = brand,
          onValueChange = { brand = it },
          label = { Text("Marca / Fabricante *") },
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = costPrice,
          onValueChange = { costPrice = it },
          label = { Text("Costo (S/) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = salePrice,
          onValueChange = { salePrice = it },
          label = { Text("Venta (S/) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = stock,
          onValueChange = { stock = it },
          label = { Text("Stock Inicial") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = minStock,
          onValueChange = { minStock = it },
          label = { Text("Mínimo Requerido") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      OutlinedTextField(
        value = location,
        onValueChange = { location = it },
        label = { Text("Ubicación Física en Almacén") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = compatibleVehicles,
        onValueChange = { compatibleVehicles = it },
        label = { Text("Modelos compatibles (separados por coma)") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      Button(
        onClick = {
          if (name.isNotBlank() && partNumber.isNotBlank()) {
            val pCost = costPrice.toDoubleOrNull() ?: 50.0
            val pSale = salePrice.toDoubleOrNull() ?: 90.0
            val newPart = Part(
              id = "REP-00${(20..99).random()}",
              name = name,
              partNumber = partNumber,
              brand = brand.ifBlank { "Genérico" },
              category = category,
              stock = stock.toIntOrNull() ?: 1,
              minStock = minStock.toIntOrNull() ?: 2,
              purchaseCost = pCost,
              realCost = pCost,
              salePrice = pSale,
              location = location,
              compatibility = compatibleVehicles.split(",").map { it.trim() }
            )
            repository.savePart(newPart)
            onPartSaved()
          }
        },
        enabled = name.isNotBlank() && partNumber.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("GUARDAR REPUESTO EN ALMACÉN", fontWeight = FontWeight.Bold)
      }
    }
  }
}
