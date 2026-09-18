package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GarageRepository() {

  companion object {
    @Volatile
    private var instance: GarageRepository? = null

    fun getInstance(): GarageRepository {
      return instance ?: synchronized(this) {
        instance ?: GarageRepository().also { instance = it }
      }
    }

    fun currentDateStr(): String {
      return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    fun currentTimeStr(): String {
      return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
  }

  // Alias for payments
  val allPayments: StateFlow<List<CashMovement>> get() = _cashMovements.asStateFlow()

  fun toggleCashSession() {
    if (_cashSession.value.isOpen) {
      closeCashSession()
    } else {
      openCashSession(500.0)
    }
  }

  fun saveExpense(expense: Expense) = registerExpense(expense)

  fun adjustPartStock(partId: String, delta: Int, reason: String) {
    val part = _parts.value.find { it.id == partId } ?: return
    val newStock = (part.stock + delta).coerceAtLeast(0)
    _parts.value = _parts.value.map { if (it.id == partId) it.copy(stock = newStock) else it }
    val mov = InventoryMovement(
      id = "MOV-${System.currentTimeMillis() % 10000}",
      partId = partId,
      partName = part.name,
      type = MovementType.AJUSTE,
      quantity = delta,
      sourceDoc = "AJUSTE MANUAL",
      date = "${currentDateStr()} ${currentTimeStr()}",
      user = _currentUser.value.name,
      notes = reason
    )
    _movements.value = listOf(mov) + _movements.value
    logAudit("AJUSTE DE STOCK", "Repuestos", partId, "Ajuste de $delta unidades en ${part.name}: $reason")
  }

  fun markNotificationAsRead(id: String) {
    _notifications.value = _notifications.value.map {
      if (it.id == id) it.copy(isRead = true) else it
    }
  }

  fun clearAllNotifications() {
    _notifications.value = emptyList()
  }

  // Current logged in session
  private val _currentUser = MutableStateFlow(
    UserSession(
      id = "usr-01",
      name = "Rocío Ariac",
      email = "admin@mickomotors.pe",
      role = UserRole.ADMINISTRADOR
    )
  )
  val currentUser: StateFlow<UserSession> = _currentUser.asStateFlow()

  // Settings
  private val _settings = MutableStateFlow(GarageSettings())
  val settings: StateFlow<GarageSettings> = _settings.asStateFlow()

  // Clients
  private val _clients = MutableStateFlow<List<Client>>(
    listOf(
      Client(
        id = "CLI-001",
        name = "Carlos Mendoza",
        phone = "953 234 890",
        whatsapp = "51953234890",
        email = "carlos.mendoza@gmail.com",
        dniRuc = "42890123",
        address = "Calle Lima 420, Moquegua",
        notes = "Cliente frecuente. Vehículos corporativos y particulares.",
        totalDebt = 350.0
      ),
      Client(
        id = "CLI-002",
        name = "Transportes del Sur S.A.C.",
        phone = "952 876 112",
        whatsapp = "51952876112",
        email = "operaciones@transur.pe",
        dniRuc = "20543981245",
        address = "Parque Industrial Mz. B Lt. 4, Moquegua",
        notes = "Flota Hilux y D-Max.",
        totalDebt = 900.0
      ),
      Client(
        id = "CLI-003",
        name = "Mariana Valdivia",
        phone = "958 112 344",
        whatsapp = "51958112344",
        email = "m.valdivia@outlook.com",
        dniRuc = "70891234",
        address = "Urb. El Naranjal C-12, Moquegua",
        totalDebt = 0.0
      )
    )
  )
  val clients: StateFlow<List<Client>> = _clients.asStateFlow()

  // Vehicles
  private val _vehicles = MutableStateFlow<List<Vehicle>>(
    listOf(
      Vehicle(
        id = "VEH-001",
        plate = "V8X-321",
        brand = "Jeep",
        model = "Grand Cherokee Laredo",
        year = 2016,
        vin = "1C4RJFBG2GC189204",
        engine = "3.6L V6 Pentastar",
        transmission = "Automática 8 vel.",
        fuel = "Gasolina 95",
        mileageKm = 98450,
        ownerClientId = "CLI-001",
        ownerName = "Carlos Mendoza"
      ),
      Vehicle(
        id = "VEH-002",
        plate = "B1A-852",
        brand = "Toyota",
        model = "Hilux 4x4",
        year = 2021,
        vin = "8AJBA3CD4M1209384",
        engine = "2.8L D-4D Turbo Diésel",
        transmission = "Manual 6 vel.",
        fuel = "Diésel B5 S-50",
        mileageKm = 64200,
        ownerClientId = "CLI-002",
        ownerName = "Transportes del Sur S.A.C."
      ),
      Vehicle(
        id = "VEH-003",
        plate = "Z4D-610",
        brand = "Hyundai",
        model = "Tucson GL",
        year = 2019,
        vin = "KMHJT81CBDU294810",
        engine = "2.0L MPI",
        transmission = "Automática",
        fuel = "Gasolina 95",
        mileageKm = 52100,
        ownerClientId = "CLI-003",
        ownerName = "Mariana Valdivia"
      )
    )
  )
  val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

  // Parts / Inventory
  private val _parts = MutableStateFlow<List<Part>>(
    listOf(
      Part(
        id = "REP-001",
        name = "Amortiguador Delantero KYB",
        partNumber = "339081",
        brand = "KYB Excel-G",
        category = "Suspensión",
        stock = 4,
        minStock = 2,
        purchaseCost = 210.0,
        realCost = 245.0,
        salePrice = 360.0,
        location = "A-12",
        compatibility = listOf("Jeep Grand Cherokee 2011-2020", "Dodge Durango 2011-2020")
      ),
      Part(
        id = "REP-002",
        name = "Pastillas de Freno Cerámicas Brembo",
        partNumber = "P83085N",
        brand = "Brembo",
        category = "Frenos",
        stock = 8,
        minStock = 3,
        purchaseCost = 110.0,
        realCost = 125.0,
        salePrice = 195.0,
        location = "B-04",
        compatibility = listOf("Toyota Hilux 2016+", "Toyota Fortuner 2016+")
      ),
      Part(
        id = "REP-003",
        name = "Filtro de Aceite Sintético K&N",
        partNumber = "HP-1010",
        brand = "K&N",
        category = "Filtros",
        stock = 14,
        minStock = 5,
        purchaseCost = 35.0,
        realCost = 42.0,
        salePrice = 75.0,
        location = "C-01",
        compatibility = listOf("Universal rosca M20x1.5", "Hyundai Tucson", "Kia Sportage")
      ),
      Part(
        id = "REP-004",
        name = "Aceite Motul 8100 X-cess 5W40 (5L)",
        partNumber = "MOT-102784",
        brand = "Motul",
        category = "Lubricantes",
        stock = 6,
        minStock = 4,
        purchaseCost = 140.0,
        realCost = 158.0,
        salePrice = 240.0,
        location = "LUB-01",
        compatibility = listOf("Sintético Alto Desempeño Gasolina / Diésel")
      ),
      Part(
        id = "REP-005",
        name = "Bomba de Agua GMB",
        partNumber = "GWIS-42A",
        brand = "GMB Japan",
        category = "Motor",
        stock = 1, // Low stock warning
        minStock = 2,
        purchaseCost = 160.0,
        realCost = 185.0,
        salePrice = 280.0,
        location = "A-03",
        compatibility = listOf("Isuzu D-Max 3.0", "Chevrolet D-Max 3.0")
      ),
      Part(
        id = "REP-006",
        name = "Kit de Embrague Exedy",
        partNumber = "TYK2238",
        brand = "Exedy Japan",
        category = "Transmisión",
        stock = 0, // Out of stock warning
        minStock = 2,
        purchaseCost = 520.0,
        realCost = 590.0,
        salePrice = 850.0,
        location = "T-08",
        compatibility = listOf("Toyota Hilux 1KD/2KD 2005-2015")
      )
    )
  )
  val parts: StateFlow<List<Part>> = _parts.asStateFlow()

  // Inventory Movements
  private val _movements = MutableStateFlow<List<InventoryMovement>>(
    listOf(
      InventoryMovement(
        id = "MOV-001",
        partId = "REP-001",
        partName = "Amortiguador Delantero KYB",
        type = MovementType.ORDEN,
        quantity = -2,
        sourceDoc = "OT-00218",
        date = "17/09/2026 08:42",
        user = "Rocío Ariac",
        notes = "Instalado en Jeep Grand Cherokee V8X-321"
      ),
      InventoryMovement(
        id = "MOV-002",
        partId = "REP-002",
        partName = "Pastillas de Freno Cerámicas Brembo",
        type = MovementType.VENTA,
        quantity = -1,
        sourceDoc = "V-00124",
        date = "17/09/2026 08:30",
        user = "Rocío Ariac",
        notes = "Venta directa mostrador"
      ),
      InventoryMovement(
        id = "MOV-003",
        partId = "REP-003",
        partName = "Filtro de Aceite Sintético K&N",
        type = MovementType.ENTRADA,
        quantity = 10,
        sourceDoc = "COM-0041",
        date = "15/09/2026 14:10",
        user = "Rocío Ariac",
        notes = "Ingreso por compra nacional"
      )
    )
  )
  val movements: StateFlow<List<InventoryMovement>> = _movements.asStateFlow()

  // Work Orders
  private val _workOrders = MutableStateFlow<List<WorkOrder>>(
    listOf(
      WorkOrder(
        id = "OT-00218",
        clientId = "CLI-001",
        clientName = "Carlos Mendoza",
        clientPhone = "953 234 890",
        vehicleId = "VEH-001",
        vehiclePlate = "V8X-321",
        vehicleModel = "Jeep Grand Cherokee 2016",
        mileageKm = 98450,
        status = OrderStatus.EN_REPARACION,
        technician = "Juan Quispe (Mecánico Senior)",
        reportedProblem = "Ruido seco y golpeteo metálico en suspensión delantera derecha al pasar baches. Desgaste irregular de neumático.",
        diagnostic = "Amortiguadores delanteros con fuga de gas hidráulico y bujes de trapecio fatigados. Requiere cambio de amortiguadores y alineación.",
        laborCost = 150.0,
        services = listOf(
          WorkOrderService("SRV-01", "Desmontaje e instalación de amortiguadores delanteros", 120.0),
          WorkOrderService("SRV-02", "Alineación láser computarizada y balanceo 3D", 80.0)
        ),
        parts = listOf(
          WorkOrderPart(
            id = "WOP-01",
            partId = "REP-001",
            name = "Amortiguador Delantero KYB",
            partNumber = "339081",
            quantity = 2,
            unitPrice = 360.0
          )
        ),
        payments = listOf(
          OrderPayment(
            id = "PAY-01",
            amount = 720.0,
            method = "Transferencia BCP",
            date = "17/09/2026 08:45",
            reference = "Op-491823"
          )
        ),
        createdAt = "17/09/2026 08:15",
        updatedAt = "17/09/2026 08:45"
      ),
      WorkOrder(
        id = "OT-00217",
        clientId = "CLI-002",
        clientName = "Transportes del Sur S.A.C.",
        clientPhone = "952 876 112",
        vehicleId = "VEH-002",
        vehiclePlate = "B1A-852",
        vehicleModel = "Toyota Hilux 2021",
        mileageKm = 64200,
        status = OrderStatus.LISTO,
        technician = "Marcos Delgado (Técnico Frenos)",
        reportedProblem = "Mantenimiento preventivo de frenos 60,000 km y chirrido al frenar a baja velocidad.",
        diagnostic = "Pastillas delanteras al 15% de vida útil. Discos rectificados.",
        laborCost = 100.0,
        services = listOf(
          WorkOrderService("SRV-03", "Rectificación de discos de freno y purgado", 110.0)
        ),
        parts = listOf(
          WorkOrderPart(
            id = "WOP-02",
            partId = "REP-002",
            name = "Pastillas de Freno Cerámicas Brembo",
            partNumber = "P83085N",
            quantity = 1,
            unitPrice = 195.0
          )
        ),
        payments = listOf(
          OrderPayment(
            id = "PAY-02",
            amount = 405.0,
            method = "Yape",
            date = "17/09/2026 08:20",
            reference = "Yape-9182"
          )
        ),
        createdAt = "16/09/2026 15:30",
        updatedAt = "17/09/2026 08:20"
      ),
      WorkOrder(
        id = "OT-00216",
        clientId = "CLI-003",
        clientName = "Mariana Valdivia",
        clientPhone = "958 112 344",
        vehicleId = "VEH-003",
        vehiclePlate = "Z4D-610",
        vehicleModel = "Hyundai Tucson 2019",
        mileageKm = 52100,
        status = OrderStatus.ESPERANDO_REPUESTO,
        technician = "Juan Quispe",
        reportedProblem = "Pérdida de refrigerante y temperatura sube en pendientes.",
        diagnostic = "Bomba de agua con juego axial y goteo por sello mecánico.",
        laborCost = 140.0,
        services = listOf(
          WorkOrderService("SRV-04", "Cambio de bomba de agua y purga de sistema", 140.0)
        ),
        parts = emptyList(),
        payments = emptyList(),
        createdAt = "16/09/2026 11:00",
        updatedAt = "16/09/2026 11:00"
      )
    )
  )
  val workOrders: StateFlow<List<WorkOrder>> = _workOrders.asStateFlow()

  // Sales
  private val _sales = MutableStateFlow<List<Sale>>(
    listOf(
      Sale(
        id = "V-00124",
        clientId = "CLI-001",
        clientName = "Carlos Mendoza",
        items = listOf(
          SaleItem(
            partId = "REP-002",
            partName = "Pastillas de Freno Cerámicas Brembo",
            quantity = 1,
            unitPrice = 195.0,
            unitCost = 125.0
          )
        ),
        discount = 0.0,
        paymentMethod = "Yape",
        isPaid = true,
        date = "17/09/2026 08:30"
      ),
      Sale(
        id = "V-00123",
        clientId = "CLI-002",
        clientName = "Transportes del Sur S.A.C.",
        items = listOf(
          SaleItem(
            partId = "REP-004",
            partName = "Aceite Motul 8100 X-cess 5W40 (5L)",
            quantity = 2,
            unitPrice = 240.0,
            unitCost = 158.0
          ),
          SaleItem(
            partId = "REP-003",
            partName = "Filtro de Aceite Sintético K&N",
            quantity = 2,
            unitPrice = 75.0,
            unitCost = 42.0
          )
        ),
        discount = 30.0,
        paymentMethod = "Transferencia BBVA",
        isPaid = true,
        date = "16/09/2026 16:45"
      )
    )
  )
  val sales: StateFlow<List<Sale>> = _sales.asStateFlow()

  // Purchases
  private val _purchases = MutableStateFlow<List<Purchase>>(
    listOf(
      Purchase(
        id = "COM-0042",
        supplierId = "PRV-001",
        supplierName = "Importadora Autopartes Lima S.A.C.",
        items = listOf(
          PurchaseItem("REP-001", "Amortiguador Delantero KYB", 4, 210.0),
          PurchaseItem("REP-002", "Pastillas de Freno Brembo", 6, 110.0)
        ),
        freightCost = 90.0,
        courierCost = 25.0,
        customsCost = 0.0,
        taxesCost = 0.0,
        status = PurchaseStatus.RECIBIDA,
        date = "15/09/2026"
      ),
      Purchase(
        id = "COM-0043",
        supplierId = "PRV-002",
        supplierName = "Distribuidora Motul Perú",
        items = listOf(
          PurchaseItem("REP-004", "Aceite Motul 8100 X-cess 5W40 (5L)", 10, 140.0)
        ),
        freightCost = 60.0,
        status = PurchaseStatus.PENDIENTE,
        date = "16/09/2026"
      )
    )
  )
  val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()

  // Imports
  private val _imports = MutableStateFlow<List<ImportRecord>>(
    listOf(
      ImportRecord(
        id = "IMP-0034",
        description = "Amortiguadores KYB y Sensores Bosch Originales",
        originCountry = "Estados Unidos (Miami)",
        trackingNumber = "DHL-7821940214",
        status = ImportStatus.EN_TRANSITO,
        itemsCount = 12,
        productCostUsd = 1450.0,
        freightUsd = 210.0,
        customsPen = 420.0,
        localFreightPen = 95.0,
        exchangeRate = 3.75,
        date = "12/09/2026"
      ),
      ImportRecord(
        id = "IMP-0033",
        description = "Kits de Embrague Exedy Japón",
        originCountry = "Japón (Yokohama)",
        trackingNumber = "FEDEX-901840192",
        status = ImportStatus.RECIBIDO,
        itemsCount = 6,
        productCostUsd = 2200.0,
        freightUsd = 340.0,
        customsPen = 680.0,
        localFreightPen = 120.0,
        exchangeRate = 3.75,
        date = "28/08/2026"
      )
    )
  )
  val imports: StateFlow<List<ImportRecord>> = _imports.asStateFlow()

  // Suppliers
  private val _suppliers = MutableStateFlow<List<Supplier>>(
    listOf(
      Supplier(
        id = "PRV-001",
        name = "Importadora Autopartes Lima S.A.C.",
        contactPerson = "Eduardo Chang",
        phone = "01 428 9012",
        email = "ventas@autoparteslima.pe",
        ruc = "20489012389",
        country = "Perú",
        debtPending = 1500.0
      ),
      Supplier(
        id = "PRV-002",
        name = "Distribuidora Motul Perú",
        contactPerson = "Verónica Rios",
        phone = "01 614 7780",
        email = "pedidos@motulperu.com",
        ruc = "20512894012",
        country = "Perú",
        debtPending = 1460.0
      ),
      Supplier(
        id = "PRV-003",
        name = "RockAuto LLC Global Export",
        contactPerson = "Support Team",
        phone = "+1 608 661 1376",
        email = "service@rockauto.com",
        ruc = "US-91823901",
        country = "Estados Unidos",
        debtPending = 0.0
      )
    )
  )
  val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

  // Cash Session & Movements
  private val _cashSession = MutableStateFlow(
    CashSession(
      id = "CAJA-20260917",
      isOpen = true,
      openedAt = "17/09/2026 07:50",
      initialAmount = 500.0,
      currentAmount = 2170.0,
      totalIncomes = 1970.0,
      totalExpenses = 300.0
    )
  )
  val cashSession: StateFlow<CashSession> = _cashSession.asStateFlow()

  private val _cashMovements = MutableStateFlow<List<CashMovement>>(
    listOf(
      CashMovement(
        id = "CM-01",
        type = CashMovementType.INGRESO,
        category = "Cobro Orden de Trabajo",
        amount = 720.0,
        reference = "OT-00218",
        method = "Transferencia BCP",
        date = "17/09/2026 08:45",
        description = "Anticipo reparación Jeep Grand Cherokee"
      ),
      CashMovement(
        id = "CM-02",
        type = CashMovementType.INGRESO,
        category = "Venta de Repuesto",
        amount = 195.0,
        reference = "V-00124",
        method = "Yape",
        date = "17/09/2026 08:30",
        description = "Pastillas Brembo mostrador"
      ),
      CashMovement(
        id = "CM-03",
        type = CashMovementType.INGRESO,
        category = "Cobro Orden de Trabajo",
        amount = 405.0,
        reference = "OT-00217",
        method = "Yape",
        date = "17/09/2026 08:20",
        description = "Liquidación frenos Hilux B1A-852"
      ),
      CashMovement(
        id = "CM-04",
        type = CashMovementType.EGRESO,
        category = "Servicios Básicos / Taller",
        amount = 300.0,
        reference = "GAS-001",
        method = "Efectivo",
        date = "17/09/2026 08:05",
        description = "Pago servicio de luz y energía trifásica taller"
      ),
      CashMovement(
        id = "CM-05",
        type = CashMovementType.INGRESO,
        category = "Apertura de Caja",
        amount = 500.0,
        reference = "APERTURA",
        method = "Efectivo",
        date = "17/09/2026 07:50",
        description = "Fondo inicial de sencillo en caja chica"
      )
    )
  )
  val cashMovements: StateFlow<List<CashMovement>> = _cashMovements.asStateFlow()

  // Expenses
  private val _expenses = MutableStateFlow<List<Expense>>(
    listOf(
      Expense(
        id = "GAS-001",
        category = "Servicios Básicos",
        amount = 300.0,
        description = "Luz trifásica Electro Sur Este taller",
        method = "Efectivo",
        date = "17/09/2026 08:05",
        receiptNumber = "REC-89102"
      ),
      Expense(
        id = "GAS-002",
        category = "Herramientas e Insumos",
        amount = 180.0,
        description = "Grasas para rodamientos y desengrasante industrial",
        method = "Yape",
        date = "16/09/2026 17:15",
        receiptNumber = "BOL-19203"
      ),
      Expense(
        id = "GAS-003",
        category = "Logística y Courier",
        amount = 95.0,
        description = "Flete encomienda Olva Courier Lima-Moquegua",
        method = "Efectivo",
        date = "15/09/2026 12:40",
        receiptNumber = "GR-49102"
      )
    )
  )
  val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

  // Accounts
  private val _accounts = MutableStateFlow<List<AccountRecord>>(
    listOf(
      AccountRecord(
        id = "CXC-01",
        type = AccountType.POR_COBRAR,
        entityName = "Carlos Mendoza",
        entityId = "CLI-001",
        operationRef = "OT-00218",
        totalAmount = 1070.0,
        paidAmount = 720.0,
        date = "17/09/2026",
        dueDate = "20/09/2026"
      ),
      AccountRecord(
        id = "CXC-02",
        type = AccountType.POR_COBRAR,
        entityName = "Transportes del Sur S.A.C.",
        entityId = "CLI-002",
        operationRef = "V-00123",
        totalAmount = 570.0,
        paidAmount = 0.0,
        date = "16/09/2026",
        dueDate = "23/09/2026"
      ),
      AccountRecord(
        id = "CXP-01",
        type = AccountType.POR_PAGAR,
        entityName = "Importadora Autopartes Lima S.A.C.",
        entityId = "PRV-001",
        operationRef = "COM-0042",
        totalAmount = 1615.0,
        paidAmount = 115.0,
        date = "15/09/2026",
        dueDate = "25/09/2026"
      ),
      AccountRecord(
        id = "CXP-02",
        type = AccountType.POR_PAGAR,
        entityName = "Distribuidora Motul Perú",
        entityId = "PRV-002",
        operationRef = "COM-0043",
        totalAmount = 1460.0,
        paidAmount = 0.0,
        date = "16/09/2026",
        dueDate = "30/09/2026"
      )
    )
  )
  val accounts: StateFlow<List<AccountRecord>> = _accounts.asStateFlow()

  // Audit Logs
  private val _auditLogs = MutableStateFlow<List<AuditLog>>(
    listOf(
      AuditLog(
        id = "AUD-01",
        user = "Rocío Ariac",
        action = "ORDEN REGISTRADA",
        module = "Taller",
        recordRef = "OT-00218",
        date = "17/09/2026 08:42",
        description = "Recepción y diagnóstico Jeep Grand Cherokee V8X-321 (Carlos Mendoza)"
      ),
      AuditLog(
        id = "AUD-02",
        user = "Rocío Ariac",
        action = "VENTA REGISTRADA",
        module = "Repuestos",
        recordRef = "V-00124",
        date = "17/09/2026 08:30",
        description = "Venta mostrador pastillas Brembo P83085N por S/ 195.00"
      ),
      AuditLog(
        id = "AUD-03",
        user = "Rocío Ariac",
        action = "PAGO RECIBIDO",
        module = "Finanzas",
        recordRef = "OT-00217",
        date = "17/09/2026 08:20",
        description = "Cobro total de mantenimiento Toyota Hilux B1A-852 por S/ 405.00"
      ),
      AuditLog(
        id = "AUD-04",
        user = "Rocío Ariac",
        action = "CAJA ABIERTA",
        module = "Finanzas",
        recordRef = "CAJA-20260917",
        date = "17/09/2026 07:50",
        description = "Apertura de turno con S/ 500.00 en efectivo"
      )
    )
  )
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<NotificationItem>>(
    listOf(
      NotificationItem(
        id = "NOT-01",
        title = "Vehículo listo para entrega",
        message = "La orden OT-00217 de Toyota Hilux (B1A-852) fue finalizada y está lista para entrega.",
        category = "Taller",
        targetRef = "OT-00217",
        targetType = "ORDER",
        date = "Hoy 08:20"
      ),
      NotificationItem(
        id = "NOT-02",
        title = "Alerta de Stock Crítico",
        message = "Bomba de Agua GMB (GWIS-42A) tiene solo 1 unidad restante en almacén.",
        category = "Stock",
        targetRef = "REP-005",
        targetType = "PART",
        date = "Hoy 07:55"
      ),
      NotificationItem(
        id = "NOT-03",
        title = "Vehículo esperando repuesto",
        message = "OT-00216 Hyundai Tucson (Z4D-610) requiere aprobación de repuesto bomba de agua.",
        category = "Taller",
        targetRef = "OT-00216",
        targetType = "ORDER",
        date = "Ayer 18:30"
      ),
      NotificationItem(
        id = "NOT-04",
        title = "Importación en Aduana",
        message = "Cargamento IMP-0034 de Miami pasó a revisión en Aduana Marítima.",
        category = "Stock",
        targetRef = "IMP-0034",
        targetType = "IMPORT",
        date = "Ayer 15:40"
      ),
      NotificationItem(
        id = "NOT-05",
        title = "Cuenta por cobrar próxima",
        message = "Carlos Mendoza mantiene saldo pendiente de S/ 350.00 por OT-00218.",
        category = "Finanzas",
        targetRef = "OT-00218",
        targetType = "ORDER",
        date = "Hoy 08:45"
      )
    )
  )
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  // Personnel
  private val _personnel = MutableStateFlow<List<Employee>>(
    listOf(
      Employee("EMP-01", "Juan Quispe", "Mecánico Senior", "953 441 209", 2, "Activo"),
      Employee("EMP-02", "Marcos Delgado", "Técnico Especialista Frenos", "952 119 402", 1, "Activo"),
      Employee("EMP-03", "Renzo Apaza", "Asesor de Repuestos y Ventas", "958 332 890", 0, "Activo"),
      Employee("EMP-04", "Rocío Ariac", "Jefa de Taller y Administradora", "953 912 845", 0, "Activo")
    )
  )
  val personnel: StateFlow<List<Employee>> = _personnel.asStateFlow()

  // Users for Administration
  private val _users = MutableStateFlow<List<UserSession>>(
    listOf(
      UserSession("usr-01", "Rocío Ariac", "admin@mickomotors.pe", UserRole.ADMINISTRADOR),
      UserSession("usr-02", "Juan Quispe", "mecanica@mickomotors.pe", UserRole.TECNICO),
      UserSession("usr-03", "Renzo Apaza", "ventas@mickomotors.pe", UserRole.VENDEDOR)
    )
  )
  val users: StateFlow<List<UserSession>> = _users.asStateFlow()

  // ---------------------------------------------------------------------------
  // INTERCONNECTED MUTATIONS
  // ---------------------------------------------------------------------------

  fun switchUserRole(role: UserRole) {
    _currentUser.value = _currentUser.value.copy(role = role)
  }

  fun loginUser(email: String, name: String, role: UserRole) {
    _currentUser.value = UserSession(
      id = "usr-${System.currentTimeMillis() % 1000}",
      name = name,
      email = email,
      role = role
    )
    logAudit("INICIO DE SESIÓN", "Seguridad", email, "Ingreso exitoso como ${role.label}")
  }

  fun logoutUser() {
    logAudit("CIERRE DE SESIÓN", "Seguridad", _currentUser.value.email, "Cierre de sesión de usuario")
    _currentUser.value = UserSession(
      id = "usr-guest",
      name = "Invitado",
      email = "guest@mickomotors.pe",
      role = UserRole.TECNICO
    )
  }

  private fun logAudit(action: String, module: String, recordRef: String, description: String) {
    val log = AuditLog(
      id = "AUD-${System.currentTimeMillis() % 10000}",
      user = _currentUser.value.name,
      action = action,
      module = module,
      recordRef = recordRef,
      date = "${currentDateStr()} ${currentTimeStr()}",
      description = description
    )
    _auditLogs.value = listOf(log) + _auditLogs.value
  }

  // CREATE OR UPDATE WORK ORDER
  fun saveWorkOrder(order: WorkOrder) {
    val existing = _workOrders.value.find { it.id == order.id }
    if (existing != null) {
      _workOrders.value = _workOrders.value.map { if (it.id == order.id) order else it }
      logAudit("ORDEN MODIFICADA", "Taller", order.id, "Actualizada orden ${order.id} (${order.vehiclePlate})")
    } else {
      _workOrders.value = listOf(order) + _workOrders.value
      logAudit("NUEVA ORDEN", "Taller", order.id, "Creada orden ${order.id} para ${order.clientName} (${order.vehiclePlate})")

      // Check if client exists or create
      val clientExists = _clients.value.any { it.name.equals(order.clientName, ignoreCase = true) }
      if (!clientExists && order.clientName.isNotBlank()) {
        val newClient = Client(
          id = "CLI-${System.currentTimeMillis() % 10000}",
          name = order.clientName,
          phone = order.clientPhone
        )
        _clients.value = _clients.value + newClient
      }

      // Check if vehicle exists or create
      val vehicleExists = _vehicles.value.any { it.plate.equals(order.vehiclePlate, ignoreCase = true) }
      if (!vehicleExists && order.vehiclePlate.isNotBlank()) {
        val newVeh = Vehicle(
          id = "VEH-${System.currentTimeMillis() % 10000}",
          plate = order.vehiclePlate.uppercase(),
          brand = order.vehicleModel.split(" ").firstOrNull() ?: "Auto",
          model = order.vehicleModel,
          year = 2020,
          mileageKm = order.mileageKm,
          ownerName = order.clientName
        )
        _vehicles.value = _vehicles.value + newVeh
      }
    }
  }

  fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
    val order = _workOrders.value.find { it.id == orderId } ?: return
    _workOrders.value = _workOrders.value.map {
      if (it.id == orderId) it.copy(status = newStatus, updatedAt = "${currentDateStr()} ${currentTimeStr()}") else it
    }
    logAudit("ESTADO ORDEN ACTUALIZADO", "Taller", orderId, "Estado de $orderId cambiado a ${newStatus.label}")

    if (newStatus == OrderStatus.LISTO) {
      val notif = NotificationItem(
        id = "NOT-${System.currentTimeMillis() % 10000}",
        title = "Vehículo listo",
        message = "Orden ${order.id} (${order.vehiclePlate}) completada por ${order.technician}",
        category = "Taller",
        targetRef = order.id,
        targetType = "ORDER",
        date = "Hoy ${currentTimeStr()}"
      )
      _notifications.value = listOf(notif) + _notifications.value
    }
  }

  // ADD SERVICE TO ORDER
  fun addServiceToOrder(orderId: String, description: String, cost: Double) {
    val order = _workOrders.value.find { it.id == orderId } ?: return
    val newService = WorkOrderService(
      id = "SRV-${System.currentTimeMillis() % 10000}",
      description = description,
      cost = cost
    )
    val updatedServices = order.services + newService
    val updatedOrder = order.copy(
      services = updatedServices,
      updatedAt = "${currentDateStr()} ${currentTimeStr()}"
    )
    _workOrders.value = _workOrders.value.map { if (it.id == orderId) updatedOrder else it }
    logAudit("SERVICIO AGREGADO", "Taller", orderId, "Agregado servicio '$description' (S/ $cost) a $orderId")
  }

  // ADD PART TO ORDER (DECREASES INVENTORY STOCK AND LOGS MOVEMENT)
  fun addPartToOrder(orderId: String, part: Part, quantity: Int, unitPrice: Double) {
    val order = _workOrders.value.find { it.id == orderId } ?: return

    // Deduct inventory
    val currentPart = _parts.value.find { it.id == part.id } ?: part
    val updatedStock = (currentPart.stock - quantity).coerceAtLeast(0)
    _parts.value = _parts.value.map {
      if (it.id == part.id) it.copy(stock = updatedStock) else it
    }

    // Log inventory movement
    val mov = InventoryMovement(
      id = "MOV-${System.currentTimeMillis() % 10000}",
      partId = part.id,
      partName = part.name,
      type = MovementType.ORDEN,
      quantity = -quantity,
      sourceDoc = orderId,
      date = "${currentDateStr()} ${currentTimeStr()}",
      user = _currentUser.value.name,
      notes = "Instalado en ${order.vehiclePlate}"
    )
    _movements.value = listOf(mov) + _movements.value

    // Add to order
    val newOrderPart = WorkOrderPart(
      id = "WOP-${System.currentTimeMillis() % 10000}",
      partId = part.id,
      name = part.name,
      partNumber = part.partNumber,
      quantity = quantity,
      unitPrice = unitPrice
    )
    val updatedOrder = order.copy(
      parts = order.parts + newOrderPart,
      updatedAt = "${currentDateStr()} ${currentTimeStr()}"
    )
    _workOrders.value = _workOrders.value.map { if (it.id == orderId) updatedOrder else it }

    logAudit("REPUESTO AGREGADO A ORDEN", "Taller", orderId, "Descontadas $quantity unidades de ${part.name} para $orderId")
  }

  // REGISTER PAYMENT (FOR ORDER, SALE, OR ACCOUNT)
  fun registerPayment(
    operationRef: String,
    entityName: String,
    amount: Double,
    method: String,
    referenceDoc: String = ""
  ) {
    val dateTimeStr = "${currentDateStr()} ${currentTimeStr()}"

    // 1. If it's a Work Order
    if (operationRef.startsWith("OT-")) {
      val order = _workOrders.value.find { it.id == operationRef }
      if (order != null) {
        val payment = OrderPayment(
          id = "PAY-${System.currentTimeMillis() % 10000}",
          amount = amount,
          method = method,
          date = dateTimeStr,
          reference = referenceDoc
        )
        val updatedOrder = order.copy(
          payments = order.payments + payment,
          updatedAt = dateTimeStr
        )
        _workOrders.value = _workOrders.value.map { if (it.id == operationRef) updatedOrder else it }
      }
    }

    // 2. Add to Cash Movement & update Cash Session
    val cashMov = CashMovement(
      id = "CM-${System.currentTimeMillis() % 10000}",
      type = CashMovementType.INGRESO,
      category = "Cobro / Pago",
      amount = amount,
      reference = operationRef,
      method = method,
      date = dateTimeStr,
      description = "Pago recibido de $entityName ($operationRef)"
    )
    _cashMovements.value = listOf(cashMov) + _cashMovements.value

    val currentCash = _cashSession.value
    _cashSession.value = currentCash.copy(
      currentAmount = currentCash.currentAmount + amount,
      totalIncomes = currentCash.totalIncomes + amount
    )

    // 3. Update Accounts Receivable if exists
    _accounts.value = _accounts.value.map { acc ->
      if (acc.operationRef == operationRef) {
        acc.copy(paidAmount = acc.paidAmount + amount)
      } else acc
    }

    // 4. Update Client Debt
    _clients.value = _clients.value.map { client ->
      if (client.name.equals(entityName, ignoreCase = true)) {
        client.copy(totalDebt = (client.totalDebt - amount).coerceAtLeast(0.0))
      } else client
    }

    logAudit("PAGO REGISTRADO", "Finanzas", operationRef, "Recibido S/ $amount vía $method de $entityName")
  }

  // CREATE NEW SALE (PARTS)
  fun createSale(sale: Sale) {
    // 1. Deduct stock for each item and log movement
    val dateTimeStr = "${currentDateStr()} ${currentTimeStr()}"
    sale.items.forEach { item ->
      val p = _parts.value.find { it.id == item.partId }
      if (p != null) {
        val newStock = (p.stock - item.quantity).coerceAtLeast(0)
        _parts.value = _parts.value.map { if (it.id == item.partId) it.copy(stock = newStock) else it }

        val mov = InventoryMovement(
          id = "MOV-${System.currentTimeMillis() % 10000}",
          partId = item.partId,
          partName = item.partName,
          type = MovementType.VENTA,
          quantity = -item.quantity,
          sourceDoc = sale.id,
          date = dateTimeStr,
          user = _currentUser.value.name,
          notes = "Venta ${sale.id} para ${sale.clientName}"
        )
        _movements.value = listOf(mov) + _movements.value
      }
    }

    // 2. Add Sale
    _sales.value = listOf(sale) + _sales.value

    // 3. If paid, update Cash; if unpaid, create Account Receivable
    if (sale.isPaid) {
      val cashMov = CashMovement(
        id = "CM-${System.currentTimeMillis() % 10000}",
        type = CashMovementType.INGRESO,
        category = "Venta de Repuestos",
        amount = sale.total,
        reference = sale.id,
        method = sale.paymentMethod,
        date = dateTimeStr,
        description = "Venta mostrador ${sale.id} a ${sale.clientName}"
      )
      _cashMovements.value = listOf(cashMov) + _cashMovements.value

      val cur = _cashSession.value
      _cashSession.value = cur.copy(
        currentAmount = cur.currentAmount + sale.total,
        totalIncomes = cur.totalIncomes + sale.total
      )
    } else {
      val acc = AccountRecord(
        id = "CXC-${System.currentTimeMillis() % 10000}",
        type = AccountType.POR_COBRAR,
        entityName = sale.clientName,
        entityId = sale.clientId,
        operationRef = sale.id,
        totalAmount = sale.total,
        paidAmount = 0.0,
        date = currentDateStr(),
        dueDate = currentDateStr() // 7 days later
      )
      _accounts.value = listOf(acc) + _accounts.value
    }

    logAudit("VENTA REGISTRADA", "Repuestos", sale.id, "Registrada venta ${sale.id} por S/ ${sale.total}")
  }

  // CREATE OR UPDATE PART
  fun savePart(part: Part) {
    val existing = _parts.value.find { it.id == part.id }
    if (existing != null) {
      _parts.value = _parts.value.map { if (it.id == part.id) part else it }
      logAudit("REPUESTO MODIFICADO", "Repuestos", part.id, "Actualizado repuesto ${part.name}")
    } else {
      _parts.value = listOf(part) + _parts.value
      val mov = InventoryMovement(
        id = "MOV-${System.currentTimeMillis() % 10000}",
        partId = part.id,
        partName = part.name,
        type = MovementType.ENTRADA,
        quantity = part.stock,
        sourceDoc = "ALTA INICIAL",
        date = "${currentDateStr()} ${currentTimeStr()}",
        user = _currentUser.value.name,
        notes = "Ingreso inicial de catálogo"
      )
      _movements.value = listOf(mov) + _movements.value
      logAudit("NUEVO REPUESTO", "Repuestos", part.id, "Alta de ${part.name} stock inicial: ${part.stock}")
    }
  }

  // CREATE PURCHASE
  fun savePurchase(purchase: Purchase) {
    _purchases.value = listOf(purchase) + _purchases.value
    if (purchase.status == PurchaseStatus.RECIBIDA) {
      receivePurchase(purchase.id)
    } else {
      // Add Account Payable
      val acc = AccountRecord(
        id = "CXP-${System.currentTimeMillis() % 10000}",
        type = AccountType.POR_PAGAR,
        entityName = purchase.supplierName,
        entityId = purchase.supplierId,
        operationRef = purchase.id,
        totalAmount = purchase.totalRealCost,
        paidAmount = 0.0,
        date = purchase.date,
        dueDate = purchase.date
      )
      _accounts.value = listOf(acc) + _accounts.value
    }
    logAudit("NUEVA COMPRA", "Repuestos", purchase.id, "Registrada compra ${purchase.id} a ${purchase.supplierName}")
  }

  fun receivePurchase(purchaseId: String) {
    val purchase = _purchases.value.find { it.id == purchaseId } ?: return
    val dateTimeStr = "${currentDateStr()} ${currentTimeStr()}"

    // Increase stock for items
    purchase.items.forEach { item ->
      val p = _parts.value.find { it.id == item.partId }
      if (p != null) {
        val updatedStock = p.stock + item.quantity
        _parts.value = _parts.value.map { if (it.id == item.partId) it.copy(stock = updatedStock) else it }

        val mov = InventoryMovement(
          id = "MOV-${System.currentTimeMillis() % 10000}",
          partId = item.partId,
          partName = item.partName,
          type = MovementType.COMPRA,
          quantity = item.quantity,
          sourceDoc = purchaseId,
          date = dateTimeStr,
          user = _currentUser.value.name,
          notes = "Recepción de compra ${purchase.supplierName}"
        )
        _movements.value = listOf(mov) + _movements.value
      }
    }

    _purchases.value = _purchases.value.map {
      if (it.id == purchaseId) it.copy(status = PurchaseStatus.RECIBIDA) else it
    }

    logAudit("COMPRA RECIBIDA", "Repuestos", purchaseId, "Aumentado inventario por recepción de compra $purchaseId")
  }

  // CREATE EXPENSE
  fun registerExpense(expense: Expense) {
    _expenses.value = listOf(expense) + _expenses.value

    // Deduct from cash
    val cashMov = CashMovement(
      id = "CM-${System.currentTimeMillis() % 10000}",
      type = CashMovementType.EGRESO,
      category = expense.category,
      amount = expense.amount,
      reference = expense.id,
      method = expense.method,
      date = expense.date,
      description = expense.description
    )
    _cashMovements.value = listOf(cashMov) + _cashMovements.value

    val cur = _cashSession.value
    _cashSession.value = cur.copy(
      currentAmount = (cur.currentAmount - expense.amount).coerceAtLeast(0.0),
      totalExpenses = cur.totalExpenses + expense.amount
    )

    logAudit("GASTO REGISTRADO", "Finanzas", expense.id, "Gasto ${expense.category}: S/ ${expense.amount} (${expense.description})")
  }

  // CASH CLOSURE
  fun closeCashSession() {
    val cur = _cashSession.value
    val time = "${currentDateStr()} ${currentTimeStr()}"
    _cashSession.value = cur.copy(
      isOpen = false,
      closedAt = time
    )
    logAudit("CIERRE DE CAJA", "Finanzas", cur.id, "Caja cerrada con saldo final de S/ ${cur.currentAmount}")
  }

  fun openCashSession(initialAmount: Double) {
    val time = "${currentDateStr()} ${currentTimeStr()}"
    val newSession = CashSession(
      id = "CAJA-${System.currentTimeMillis() % 10000}",
      isOpen = true,
      openedAt = time,
      initialAmount = initialAmount,
      currentAmount = initialAmount,
      totalIncomes = 0.0,
      totalExpenses = 0.0
    )
    _cashSession.value = newSession
    logAudit("APERTURA DE CAJA", "Finanzas", newSession.id, "Caja abierta con monto inicial S/ $initialAmount")
  }

  // CLIENTS & VEHICLES CRUD
  fun saveClient(client: Client) {
    val exists = _clients.value.any { it.id == client.id }
    if (exists) {
      _clients.value = _clients.value.map { if (it.id == client.id) client else it }
      logAudit("CLIENTE ACTUALIZADO", "Taller", client.id, "Actualizados datos de ${client.name}")
    } else {
      _clients.value = listOf(client) + _clients.value
      logAudit("NUEVO CLIENTE", "Taller", client.id, "Registrado nuevo cliente ${client.name}")
    }
  }

  fun saveVehicle(vehicle: Vehicle) {
    val exists = _vehicles.value.any { it.id == vehicle.id }
    if (exists) {
      _vehicles.value = _vehicles.value.map { if (it.id == vehicle.id) vehicle else it }
      logAudit("VEHÍCULO ACTUALIZADO", "Taller", vehicle.plate, "Actualizado vehículo ${vehicle.plate}")
    } else {
      _vehicles.value = listOf(vehicle) + _vehicles.value
      logAudit("NUEVO VEHÍCULO", "Taller", vehicle.plate, "Registrado ${vehicle.brand} ${vehicle.model} (${vehicle.plate})")
    }
  }

  fun deleteWorkOrder(orderId: String) {
    val order = _workOrders.value.find { it.id == orderId }
    _workOrders.value = _workOrders.value.filter { it.id != orderId }
    logAudit("ORDEN ELIMINADA", "Taller", orderId, "Eliminada orden ${order?.vehiclePlate ?: orderId}")
  }

  fun deleteClient(clientId: String) {
    val client = _clients.value.find { it.id == clientId }
    _clients.value = _clients.value.filter { it.id != clientId }
    logAudit("CLIENTE ELIMINADO", "Taller", clientId, "Eliminado cliente ${client?.name ?: clientId}")
  }

  fun deleteVehicle(vehicleId: String) {
    val vehicle = _vehicles.value.find { it.id == vehicleId }
    _vehicles.value = _vehicles.value.filter { it.id != vehicleId }
    logAudit("VEHÍCULO ELIMINADO", "Taller", vehicle?.plate ?: vehicleId, "Eliminado vehículo ${vehicle?.plate ?: vehicleId}")
  }

  fun deletePart(partId: String) {
    val part = _parts.value.find { it.id == partId }
    _parts.value = _parts.value.filter { it.id != partId }
    logAudit("REPUESTO ELIMINADO", "Repuestos", partId, "Eliminado repuesto ${part?.name ?: partId}")
  }

  fun deleteSale(saleId: String) {
    val sale = _sales.value.find { it.id == saleId }
    _sales.value = _sales.value.filter { it.id != saleId }
    logAudit("VENTA ELIMINADA", "Repuestos", saleId, "Eliminada venta $saleId de ${sale?.clientName ?: ""}")
  }

  fun deletePurchase(purchaseId: String) {
    val purchase = _purchases.value.find { it.id == purchaseId }
    _purchases.value = _purchases.value.filter { it.id != purchaseId }
    logAudit("COMPRA ELIMINADA", "Repuestos", purchaseId, "Eliminada compra a ${purchase?.supplierName ?: ""}")
  }

  fun deleteImport(importId: String) {
    val imp = _imports.value.find { it.id == importId }
    _imports.value = _imports.value.filter { it.id != importId }
    logAudit("IMPORTACIÓN ELIMINADA", "Repuestos", importId, "Eliminada importación ${imp?.description ?: importId}")
  }

  fun deleteExpense(expenseId: String) {
    val exp = _expenses.value.find { it.id == expenseId }
    _expenses.value = _expenses.value.filter { it.id != expenseId }
    logAudit("GASTO ELIMINADO", "Finanzas", expenseId, "Eliminado gasto ${exp?.description ?: expenseId}")
  }

  // GLOBAL SEARCH ENGINE (Simultaneously across Clients, Plates, VIN, Orders, Parts, Sales, etc.)
  fun searchAll(query: String): List<SearchResultItem> {
    val q = query.trim().lowercase()
    if (q.isBlank()) return emptyList()

    val results = mutableListOf<SearchResultItem>()

    // Search Vehicles
    _vehicles.value.filter {
      it.plate.lowercase().contains(q) ||
        it.vin.lowercase().contains(q) ||
        it.model.lowercase().contains(q) ||
        it.brand.lowercase().contains(q) ||
        it.ownerName.lowercase().contains(q)
    }.forEach {
      results.add(
        SearchResultItem(
          id = it.id,
          title = "${it.brand} ${it.model} (${it.year})",
          subtitle = "Placa: ${it.plate} • Propietario: ${it.ownerName}",
          badge = "Vehículo",
          type = "VEHICLE",
          dataRef = it.id
        )
      )
    }

    // Search Clients
    _clients.value.filter {
      it.name.lowercase().contains(q) ||
        it.dniRuc.lowercase().contains(q) ||
        it.phone.lowercase().contains(q)
    }.forEach {
      results.add(
        SearchResultItem(
          id = it.id,
          title = it.name,
          subtitle = "Tel: ${it.phone} • DNI/RUC: ${it.dniRuc}",
          badge = "Cliente",
          type = "CLIENT",
          dataRef = it.id
        )
      )
    }

    // Search Work Orders
    _workOrders.value.filter {
      it.id.lowercase().contains(q) ||
        it.vehiclePlate.lowercase().contains(q) ||
        it.clientName.lowercase().contains(q) ||
        it.reportedProblem.lowercase().contains(q)
    }.forEach {
      results.add(
        SearchResultItem(
          id = it.id,
          title = "${it.id} - ${it.vehicleModel}",
          subtitle = "${it.vehiclePlate} • ${it.clientName} • ${it.status.label}",
          badge = "Orden OT",
          type = "ORDER",
          dataRef = it.id
        )
      )
    }

    // Search Parts
    _parts.value.filter {
      it.name.lowercase().contains(q) ||
        it.partNumber.lowercase().contains(q) ||
        it.brand.lowercase().contains(q) ||
        it.category.lowercase().contains(q)
    }.forEach {
      results.add(
        SearchResultItem(
          id = it.id,
          title = it.name,
          subtitle = "N° Parte: ${it.partNumber} • Stock: ${it.stock} • S/ ${it.salePrice}",
          badge = "Repuesto",
          type = "PART",
          dataRef = it.id
        )
      )
    }

    // Search Sales
    _sales.value.filter {
      it.id.lowercase().contains(q) ||
        it.clientName.lowercase().contains(q)
    }.forEach {
      results.add(
        SearchResultItem(
          id = it.id,
          title = "${it.id} - ${it.clientName}",
          subtitle = "Total: S/ ${it.total} • ${it.date}",
          badge = "Venta",
          type = "SALE",
          dataRef = it.id
        )
      )
    }

    return results
  }
}

data class SearchResultItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val badge: String,
  val type: String,
  val dataRef: String
)
