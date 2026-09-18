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
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.GarageSearchBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.workshop.formFieldColors
import com.example.ui.theme.*

@Composable
fun SalesListScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val sales by repository.sales.collectAsState()
  var query by remember { mutableStateOf("") }
  var saleToDelete by remember { mutableStateOf<Sale?>(null) }

  saleToDelete?.let { targetSale ->
    ConfirmDeleteDialog(
      title = "Eliminar Venta",
      message = "Esta acción eliminará el comprobante de venta del registro. Quedará registrada en la auditoría.",
      itemName = "${targetSale.id} - ${targetSale.clientName} (S/ ${String.format("%.2f", targetSale.total)})",
      onConfirm = {
        repository.deleteSale(targetSale.id)
        saleToDelete = null
      },
      onDismiss = { saleToDelete = null }
    )
  }

  val filteredSales = remember(sales, query) {
    if (query.isBlank()) sales
    else sales.filter {
      it.id.contains(query, ignoreCase = true) ||
      it.clientName.contains(query, ignoreCase = true)
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
        placeholderText = "Buscar por N° de venta o cliente..."
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "VENTAS REGISTRADAS (${filteredSales.size})",
        color = GarageTextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredSales) { sale ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToScreen(Screen.SaleDetail(sale.id)) }
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
                  Text(sale.id, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("• ${sale.date}", color = GarageTextMuted, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(sale.clientName, color = GarageTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("${sale.items.size} ítems • Pago: ${sale.paymentMethod}", color = GarageTextSecondary, fontSize = 12.sp)
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "S/ ${String.format("%.2f", sale.total)}",
                  color = StatusReady,
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  IconButton(
                    onClick = { saleToDelete = sale },
                    modifier = Modifier.size(32.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.DeleteOutline,
                      contentDescription = "Eliminar venta",
                      tint = GarageRed.copy(alpha = 0.7f),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Ver venta",
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
fun SaleDetailScreen(
  saleId: String,
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val sales by repository.sales.collectAsState()
  val sale = sales.find { it.id == saleId }
  var showDeleteDialog by remember { mutableStateOf(false) }

  if (sale == null) {
    AutomotiveBackground {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Venta no encontrada: $saleId", color = GarageTextSecondary)
      }
    }
    return
  }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "Eliminar Venta",
      message = "Esta acción eliminará el comprobante de venta del sistema y quedará registrada en la auditoría.",
      itemName = "${sale.id} - ${sale.clientName} (S/ ${String.format("%.2f", sale.total)})",
      onConfirm = {
        repository.deleteSale(sale.id)
        onNavigateToScreen(Screen.SalesList)
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
              Text(sale.id, color = GarageRed, fontSize = 18.sp, fontWeight = FontWeight.Black)
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = StatusReady.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text("VENTA COMPLETADA", color = StatusReady, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(6.dp, 2.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                  onClick = { showDeleteDialog = true },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar venta",
                    tint = GarageRed,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cliente: ${sale.clientName}", color = GarageTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Fecha: ${sale.date}", color = GarageTextSecondary, fontSize = 12.sp)
            Text("Método de pago: ${sale.paymentMethod}", color = GarageTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = GarageBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("TOTAL PAGADO:", color = GarageTextSecondary, fontWeight = FontWeight.Bold)
              Text("S/ ${String.format("%.2f", sale.total)}", color = StatusReady, fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
          }
        }
      }

      item {
        Text("ÍTEMS VENDIDOS (${sale.items.size})", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(sale.items) { item ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(item.partName, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Cant: ${item.quantity} x S/ ${String.format("%.2f", item.unitPrice)}", color = GarageTextSecondary, fontSize = 12.sp)
            }
            Text("S/ ${String.format("%.2f", item.total)}", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }
  }
}

@Composable
fun NewSaleScreen(
  repository: GarageRepository,
  onSaleCreated: (String) -> Unit
) {
  val allParts by repository.parts.collectAsState()
  var clientName by remember { mutableStateOf("") }
  var paymentMethod by remember { mutableStateOf("Yape") }
  val methods = listOf("Yape", "Plin", "Efectivo", "Transferencia BCP", "Tarjeta POS")

  var selectedPart by remember { mutableStateOf(allParts.firstOrNull()) }
  var quantityInput by remember { mutableStateOf("1") }
  var cartItems by remember { mutableStateOf(listOf<SaleItem>()) }

  val total = cartItems.sumOf { it.total }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text("NUEVA VENTA DE MOSTRADOR", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = clientName,
        onValueChange = { clientName = it },
        label = { Text("Nombre del Cliente *") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      // ADD ITEM CONTROLS
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Agregar repuesto al carrito:", color = GarageTextSecondary, fontSize = 12.sp)

          // Selector
          selectedPart?.let { part ->
            Text("${part.name} - Stock: ${part.stock} (S/ ${part.salePrice})", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = quantityInput,
              onValueChange = { quantityInput = it },
              label = { Text("Cantidad") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              colors = formFieldColors(),
              modifier = Modifier.weight(1f)
            )

            Button(
              onClick = {
                val p = selectedPart
                val qty = quantityInput.toIntOrNull() ?: 1
                if (p != null && qty > 0) {
                  val item = SaleItem(
                    partId = p.id,
                    partName = p.name,
                    quantity = qty,
                    unitPrice = p.salePrice,
                    unitCost = p.realCost
                  )
                  cartItems = cartItems + item
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Agregar")
            }
          }
        }
      }

      // CART LIST
      Text("CARRITO (${cartItems.size})", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.weight(1f)
      ) {
        items(cartItems) { item ->
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCardElevated),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(item.partName, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("${item.quantity} x S/ ${String.format("%.2f", item.unitPrice)}", color = GarageTextSecondary, fontSize = 11.sp)
              }
              Text("S/ ${String.format("%.2f", item.total)}", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }

      // TOTAL & COMPLETE SALE
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GarageCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("TOTAL A COBRAR:", color = GarageTextSecondary, fontWeight = FontWeight.Bold)
            Text("S/ ${String.format("%.2f", total)}", color = StatusReady, fontWeight = FontWeight.Black, fontSize = 18.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              if (clientName.isNotBlank() && cartItems.isNotEmpty()) {
                val saleId = "V-00${(130..999).random()}"
                val createdSale = Sale(
                  id = saleId,
                  clientId = "CLI-${(100..999).random()}",
                  clientName = clientName,
                  items = cartItems,
                  paymentMethod = paymentMethod,
                  isPaid = true,
                  date = "17/09/2026"
                )
                repository.createSale(createdSale)
                onSaleCreated(saleId)
              }
            },
            enabled = clientName.isNotBlank() && cartItems.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Text("COMPLETAR Y REGISTRAR VENTA", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
