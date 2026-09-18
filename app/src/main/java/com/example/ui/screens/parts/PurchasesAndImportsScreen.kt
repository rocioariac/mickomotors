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
import com.example.data.model.*
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.workshop.formFieldColors
import com.example.ui.theme.*

// --- PURCHASES ---
@Composable
fun PurchasesListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val purchases by repository.purchases.collectAsState()
  var purchaseToDelete by remember { mutableStateOf<Purchase?>(null) }

  purchaseToDelete?.let { targetPurchase ->
    ConfirmDeleteDialog(
      title = "Eliminar Compra a Proveedor",
      message = "Esta acción eliminará el registro de la compra del sistema. No se puede deshacer.",
      itemName = "${targetPurchase.id} - ${targetPurchase.supplierName} (S/ ${String.format("%.2f", targetPurchase.totalRealCost)})",
      onConfirm = {
        repository.deletePurchase(targetPurchase.id)
        purchaseToDelete = null
      },
      onDismiss = { purchaseToDelete = null }
    )
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "COMPRAS A PROVEEDORES NACIONALES (${purchases.size})",
        color = GarageTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(purchases) { purchase ->
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
                Text(purchase.id, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(purchase.supplierName, color = GarageTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("${purchase.date} • ${purchase.items.size} ítems", color = GarageTextSecondary, fontSize = 12.sp)
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Column(horizontalAlignment = Alignment.End) {
                  Text("S/ ${String.format("%.2f", purchase.totalRealCost)}", color = GarageTextPrimary, fontWeight = FontWeight.Black, fontSize = 15.sp)
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (purchase.status == PurchaseStatus.RECIBIDA) StatusReady.copy(alpha = 0.2f) else StatusWaitingPart.copy(alpha = 0.2f),
                    modifier = Modifier.padding(top = 4.dp)
                  ) {
                    Text(
                      text = purchase.status.label,
                      color = if (purchase.status == PurchaseStatus.RECIBIDA) StatusReady else StatusWaitingPart,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                IconButton(
                  onClick = { purchaseToDelete = purchase },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar compra",
                    tint = GarageRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
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
fun NewPurchaseScreen(
  repository: GarageRepository,
  onPurchaseSaved: () -> Unit
) {
  var supplier by remember { mutableStateOf("") }
  var itemName by remember { mutableStateOf("") }
  var quantity by remember { mutableStateOf("1") }
  var unitCost by remember { mutableStateOf("") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("REGISTRAR COMPRA A PROVEEDOR", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = supplier,
        onValueChange = { supplier = it },
        label = { Text("Proveedor / Distribuidor *") },
        placeholder = { Text("Ej: Distribuidora Automotriz Lima SAC") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = itemName,
        onValueChange = { itemName = it },
        label = { Text("Descripción del Repuesto / Ítem *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = quantity,
          onValueChange = { quantity = it },
          label = { Text("Cantidad") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
          value = unitCost,
          onValueChange = { unitCost = it },
          label = { Text("Costo Unitario (S/) *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = formFieldColors(),
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          if (supplier.isNotBlank() && itemName.isNotBlank()) {
            val q = quantity.toIntOrNull() ?: 1
            val cost = unitCost.toDoubleOrNull() ?: 50.0
            val p = Purchase(
              id = "COM-00${(100..999).random()}",
              supplierId = "PRV-00${(10..99).random()}",
              supplierName = supplier,
              items = listOf(
                PurchaseItem(
                  partId = "REP-${(100..999).random()}",
                  partName = itemName,
                  quantity = q,
                  unitCost = cost
                )
              ),
              status = PurchaseStatus.RECIBIDA,
              date = "17/09/2026"
            )
            repository.savePurchase(p)
            onPurchaseSaved()
          }
        },
        enabled = supplier.isNotBlank() && itemName.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("INGRESAR COMPRA", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// --- IMPORTS ---
@Composable
fun ImportsListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val imports by repository.imports.collectAsState()
  var importToDelete by remember { mutableStateOf<ImportRecord?>(null) }

  importToDelete?.let { targetImport ->
    ConfirmDeleteDialog(
      title = "Eliminar Importación",
      message = "Esta acción eliminará el registro de la importación y su costeo aduanero. No se puede deshacer.",
      itemName = "${targetImport.id} - ${targetImport.description} (${targetImport.trackingNumber})",
      onConfirm = {
        repository.deleteImport(targetImport.id)
        importToDelete = null
      },
      onDismiss = { importToDelete = null }
    )
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "IMPORTACIONES INTERNACIONALES (${imports.size})",
        color = GarageTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(imports) { imp ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.ImportDetail(imp.id)) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(imp.id, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(imp.originCountry, color = GarageTextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(imp.description, color = GarageTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Tracking: ${imp.trackingNumber}", color = GarageTextSecondary, fontSize = 12.sp)
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Column(horizontalAlignment = Alignment.End) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (imp.status) {
                      ImportStatus.EN_TRANSITO -> StatusWaitingPart.copy(alpha = 0.2f)
                      ImportStatus.EN_ADUANA -> StatusDiagnostic.copy(alpha = 0.2f)
                      ImportStatus.RECIBIDO -> StatusReady.copy(alpha = 0.2f)
                      else -> GarageCardElevated
                    }
                  ) {
                    Text(
                      text = imp.status.label,
                      color = when (imp.status) {
                        ImportStatus.EN_TRANSITO -> StatusWaitingPart
                        ImportStatus.EN_ADUANA -> StatusDiagnostic
                        ImportStatus.RECIBIDO -> StatusReady
                        else -> GarageTextSecondary
                      },
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(6.dp, 2.dp)
                    )
                  }
                }
                IconButton(
                  onClick = { importToDelete = imp },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar importación",
                    tint = GarageRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                  contentDescription = null,
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

@Composable
fun ImportDetailScreen(
  importId: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val imports by repository.imports.collectAsState()
  val imp = imports.find { it.id == importId }
  var showDeleteDialog by remember { mutableStateOf(false) }

  if (imp == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Importación no encontrada: $importId", color = GarageTextSecondary)
      }
    }
    return
  }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "Eliminar Importación",
      message = "Esta acción eliminará permanentemente la importación y su costeo aduanero.",
      itemName = "${imp.id} - ${imp.description} (${imp.trackingNumber})",
      onConfirm = {
        repository.deleteImport(imp.id)
        onNavigateToScreen(Screen.ImportsList)
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
              Text(imp.id, color = GarageRed, fontSize = 20.sp, fontWeight = FontWeight.Black)
              IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = "Eliminar importación",
                  tint = GarageRed,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(imp.description, color = GarageTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("País de origen: ${imp.originCountry}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("N° Tracking Internacional: ${imp.trackingNumber}", color = GarageTextSecondary, fontSize = 13.sp)
            Text("Estado aduanero: ${imp.status.label}", color = StatusWaitingPart, fontWeight = FontWeight.Bold, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = GarageBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Costo FOB (USD):", color = GarageTextSecondary)
              Text("$ ${String.format("%.2f", imp.productCostUsd)} USD", color = GarageTextPrimary, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Flete Internacional (USD):", color = GarageTextSecondary)
              Text("$ ${String.format("%.2f", imp.freightUsd)} USD", color = GarageTextSecondary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Aduanas SUNAT (PEN):", color = GarageTextSecondary)
              Text("S/ ${String.format("%.2f", imp.customsPen)}", color = GarageTextSecondary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Flete Local Moquegua (PEN):", color = GarageTextSecondary)
              Text("S/ ${String.format("%.2f", imp.localFreightPen)}", color = GarageTextSecondary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Costo Real Puesto en Taller (PEN):", color = GarageTextSecondary, fontWeight = FontWeight.Bold)
              Text("S/ ${String.format("%.2f", imp.totalCostPen)}", color = GarageRed, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
          }
        }
      }
    }
  }
}

// --- SUPPLIERS ---
@Composable
fun SuppliersListScreen(
  repository: GarageRepository
) {
  val suppliers by repository.suppliers.collectAsState()

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "PROVEEDORES Y DISTRIBUIDORES (${suppliers.size})",
        color = GarageRed,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(suppliers) { sup ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(sup.name, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Contacto: ${sup.contactPerson} • Tel: ${sup.phone}", color = GarageTextSecondary, fontSize = 12.sp)
              Text("RUC: ${sup.ruc} • ${sup.country}", color = GarageTextMuted, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}
