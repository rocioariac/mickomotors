package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class UserRole(val label: String) {
  ADMINISTRADOR("Administrador"),
  TECNICO("Técnico"),
  VENDEDOR("Vendedor")
}

data class UserSession(
  val id: String = "usr-01",
  val name: String = "Rocío Ariac",
  val email: String = "admin@mickomotors.pe",
  val role: UserRole = UserRole.ADMINISTRADOR
)

data class Client(
  val id: String,
  val name: String,
  val phone: String,
  val whatsapp: String = "",
  val email: String = "",
  val dniRuc: String = "",
  val address: String = "",
  val notes: String = "",
  val totalDebt: Double = 0.0
)

data class Vehicle(
  val id: String,
  val plate: String,
  val brand: String,
  val model: String,
  val year: Int,
  val vin: String = "",
  val engine: String = "2.4L Gasolina",
  val transmission: String = "Automática",
  val fuel: String = "Gasolina 95",
  val mileageKm: Int = 0,
  val ownerClientId: String = "",
  val ownerName: String = ""
)

enum class OrderStatus(val label: String) {
  RECIBIDO("Recibido"),
  DIAGNOSTICO("Diagnóstico"),
  ESPERANDO_REPUESTO("Esperando repuesto"),
  EN_REPARACION("En reparación"),
  LISTO("Listo"),
  ENTREGADO("Entregado")
}

data class WorkOrderService(
  val id: String,
  val description: String,
  val cost: Double
)

data class WorkOrderPart(
  val id: String,
  val partId: String,
  val name: String,
  val partNumber: String,
  val quantity: Int,
  val unitPrice: Double
) {
  val total: Double get() = quantity * unitPrice
}

data class OrderPayment(
  val id: String,
  val amount: Double,
  val method: String,
  val date: String,
  val reference: String = ""
)

data class WorkOrder(
  val id: String, // e.g. "OT-00218"
  val clientId: String,
  val clientName: String,
  val clientPhone: String,
  val vehicleId: String,
  val vehiclePlate: String,
  val vehicleModel: String,
  val mileageKm: Int,
  val status: OrderStatus,
  val technician: String,
  val reportedProblem: String,
  val diagnostic: String = "",
  val laborCost: Double = 0.0,
  val services: List<WorkOrderService> = emptyList(),
  val parts: List<WorkOrderPart> = emptyList(),
  val payments: List<OrderPayment> = emptyList(),
  val createdAt: String = "",
  val updatedAt: String = ""
) {
  val servicesTotal: Double get() = services.sumOf { it.cost }
  val partsTotal: Double get() = parts.sumOf { it.total }
  val total: Double get() = laborCost + servicesTotal + partsTotal
  val paid: Double get() = payments.sumOf { it.amount }
  val pending: Double get() = (total - paid).coerceAtLeast(0.0)
}

data class Part(
  val id: String,
  val name: String,
  val partNumber: String,
  val brand: String,
  val category: String,
  val stock: Int,
  val minStock: Int = 2,
  val purchaseCost: Double,
  val realCost: Double,
  val salePrice: Double,
  val location: String = "A-12",
  val compatibility: List<String> = emptyList()
) {
  val profit: Double get() = salePrice - realCost
  val marginOnCost: Double get() = if (realCost > 0) (profit / realCost) * 100.0 else 0.0
  val marginOnSale: Double get() = if (salePrice > 0) (profit / salePrice) * 100.0 else 0.0
  val isLowStock: Boolean get() = stock in 1..minStock
  val isOutOfStock: Boolean get() = stock <= 0
}

enum class MovementType(val label: String) {
  ENTRADA("Entrada"),
  SALIDA("Salida"),
  VENTA("Venta"),
  COMPRA("Compra"),
  ORDEN("Orden OT"),
  AJUSTE("Ajuste"),
  DEVOLUCION("Devolución")
}

data class InventoryMovement(
  val id: String,
  val partId: String,
  val partName: String,
  val type: MovementType,
  val quantity: Int,
  val sourceDoc: String,
  val date: String,
  val user: String,
  val notes: String = ""
)

data class SaleItem(
  val partId: String,
  val partName: String,
  val quantity: Int,
  val unitPrice: Double,
  val unitCost: Double = 0.0
) {
  val total: Double get() = quantity * unitPrice
  val profit: Double get() = quantity * (unitPrice - unitCost)
}

data class Sale(
  val id: String, // e.g. "V-00124"
  val clientId: String,
  val clientName: String,
  val items: List<SaleItem>,
  val discount: Double = 0.0,
  val paymentMethod: String = "Efectivo",
  val isPaid: Boolean = true,
  val date: String
) {
  val subtotal: Double get() = items.sumOf { it.total }
  val total: Double get() = (subtotal - discount).coerceAtLeast(0.0)
  val profit: Double get() = items.sumOf { it.profit } - discount
}

