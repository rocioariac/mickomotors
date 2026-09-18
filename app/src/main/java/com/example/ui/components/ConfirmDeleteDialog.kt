package com.example.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ConfirmDeleteDialog(
  title: String = "Confirmar eliminación",
  message: String,
  itemName: String? = null,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(14.dp),
    containerColor = GarageCard,
    icon = {
      Icon(
        imageVector = Icons.Default.DeleteForever,
        contentDescription = null,
        tint = GarageRed
      )
    },
    title = {
      Text(
        text = title,
        color = GarageTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Text(
        text = if (itemName != null) {
          "¿Está seguro de que desea eliminar \"$itemName\"?\n\n$message"
        } else {
          message
        },
        color = GarageTextSecondary,
        fontSize = 14.sp,
        lineHeight = 20.sp
      )
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("ELIMINAR", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = GarageTextSecondary),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder)
      ) {
        Text("CANCELAR")
      }
    }
  )
}
