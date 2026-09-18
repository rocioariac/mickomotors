package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun UnsavedChangesDialog(
  onContinueEditing: () -> Unit,
  onSaveAndExit: () -> Unit,
  onDiscardChanges: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onContinueEditing,
    containerColor = GarageCard,
    title = {
      Text(
        text = "Cambios sin guardar",
        color = GarageTextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Text(
        text = "Tienes modificaciones pendientes en este formulario. ¿Qué deseas hacer antes de salir?",
        color = GarageTextSecondary,
        fontSize = 14.sp
      )
    },
    confirmButton = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Button(
          onClick = onContinueEditing,
          colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("CONTINUAR EDITANDO", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedButton(
          onClick = onSaveAndExit,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = GarageTextPrimary),
          border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("GUARDAR Y SALIR")
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(
          onClick = onDiscardChanges,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Descartar cambios", color = GarageTextMuted)
        }
      }
    }
  )
}
