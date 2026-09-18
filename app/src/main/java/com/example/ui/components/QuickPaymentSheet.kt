package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPaymentSheet(
  operationRef: String = "OT-00218",
  entityName: String = "Carlos Mendoza",
  pendingAmount: Double = 350.0,
  onDismiss: () -> Unit,
  onConfirmPayment: (operationRef: String, entityName: String, amount: Double, method: String) -> Unit
) {
  var amountInput by remember { mutableStateOf(pendingAmount.toString()) }
  var selectedMethod by remember { mutableStateOf("Yape") }
  val methods = listOf("Yape", "Plin", "Efectivo", "Transferencia BCP", "Transferencia BBVA", "Tarjeta POS")
  var expandedMethods by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = GarageCard,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 10.dp)
          .width(40.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(GarageBorder)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
      // HEADER WITH ✕
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "REGISTRAR PAGO",
            color = GarageTextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "$operationRef • $entityName",
            color = GarageTextSecondary,
            fontSize = 12.sp
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar ventana temporal",
            tint = GarageTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PENDING AMOUNT CARD
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GarageCardElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Saldo pendiente:",
            color = GarageTextSecondary,
            fontSize = 14.sp
          )
          Text(
            text = "S/ ${String.format("%.2f", pendingAmount)}",
            color = GarageRed,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // AMOUNT INPUT
      Text(
        text = "Monto a abonar (S/):",
        color = GarageTextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))
      OutlinedTextField(
        value = amountInput,
        onValueChange = { amountInput = it },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = GarageCardElevated,
          unfocusedContainerColor = GarageCardElevated,
          focusedBorderColor = GarageRed,
          unfocusedBorderColor = GarageBorder,
          focusedTextColor = GarageTextPrimary,
          unfocusedTextColor = GarageTextPrimary
        ),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      // METHOD SELECTOR
      Text(
        text = "Método de pago:",
        color = GarageTextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(6.dp))

      Box {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = GarageCardElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { expandedMethods = true }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = selectedMethod,
              color = GarageTextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "▼",
              color = GarageTextSecondary,
              fontSize = 12.sp
            )
          }
        }

        DropdownMenu(
          expanded = expandedMethods,
          onDismissRequest = { expandedMethods = false },
          modifier = Modifier.background(GarageCard)
        ) {
          methods.forEach { method ->
            DropdownMenuItem(
              text = { Text(method, color = GarageTextPrimary) },
              onClick = {
                selectedMethod = method
                expandedMethods = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // CONFIRM BUTTON
      Button(
        onClick = {
          val amt = amountInput.toDoubleOrNull() ?: pendingAmount
          onConfirmPayment(operationRef, entityName, amt, selectedMethod)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text(
          text = "CONFIRMAR PAGO",
          color = GarageTextPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
