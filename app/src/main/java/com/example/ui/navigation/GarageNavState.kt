package com.example.ui.navigation

import androidx.compose.runtime.*

enum class MainTab(val label: String) {
  INICIO("Inicio"),
  TALLER("Taller"),
  REPUESTOS("Repuestos"),
  FINANZAS("Finanzas")
}

sealed class Screen {
  // Main Area Tabs (Level 1)
  data object HomeTab : Screen()
  data object WorkshopTab : Screen()
  data object PartsTab : Screen()
  data object FinanceTab : Screen()

  // Workshop (Level 2 & 3)
  data object OrdersList : Screen()
  data class OrderDetail(val orderId: String) : Screen()
  data object NewOrder : Screen()

  data object ClientsList : Screen()
  data class ClientDetail(val clientId: String) : Screen()
  data object NewClient : Screen()

  data object VehiclesList : Screen()
  data class VehicleDetail(val vehicleId: String) : Screen()
  data object NewVehicle : Screen()

  data object ServicesList : Screen()

  // Parts (Level 2 & 3)
  data object InventoryList : Screen()
  data class PartDetail(val partId: String) : Screen()
  data object NewPart : Screen()

  data object SalesList : Screen()
  data class SaleDetail(val saleId: String) : Screen()
  data object NewSale : Screen()

  data object PurchasesList : Screen()
  data object NewPurchase : Screen()

  data object ImportsList : Screen()
  data class ImportDetail(val importId: String) : Screen()

  data object SuppliersList : Screen()

  // Finance (Level 2 & 3)
  data object Cash : Screen()
  data object ExpensesList : Screen()
  data object NewExpense : Screen()
  data object AccountsList : Screen()
  data object PaymentsList : Screen()

  // Drawer / Global Screens
  data object GlobalSearch : Screen()
  data object Notifications : Screen()
  data object Reports : Screen()
  data object Personnel : Screen()
  data object UsersAdmin : Screen()
  data object Audit : Screen()
  data object Settings : Screen()
  data object Profile : Screen()

  // Auth
  data object Login : Screen()
  data object Register : Screen()
  data object ForgotPassword : Screen()
}

class GarageNavController {
  // Active Main Tab (Level 1)
  var currentTab by mutableStateOf(MainTab.INICIO)
    private set

  // Hierarchical Screen Stack (Level 2 & Level 3)
  // When empty, the app renders the active MainTab screen
  private val screenStack = mutableStateListOf<Screen>()

  val currentScreen: Screen
    get() = screenStack.lastOrNull() ?: when (currentTab) {
      MainTab.INICIO -> Screen.HomeTab
      MainTab.TALLER -> Screen.WorkshopTab
      MainTab.REPUESTOS -> Screen.PartsTab
      MainTab.FINANZAS -> Screen.FinanceTab
    }

  val isAtMainLevel: Boolean
    get() = screenStack.isEmpty()

  // Switch Main Area Tab (Bottom Navigation)
  // RULE: Switching main tabs does NOT accumulate backstack history!
  fun selectTab(tab: MainTab) {
    currentTab = tab
    screenStack.clear()
  }

  // Push a new screen onto the current flow hierarchy
  fun navigateTo(screen: Screen) {
    screenStack.add(screen)
  }

  // Pop exactly 1 level backwards in current flow
  fun popBack(): Boolean {
    if (screenStack.isNotEmpty()) {
      screenStack.removeAt(screenStack.lastIndex)
      return true
    }
    return false
  }

  // Clear backstack and return to root of current tab
  fun popToRoot() {
    screenStack.clear()
  }
}

@Composable
fun rememberGarageNavController(): GarageNavController {
  return remember { GarageNavController() }
}
