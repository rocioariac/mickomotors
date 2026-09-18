package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AutomotiveBackground
import com.example.ui.navigation.Screen
import com.example.ui.screens.workshop.formFieldColors
import com.example.ui.theme.*

@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit,
  onNavigateToRegister: () -> Unit,
  onNavigateToForgotPassword: () -> Unit
) {
  var email by remember { mutableStateOf("admin@mickomotors.pe") }
  var password by remember { mutableStateOf("••••••••") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(GarageRed),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.DirectionsCar,
          contentDescription = null,
          tint = GarageTextPrimary,
          modifier = Modifier.size(42.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "MICKO GARAGE OS",
        color = GarageTextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )

      Text(
        text = "MICKO MOTORS • SISTEMA OPERATIVO DE TALLER",
        color = GarageRed,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(32.dp))

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text("INICIAR SESIÓN", color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico o Usuario") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Text(
              text = "¿Olvidaste tu contraseña?",
              color = GarageRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Button(
            onClick = onLoginSuccess,
            colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Text("INGRESAR AL SISTEMA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("¿No tienes usuario? ", color = GarageTextSecondary, fontSize = 13.sp)
        Text(
          text = "Registrar nuevo taller",
          color = GarageRed,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          modifier = Modifier.clickable { onNavigateToRegister() }
        )
      }
    }
  }
}

@Composable
fun RegisterScreen(
  onRegisterSuccess: () -> Unit,
  onBackToLogin: () -> Unit
) {
  var workshopName by remember { mutableStateOf("") }
  var adminName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text("REGISTRO DE NUEVA SEDE", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      Text("MICKO GARAGE OS", color = GarageTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = workshopName,
            onValueChange = { workshopName = it },
            label = { Text("Nombre del Taller") },
            placeholder = { Text("Micko Motors") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = adminName,
            onValueChange = { adminName = it },
            label = { Text("Nombre del Administrador") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono de contacto") },
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            colors = formFieldColors(),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = onRegisterSuccess,
            colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Text("CREAR CUENTA Y ENTRAR", fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "← Volver a inicio de sesión",
        color = GarageTextSecondary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        modifier = Modifier.clickable { onBackToLogin() }
      )
    }
  }
}

@Composable
fun ForgotPasswordScreen(
  onBackToLogin: () -> Unit
) {
  var email by remember { mutableStateOf("") }
  var sent by remember { mutableStateOf(false) }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text("RECUPERACIÓN DE CONTRASEÑA", color = GarageRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
      Text("MICKO GARAGE OS", color = GarageTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)

      Spacer(modifier = Modifier.height(20.dp))

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GarageCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (!sent) {
            Text("Ingresa tu correo registrado y te enviaremos las instrucciones de restablecimiento:", color = GarageTextSecondary, fontSize = 13.sp)

            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Correo electrónico") },
              colors = formFieldColors(),
              modifier = Modifier.fillMaxWidth()
            )

            Button(
              onClick = { if (email.isNotBlank()) sent = true },
              colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Text("ENVIAR ENLACE", fontWeight = FontWeight.Bold)
            }
          } else {
            Text("¡Enlace enviado!", color = StatusReady, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Hemos enviado las instrucciones a $email. Por favor revisa tu bandeja de entrada.", color = GarageTextSecondary, fontSize = 13.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "← Volver a inicio de sesión",
        color = GarageRed,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.clickable { onBackToLogin() }
      )
    }
  }
}
