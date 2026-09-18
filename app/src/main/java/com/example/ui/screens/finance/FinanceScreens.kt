package com.example.ui.screens.finance

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
import com.example.data.model.AccountType
import com.example.data.model.CashMovementType
import com.example.data.model.Expense
import com.example.data.repository.GarageRepository
import com.example.ui.components.AutomotiveBackground
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.DrillDownItemRow
import com.example.ui.components.GarageMetricCard
import com.example.ui.navigation.Screen
import com.example.ui.screens.workshop.formFieldColors
import com.example.ui.theme.*

@Composable
fun FinanceHomeScreen(
  repository: GarageRepository,
  onNavigateToScreen: (Screen) -> Unit
) {
  val cashSession by repository.cashSession.collectAsState()
  val expenses by repository.expenses.collectAsState()
  val accounts by repository.accounts.collectAsState()
  val payments by repository.allPayments.collectAsState()

  val totalExpensesToday = expenses.sumOf { it.amount }
  val totalReceivable = accounts.filter { it.type == AccountType.POR_COBRAR }.sumOf { it.pendingAmount }
  val totalPayable = accounts.filter { it.type == AccountType.POR_PAGAR }.sumOf { it.pendingAmount }

  AutomotiveBackground {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
      // 1. LIVE FINANCIAL METRICS
      item {
        Text(
          text = "BALANCE OPERATIVO DIARIO",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          GarageMetricCard(
            title = "Caja Actual",
            value = "S/ ${String.format("%.2f", cashSession.currentAmount)}",
            icon = Icons.Default.AccountBalanceWallet,
            iconTint = StatusReady,
            subtitle = if (cashSession.isOpen) "Abierta 07:50 am" else "Cerrada",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.Cash) }
          )

          GarageMetricCard(
            title = "Ingresos Hoy",
            value = "S/ ${String.format("%.2f", cashSession.totalIncomes)}",
            icon = Icons.Default.TrendingUp,
            iconTint = StatusReady,
            subtitle = "${payments.size} pagos procesados",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.PaymentsList) }
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          GarageMetricCard(
            title = "Gastos Hoy",
            value = "S/ ${String.format("%.2f", totalExpensesToday)}",
            icon = Icons.Default.TrendingDown,
            iconTint = GarageRed,
            subtitle = "${expenses.size} registros",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.ExpensesList) }
          )

          GarageMetricCard(
            title = "Por Cobrar",
            value = "S/ ${String.format("%.2f", totalReceivable)}",
            icon = Icons.Default.PendingActions,
            iconTint = StatusWaitingPart,
            subtitle = "S/ ${String.format("%.2f", totalPayable)} por pagar",
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToScreen(Screen.AccountsList) }
          )
        }
      }

      // 2. MÓDULOS DEL ÁREA FINANCIERA
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "MÓDULOS FINANCIEROS Y TESORERÍA",
          color = GarageTextSecondary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          DrillDownItemRow(
            icon = Icons.Default.AccountBalanceWallet,
            title = "Caja Diaria y Turnos",
            badgeText = if (cashSession.isOpen) "ABIERTA" else "CERRADA",
            onClick = { onNavigateToScreen(Screen.Cash) }
          )

          DrillDownItemRow(
            icon = Icons.Default.MoneyOff,
            title = "Gastos y Egresos Operativos",
            badgeText = "${expenses.size} registros",
            onClick = { onNavigateToScreen(Screen.ExpensesList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.AccountBalance,
            title = "Cuentas por Cobrar y por Pagar",
            badgeText = "${accounts.size} activas",
            onClick = { onNavigateToScreen(Screen.AccountsList) }
          )

          DrillDownItemRow(
            icon = Icons.Default.ReceiptLong,
            title = "Historial Central de Pagos Recibidos",
            badgeText = "${payments.size} pagos",
            onClick = { onNavigateToScreen(Screen.PaymentsList) }
          )
        }
      }
    }
  }
}

