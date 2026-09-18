package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.repository.GarageRepository
import com.example.ui.components.*
import com.example.ui.navigation.*
import com.example.ui.screens.admin.*
import com.example.ui.screens.auth.*
import com.example.ui.screens.finance.*
import com.example.ui.screens.home.*
import com.example.ui.screens.parts.*
import com.example.ui.screens.workshop.*
import com.example.ui.theme.MickoGarageTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MickoGarageTheme {
        MickoGarageApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MickoGarageApp() {
  val repository = remember { GarageRepository() }
  val navController = rememberGarageNavController()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val currentUser by repository.currentUser.collectAsState()

  // Quick Action Modal Sheet State (Red + central button)
  var showQuickActionSheet by remember { mutableStateOf(false) }

  // Quick Payment Modal Sheet State
  var showQuickPaymentSheet by remember { mutableStateOf(false) }

  // Handle hardware / gesture back button predictably per layer
  BackHandler(enabled = drawerState.isOpen) {
    scope.launch { drawerState.close() }
  }

  BackHandler(enabled = !drawerState.isOpen && showQuickActionSheet) {
    showQuickActionSheet = false
  }

  BackHandler(enabled = !drawerState.isOpen && !showQuickActionSheet && showQuickPaymentSheet) {
    showQuickPaymentSheet = false
  }

  BackHandler(enabled = !drawerState.isOpen && !showQuickActionSheet && !showQuickPaymentSheet && !navController.isAtMainLevel) {
    navController.popBack()
  }

  val currentScreen = navController.currentScreen
  val isAuthScreen = currentScreen is Screen.Login || currentScreen is Screen.Register || currentScreen is Screen.ForgotPassword

  if (isAuthScreen) {
    when (currentScreen) {
      is Screen.Login -> LoginScreen(
        onLoginSuccess = { navController.selectTab(MainTab.INICIO) },
        onNavigateToRegister = { navController.navigateTo(Screen.Register) },
        onNavigateToForgotPassword = { navController.navigateTo(Screen.ForgotPassword) }
      )
      is Screen.Register -> RegisterScreen(
        onRegisterSuccess = { navController.selectTab(MainTab.INICIO) },
        onBackToLogin = { navController.popBack() }
      )
      is Screen.ForgotPassword -> ForgotPasswordScreen(
        onBackToLogin = { navController.popBack() }
      )
      else -> {}
    }
    return
  }

  // TOP BAR TITLE & TYPE RESOLUTION
  val (topBarTitle, topBarType, topBarSubtitle) = remember(currentScreen, navController.currentTab) {
    when (currentScreen) {
      is Screen.HomeTab -> Triple("INICIO", TopBarType.MAIN_AREA, "MICKO MOTORS")
      is Screen.WorkshopTab -> Triple("TALLER", TopBarType.MAIN_AREA, "OPERACIONES ACTIVAS")
      is Screen.PartsTab -> Triple("REPUESTOS", TopBarType.MAIN_AREA, "ALMACÉN Y VENTAS")
      is Screen.FinanceTab -> Triple("FINANZAS", TopBarType.MAIN_AREA, "TESORERÍA Y CAJA")

      // Workshop sub-screens
      is Screen.OrdersList -> Triple("ÓRDENES", TopBarType.MODULE_LIST, "Recepción y reparación")
      is Screen.OrderDetail -> Triple(currentScreen.orderId, TopBarType.DETAIL, "Detalle de orden de trabajo")
      is Screen.NewOrder -> Triple("NUEVA ORDEN", TopBarType.FORM, "Ingreso de vehículo")
      is Screen.ClientsList -> Triple("CLIENTES", TopBarType.MODULE_LIST, "Directorio central")
      is Screen.ClientDetail -> Triple("CLIENTE", TopBarType.DETAIL, "Historial de servicios")
      is Screen.NewClient -> Triple("NUEVO CLIENTE", TopBarType.FORM, "Registro de contacto")
      is Screen.VehiclesList -> Triple("VEHÍCULOS", TopBarType.MODULE_LIST, "Parque automotor")
      is Screen.VehicleDetail -> Triple("VEHÍCULO ${currentScreen.vehicleId}", TopBarType.DETAIL, "Ficha técnica")
      is Screen.NewVehicle -> Triple("NUEVO VEHÍCULO", TopBarType.FORM, "Registro en flota")
      is Screen.ServicesList -> Triple("SERVICIOS", TopBarType.MODULE_LIST, "Tarifario mano de obra")

      // Parts sub-screens
      is Screen.InventoryList -> Triple("INVENTARIO", TopBarType.MODULE_LIST, "Catálogo y existencias")
      is Screen.PartDetail -> Triple("REPUESTO", TopBarType.DETAIL, "Ficha de almacén")
      is Screen.NewPart -> Triple("NUEVO REPUESTO", TopBarType.FORM, "Alta en catálogo")
      is Screen.SalesList -> Triple("VENTAS", TopBarType.MODULE_LIST, "Mostrador y pedidos")
      is Screen.SaleDetail -> Triple(currentScreen.saleId, TopBarType.DETAIL, "Comprobante de venta")
      is Screen.NewSale -> Triple("NUEVA VENTA", TopBarType.FORM, "Facturación rápida")
      is Screen.PurchasesList -> Triple("COMPRAS", TopBarType.MODULE_LIST, "Proveedores nacionales")
      is Screen.NewPurchase -> Triple("NUEVA COMPRA", TopBarType.FORM, "Entrada mercadería")
      is Screen.ImportsList -> Triple("IMPORTACIONES", TopBarType.MODULE_LIST, "Embarques internacionales")
      is Screen.ImportDetail -> Triple(currentScreen.importId, TopBarType.DETAIL, "Tracking y costos")
      is Screen.SuppliersList -> Triple("PROVEEDORES", TopBarType.MODULE_LIST, "Directorio autorizado")

      // Finance sub-screens
      is Screen.Cash -> Triple("CAJA DIARIA", TopBarType.MODULE_LIST, "Turnos y arqueo")
      is Screen.ExpensesList -> Triple("GASTOS", TopBarType.MODULE_LIST, "Egresos operativos")
      is Screen.NewExpense -> Triple("REGISTRAR GASTO", TopBarType.FORM, "Salida de dinero")
      is Screen.AccountsList -> Triple("CUENTAS", TopBarType.MODULE_LIST, "Por cobrar y pagar")
      is Screen.PaymentsList -> Triple("PAGOS", TopBarType.MODULE_LIST, "Historial central")

      // Drawer / Global Screens
      is Screen.GlobalSearch -> Triple("BÚSQUEDA", TopBarType.MODULE_LIST, "Resultados en tiempo real")
      is Screen.Notifications -> Triple("NOTIFICACIONES", TopBarType.MODULE_LIST, "Alertas y avisos")
      is Screen.Reports -> Triple("REPORTES", TopBarType.MODULE_LIST, "Métricas y rendimiento")
      is Screen.Personnel -> Triple("PERSONAL", TopBarType.MODULE_LIST, "Mecánicos y asesores")
      is Screen.UsersAdmin -> Triple("USUARIOS", TopBarType.MODULE_LIST, "Permisos y accesos")
      is Screen.Audit -> Triple("AUDITORÍA", TopBarType.MODULE_LIST, "Bitácora del sistema")
      is Screen.Settings -> Triple("CONFIGURACIÓN", TopBarType.MODULE_LIST, "Ajustes de la sede")
      is Screen.Profile -> Triple("MI PERFIL", TopBarType.MODULE_LIST, "Datos del usuario")

      else -> Triple("MICKO GARAGE OS", TopBarType.MAIN_AREA, null)
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      GarageDrawerContent(
        currentUser = currentUser,
        currentTab = navController.currentTab,
        onSelectTab = { tab ->
          navController.selectTab(tab)
          scope.launch { drawerState.close() }
        },
        onNavigateToScreen = { screen ->
          navController.navigateTo(screen)
          scope.launch { drawerState.close() }
        },
        onCloseDrawer = {
          scope.launch { drawerState.close() }
        },
        onLogout = {
          navController.navigateTo(Screen.Login)
        }
      )
    }
  ) {
    Scaffold(
      topBar = {
        GarageTopBar(
          title = topBarTitle,
          type = topBarType,
          subtitle = topBarSubtitle,
          onMenuClick = {
            scope.launch { drawerState.open() }
          },
          onBackClick = {
            navController.popBack()
          },
          onNotificationsClick = {
            navController.navigateTo(Screen.Notifications)
          },
          onProfileClick = {
            navController.navigateTo(Screen.Profile)
          },
          onAddClick = when (currentScreen) {
            is Screen.OrdersList -> ({ navController.navigateTo(Screen.NewOrder) })
            is Screen.ClientsList -> ({ navController.navigateTo(Screen.NewClient) })
            is Screen.VehiclesList -> ({ navController.navigateTo(Screen.NewVehicle) })
            is Screen.InventoryList -> ({ navController.navigateTo(Screen.NewPart) })
            is Screen.SalesList -> ({ navController.navigateTo(Screen.NewSale) })
            is Screen.PurchasesList -> ({ navController.navigateTo(Screen.NewPurchase) })
            is Screen.ExpensesList -> ({ navController.navigateTo(Screen.NewExpense) })
            else -> null
          }
        )
      },
      bottomBar = {
        // RULE 4: Barra inferior solo aparece en las 4 áreas principales!
        if (navController.isAtMainLevel) {
          GarageBottomNav(
            currentTab = navController.currentTab,
            onTabSelected = { tab ->
              navController.selectTab(tab)
            },
            onPlusClick = {
              showQuickActionSheet = true
            }
          )
        }
      },
      containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // ACTIVE SCREEN RENDERING
        when (currentScreen) {
          // LEVEL 1: MAIN TABS
          is Screen.HomeTab -> HomeScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.WorkshopTab -> WorkshopHomeScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.PartsTab -> PartsHomeScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.FinanceTab -> FinanceHomeScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )

          // WORKSHOP MODULES
          is Screen.OrdersList -> OrdersListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.OrderDetail -> OrderDetailScreen(
            orderId = currentScreen.orderId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewOrder -> NewOrderScreen(
            repository = repository,
            onOrderCreated = { newOrderId ->
              // Direct navigation to newly created order detail
              navController.popBack()
              navController.navigateTo(Screen.OrderDetail(newOrderId))
            },
            onNavigateBack = { navController.popBack() }
          )
          is Screen.ClientsList -> ClientsListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.ClientDetail -> ClientDetailScreen(
            clientId = currentScreen.clientId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewClient -> NewClientScreen(
            repository = repository,
            onClientSaved = { navController.popBack() }
          )
          is Screen.VehiclesList -> VehiclesListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.VehicleDetail -> VehicleDetailScreen(
            plate = currentScreen.vehicleId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewVehicle -> NewVehicleScreen(
            repository = repository,
            onVehicleSaved = { navController.popBack() }
          )
          is Screen.ServicesList -> ServicesScreen()

          // PARTS MODULES
          is Screen.InventoryList -> InventoryListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.PartDetail -> PartDetailScreen(
            partId = currentScreen.partId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewPart -> NewPartScreen(
            repository = repository,
            onPartSaved = { navController.popBack() }
          )
          is Screen.SalesList -> SalesListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.SaleDetail -> SaleDetailScreen(
            saleId = currentScreen.saleId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewSale -> NewSaleScreen(
            repository = repository,
            onSaleCreated = { newSaleId ->
              navController.popBack()
              navController.navigateTo(Screen.SaleDetail(newSaleId))
            }
          )
          is Screen.PurchasesList -> PurchasesListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.NewPurchase -> NewPurchaseScreen(
            repository = repository,
            onPurchaseSaved = { navController.popBack() }
          )
          is Screen.ImportsList -> ImportsListScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.ImportDetail -> ImportDetailScreen(
            importId = currentScreen.importId,
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.SuppliersList -> SuppliersListScreen(repository = repository)

          // FINANCE MODULES
          is Screen.Cash -> CashScreen(repository = repository)
          is Screen.ExpensesList -> ExpensesListScreen(repository = repository)
          is Screen.NewExpense -> NewExpenseScreen(
            repository = repository,
            onExpenseSaved = { navController.popBack() }
          )
          is Screen.AccountsList -> AccountsListScreen(repository = repository)
          is Screen.PaymentsList -> PaymentsListScreen(repository = repository)

          // GLOBAL & ADMIN MODULES
          is Screen.GlobalSearch -> GlobalSearchScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.Notifications -> NotificationsScreen(
            repository = repository,
            onNavigateToScreen = { navController.navigateTo(it) }
          )
          is Screen.Reports -> ReportsScreen(repository = repository)
          is Screen.Personnel -> PersonnelScreen()
          is Screen.UsersAdmin -> UsersAdminScreen()
          is Screen.Audit -> AuditScreen(repository = repository)
          is Screen.Settings -> SettingsScreen()
          is Screen.Profile -> ProfileScreen(
            onLogout = { navController.navigateTo(Screen.Login) }
          )

          else -> {}
        }
      }
    }
  }

  // QUICK ACTION BOTTOM SHEET (+)
  if (showQuickActionSheet) {
    QuickActionBottomSheet(
      onDismiss = { showQuickActionSheet = false },
      onNavigateToScreen = { screen ->
        navController.navigateTo(screen)
      },
      onOpenQuickPaymentModal = {
        showQuickPaymentSheet = true
      }
    )
  }

  // QUICK PAYMENT BOTTOM SHEET
  if (showQuickPaymentSheet) {
    QuickPaymentSheet(
      operationRef = "OT-00218",
      entityName = "Carlos Mendoza",
      pendingAmount = 350.0,
      onDismiss = { showQuickPaymentSheet = false },
      onConfirmPayment = { opRef, name, amt, method ->
        repository.registerPayment(opRef, name, amt, method)
      }
    )
  }
}