data class PurchaseItem(
  val partId: String,
  val partName: String,
  val quantity: Int,
  val unitCost: Double
) {
  val total: Double get() = quantity * unitCost
}

enum class PurchaseStatus(val label: String) {
  PENDIENTE("Pendiente"),
  RECIBIDA("Recibida")
}

data class Purchase(
  val id: String, // e.g. "COM-0042"
  val supplierId: String,
  val supplierName: String,
  val items: List<PurchaseItem>,
  val freightCost: Double = 0.0,
  val courierCost: Double = 0.0,
  val customsCost: Double = 0.0,
  val taxesCost: Double = 0.0,
  val status: PurchaseStatus = PurchaseStatus.PENDIENTE,
  val date: String
) {
  val productsTotal: Double get() = items.sumOf { it.total }
  val extraCostsTotal: Double get() = freightCost + courierCost + customsCost + taxesCost
  val totalRealCost: Double get() = productsTotal + extraCostsTotal
}

enum class ImportStatus(val label: String) {
  PEDIDO("Pedido"),
  PAGADO("Pagado"),
  EN_TRANSITO("En tránsito"),
  EN_ADUANA("En aduana"),
  EN_LIMA("En Lima"),
  EN_CAMINO("En camino a Moquegua"),
  RECIBIDO("Recibido")
}

data class ImportRecord(
  val id: String, // e.g. "IMP-0034"
  val description: String,
  val originCountry: String,
  val trackingNumber: String,
  val status: ImportStatus,
  val itemsCount: Int,
  val productCostUsd: Double,
  val freightUsd: Double,
  val customsPen: Double,
  val localFreightPen: Double,
  val exchangeRate: Double = 3.75,
  val date: String
) {
  val productCostPen: Double get() = productCostUsd * exchangeRate
  val freightPen: Double get() = freightUsd * exchangeRate
  val totalCostPen: Double get() = productCostPen + freightPen + customsPen + localFreightPen
}

data class Supplier(
  val id: String,
  val name: String,
  val contactPerson: String,
  val phone: String,
  val email: String = "",
  val ruc: String = "",
  val country: String = "Perú",
  val debtPending: Double = 0.0
)

enum class CashMovementType(val label: String) {
  INGRESO("Ingreso"),
  EGRESO("Egreso")
}

data class CashMovement(
  val id: String,
  val type: CashMovementType,
  val category: String,
  val amount: Double,
  val reference: String,
  val method: String,
  val date: String,
  val description: String = ""
)

data class CashSession(
  val id: String,
  val isOpen: Boolean = true,
  val openedAt: String,
  val closedAt: String? = null,
  val initialAmount: Double = 500.0,
  val currentAmount: Double = 3850.0,
  val totalIncomes: Double = 4120.0,
  val totalExpenses: Double = 770.0
)

data class Expense(
  val id: String,
  val category: String,
  val amount: Double,
  val description: String,
  val method: String,
  val date: String,
  val receiptNumber: String = ""
)

enum class AccountType(val label: String) {
  POR_COBRAR("Por cobrar"),
  POR_PAGAR("Por pagar")
}

data class AccountRecord(
  val id: String,
  val type: AccountType,
  val entityName: String,
  val entityId: String,
  val operationRef: String,
  val totalAmount: Double,
  val paidAmount: Double,
  val date: String,
  val dueDate: String,
  val isOverdue: Boolean = false
) {
  val pendingAmount: Double get() = (totalAmount - paidAmount).coerceAtLeast(0.0)
  val isFullyPaid: Boolean get() = pendingAmount <= 0.001
}

data class PaymentRecord(
  val id: String,
  val operationRef: String,
  val entityName: String,
  val amount: Double,
  val method: String,
  val date: String,
  val referenceDoc: String = ""
)

data class AuditLog(
  val id: String,
  val user: String,
  val action: String,
  val module: String,
  val recordRef: String,
  val date: String,
  val description: String
)

data class NotificationItem(
  val id: String,
  val title: String,
  val message: String,
  val category: String, // "Taller", "Stock", "Finanzas"
  val targetRef: String, // e.g. "OT-00217", "IMP-0034", "Amortiguador KYB"
  val targetType: String, // "ORDER", "PART", "IMPORT", "ACCOUNT"
  val date: String,
  val isRead: Boolean = false
)

data class Employee(
  val id: String,
  val name: String,
  val role: String,
  val phone: String,
  val activeOrdersCount: Int,
  val status: String = "Activo"
)

data class GarageSettings(
  val companyName: String = "MICKO MOTORS",
  val slogan: String = "Control total de tu taller.",
  val ruc: String = "20608945123",
  val address: String = "Av. Manuel C. de la Torre 840, Moquegua",
  val phone: String = "+51 953 912 845",
  val email: String = "contacto@mickomotors.pe",
  val currencySymbol: String = "S/",
  val secondaryCurrency: String = "USD"
)