// --- CASH SCREEN ---
@Composable
fun CashScreen(
  repository: GarageRepository
) {
  val cashSession by repository.cashSession.collectAsState()
  val movements by repository.cashMovements.collectAsState()

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
              Text("ESTADO DE CAJA CHICA", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (cashSession.isOpen) StatusReady.copy(alpha = 0.2f) else GarageRed.copy(alpha = 0.2f)
              ) {
                Text(
                  text = if (cashSession.isOpen) "TURNO ACTIVO" else "CAJA CERRADA",
                  color = if (cashSession.isOpen) StatusReady else GarageRed,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("S/ ${String.format("%.2f", cashSession.currentAmount)}", color = GarageTextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text("Apertura: S/ ${String.format("%.2f", cashSession.initialAmount)} a las ${cashSession.openedAt}", color = GarageTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = GarageBorder)
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text("Total Ingresos", color = GarageTextSecondary, fontSize = 12.sp)
                Text("+ S/ ${String.format("%.2f", cashSession.totalIncomes)}", color = StatusReady, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
              Column {
                Text("Total Egresos", color = GarageTextSecondary, fontSize = 12.sp)
                Text("- S/ ${String.format("%.2f", cashSession.totalExpenses)}", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { repository.toggleCashSession() },
              colors = ButtonDefaults.buttonColors(containerColor = if (cashSession.isOpen) GarageRed else StatusReady),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(if (cashSession.isOpen) "CERRAR TURNO DE CAJA" else "ABRIR TURNO DE CAJA", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      item {
        Text("MOVIMIENTOS DE LA SESIÓN ACTUAL (${movements.size})", color = GarageTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      items(movements) { mov ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = GarageCardElevated),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(mov.description, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("${mov.date} • ${mov.method} (${mov.category})", color = GarageTextSecondary, fontSize = 11.sp)
            }
            Text(
              text = "${if (mov.type == CashMovementType.INGRESO) "+" else "-"} S/ ${String.format("%.2f", mov.amount)}",
              color = if (mov.type == CashMovementType.INGRESO) StatusReady else GarageRed,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }
}

// --- EXPENSES ---
@Composable
fun ExpensesListScreen(
  repository: GarageRepository
) {
  val expenses by repository.expenses.collectAsState()
  var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

  expenseToDelete?.let { targetExpense ->
    ConfirmDeleteDialog(
      title = "Eliminar Gasto Operativo",
      message = "Esta acción eliminará el registro del gasto y actualizará el balance financiero. No se puede deshacer.",
      itemName = "${targetExpense.description} (S/ ${String.format("%.2f", targetExpense.amount)})",
      onConfirm = {
        repository.deleteExpense(targetExpense.id)
        expenseToDelete = null
      },
      onDismiss = { expenseToDelete = null }
    )
  }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "REGISTRO DE GASTOS OPERATIVOS (${expenses.size})",
        color = GarageTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(expenses) { exp ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(exp.description, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Categoría: ${exp.category} • Pago: ${exp.method}", color = GarageTextSecondary, fontSize = 12.sp)
                Text("${exp.date} • Ref: ${exp.receiptNumber}", color = GarageTextMuted, fontSize = 11.sp)
              }
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text("- S/ ${String.format("%.2f", exp.amount)}", color = GarageRed, fontWeight = FontWeight.Black, fontSize = 15.sp)
                IconButton(
                  onClick = { expenseToDelete = exp },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar gasto",
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
fun NewExpenseScreen(
  repository: GarageRepository,
  onExpenseSaved: () -> Unit
) {
  var concept by remember { mutableStateOf("") }
  var amount by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Insumos Taller") }
  var paymentMethod by remember { mutableStateOf("Efectivo") }

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text("REGISTRAR GASTO OPERATIVO", color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)

      OutlinedTextField(
        value = concept,
        onValueChange = { concept = it },
        label = { Text("Concepto del Gasto *") },
        placeholder = { Text("Ej: Compra de carbones y desengrasante") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = amount,
        onValueChange = { amount = it },
        label = { Text("Monto (S/) *") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = category,
        onValueChange = { category = it },
        label = { Text("Categoría de Gasto") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      OutlinedTextField(
        value = paymentMethod,
        onValueChange = { paymentMethod = it },
        label = { Text("Medio de Pago") },
        colors = formFieldColors(),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          if (concept.isNotBlank() && amount.isNotBlank()) {
            val amt = amount.toDoubleOrNull() ?: 0.0
            val exp = Expense(
              id = "GAS-00${(10..99).random()}",
              category = category,
              amount = amt,
              description = concept,
              method = paymentMethod,
              date = "17/09/2026",
              receiptNumber = "REC-${(1000..9999).random()}"
            )
            repository.saveExpense(exp)
            onExpenseSaved()
          }
        },
        enabled = concept.isNotBlank() && amount.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = GarageRed),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        Text("GUARDAR GASTO", fontWeight = FontWeight.Bold)
      }
    }
  }
}

// --- ACCOUNTS RECEIVABLE / PAYABLE ---
@Composable
fun AccountsListScreen(
  repository: GarageRepository
) {
  val accounts by repository.accounts.collectAsState()

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "CUENTAS POR COBRAR Y PAGAR (${accounts.size})",
        color = GarageTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        items(accounts) { acc ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (acc.type == AccountType.POR_COBRAR) StatusWaitingPart.copy(alpha = 0.2f) else GarageRed.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = acc.type.label,
                      color = if (acc.type == AccountType.POR_COBRAR) StatusWaitingPart else GarageRed,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(6.dp, 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(acc.operationRef, color = GarageTextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(acc.entityName, color = GarageTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Vence: ${acc.dueDate} • Total: S/ ${acc.totalAmount}", color = GarageTextSecondary, fontSize = 12.sp)
              }

              Column(horizontalAlignment = Alignment.End) {
                Text("Pendiente:", color = GarageTextMuted, fontSize = 11.sp)
                Text(
                  text = "S/ ${String.format("%.2f", acc.pendingAmount)}",
                  color = if (acc.type == AccountType.POR_COBRAR) StatusWaitingPart else GarageRed,
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp
                )
              }
            }
          }
        }
      }
    }
  }
}

// --- PAYMENTS LIST ---
@Composable
fun PaymentsListScreen(
  repository: GarageRepository
) {
  val payments by repository.allPayments.collectAsState()

  AutomotiveBackground {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Text(
        text = "HISTORIAL CENTRAL DE PAGOS RECIBIDOS (${payments.size})",
        color = GarageTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        items(payments) { p ->
          Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GarageCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GarageBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp).fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(p.reference, color = GarageRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${p.method} • ${p.date}", color = GarageTextSecondary, fontSize = 12.sp)
              }
              Text("+ S/ ${String.format("%.2f", p.amount)}", color = StatusReady, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
          }
        }
      }
    }
  }
}
