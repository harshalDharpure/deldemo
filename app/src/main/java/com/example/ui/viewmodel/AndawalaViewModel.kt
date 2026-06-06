package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AndawalaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AndawalaDatabase.getDatabase(application)
    val dao = db.dao()
    val repository = AndawalaRepository(dao)

    // Current State Management
    val currentUser = MutableStateFlow<User?>(null)
    val currentRole = MutableStateFlow("CUSTOMER") // CUSTOMER, DELIVERY, ADMIN
    val isKannada = MutableStateFlow(false) // Language setting
    val cart = MutableStateFlow<Map<Int, Int>>(emptyMap()) // Product ID -> Quantity

    // UI Input states
    val loginPhone = MutableStateFlow("")
    val loginName = MutableStateFlow("")
    val isOtpSent = MutableStateFlow(false)
    val otpCodeInput = MutableStateFlow("")
    val isLoggingIn = MutableStateFlow(false)

    // Checkouts/Form parameters
    val checkoutAddressStr = MutableStateFlow("Sobha Tulip Apartments, Phase 2, Hennur Road, Bengaluru")
    val selectedDeliverySlot = MutableStateFlow("Morning (6-9 AM)") // Morning (6-9 AM), Evening (5-8 PM)
    val walletRechargeAmount = MutableStateFlow("500")

    // Subscriptions setup
    val subPlanSelected = MutableStateFlow("Daily") // Daily, Weekly, Monthly
    val subQuantityPerDay = MutableStateFlow(2) // 2, 4, 6 eggs

    // Flow integration for UI
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubscriptions: StateFlow<List<Subscription>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeliveries: StateFlow<List<Delivery>> = repository.allDeliveries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPartners: StateFlow<List<DeliveryPartner>> = repository.allPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventoryRecords: StateFlow<List<InventoryRecord>> = repository.inventoryRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated Tracking state
    val liveTrackingDeliveryId = MutableStateFlow<Int?>(null)
    val trackerStatus = MutableStateFlow("Order Confirmed")
    val trackerEta = MutableStateFlow("25 mins")
    val trackerProgress = MutableStateFlow(0.1f) // 0f to 1f
    val trackerLocationDesc = MutableStateFlow("Andawala Indiranagar Kitchen")

    // AI demand predictions text
    val aiInsights = MutableStateFlow<String>("Click 'Forecast with Gemini' to get Bangalore weekend weather & demand recommendations.")
    val isPredictionLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
            // Auto login as a mock customer on startup to get people started immediately
            val initialCustomer = dao.getUserByPhone("+91 9480112233")
            if (initialCustomer == null) {
                val newId = dao.insertUser(User(phone = "+91 9480112233", name = "Harshal Dharpure", walletBalance = 650.0))
                currentUser.value = dao.getUserById(newId.toInt())
            } else {
                currentUser.value = initialCustomer
            }
            startDeliverySimulationTracker()
        }
    }

    private fun startDeliverySimulationTracker() {
        viewModelScope.launch {
            while (true) {
                delay(8000)
                val id = liveTrackingDeliveryId.value
                if (id != null) {
                    val currentProgress = trackerProgress.value
                    if (currentProgress < 1.0f) {
                        val nextProgress = currentProgress + 0.2f
                        trackerProgress.value = nextProgress.coerceAtMost(1.0f)
                        when {
                            nextProgress < 0.3f -> {
                                trackerStatus.value = "Preparing Boiled Eggs"
                                trackerLocationDesc.value = "Farms to Central Bengaluru Kitchen"
                                trackerEta.value = "20 mins"
                            }
                            nextProgress < 0.6f -> {
                                trackerStatus.value = "Out For Delivery"
                                trackerLocationDesc.value = "Near Hennur Flyover, Bengaluru"
                                trackerEta.value = "12 mins"
                            }
                            nextProgress < 0.9f -> {
                                trackerStatus.value = "Entering Society Gate"
                                trackerLocationDesc.value = "Apartment Security Desk Verification"
                                trackerEta.value = "3 mins"
                            }
                            else -> {
                                trackerStatus.value = "Delivered to Doorstep"
                                trackerLocationDesc.value = "Placed gently inside the Andawala morning pouch!"
                                trackerEta.value = "Completed"
                                // Also update DB delivery status
                                repository.updateDeliveryStatus(id, "Completed", SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()))
                                repository.insertNotification(AppNotification(
                                    title = "Sachet Eggs Pack Delivered!",
                                    message = "Good morning! Your fresh boiled eggs are placed at your doorstep."
                                ))
                                liveTrackingDeliveryId.value = null
                            }
                        }
                    }
                }
            }
        }
    }

    // Role switcher
    fun setRole(role: String) {
        currentRole.value = role
    }

    // Auth flows
    fun requestOtp() {
        if (loginPhone.value.length >= 10) {
            isOtpSent.value = true
            viewModelScope.launch {
                repository.insertNotification(AppNotification(
                    title = "Andawala OTP Verification",
                    message = "Your verification OTP is 1234. Use it to complete login!"
                ))
            }
        }
    }

    fun loginOrRegister() {
        if (otpCodeInput.value == "1234" || otpCodeInput.value == "4321") {
            isLoggingIn.value = true
            viewModelScope.launch {
                val existing = repository.getUserByPhone(loginPhone.value)
                if (existing != null) {
                    currentUser.value = existing
                } else {
                    val defaultName = if (loginName.value.isNotEmpty()) loginName.value else "Bangalore Customer"
                    val newId = repository.insertUser(User(phone = loginPhone.value, name = defaultName, walletBalance = 300.0))
                    currentUser.value = repository.getUserById(newId.toInt())
                }
                isLoggingIn.value = false
                isOtpSent.value = false
                loginPhone.value = ""
                otpCodeInput.value = ""
            }
        }
    }

    fun logout() {
        currentUser.value = null
    }

    // Cart actions
    fun addToCart(productId: Int) {
        val current = cart.value.toMutableMap()
        current[productId] = (current[productId] ?: 0) + 1
        cart.value = current
    }

    fun removeFromCart(productId: Int) {
        val current = cart.value.toMutableMap()
        val count = current[productId] ?: 0
        if (count <= 1) {
            current.remove(productId)
        } else {
            current[productId] = count - 1
        }
        cart.value = current
    }

    fun clearCart() {
        cart.value = emptyMap()
    }

    // Purchase Egg Order
    fun placeOrder(paymentMethodSelected: String) {
        val user = currentUser.value ?: return
        val currentCart = cart.value
        if (currentCart.isEmpty()) return

        val productsList = allProducts.value
        var total = 0.0
        val itemsToSave = mutableListOf<OrderItem>()

        currentCart.forEach { (prodId, qty) ->
            val p = productsList.find { it.id == prodId }
            if (p != null) {
                total += p.price * qty
                itemsToSave.add(OrderItem(
                    orderId = 0,
                    productId = prodId,
                    productName = p.name,
                    quantity = qty,
                    price = p.price
                ))
            }
        }

        viewModelScope.launch {
            if (paymentMethodSelected == "Wallet") {
                if (user.walletBalance < total) {
                    repository.insertNotification(AppNotification(title = "Payment Failed", message = "Insufficient wallet balance. Recharge to place order."))
                    return@launch
                }
                repository.updateUser(user.copy(walletBalance = user.walletBalance - total))
                currentUser.value = repository.getUserById(user.id)
            }

            val newOrder = Order(
                userId = user.id,
                totalAmount = total,
                deliverySlot = selectedDeliverySlot.value,
                paymentMethod = paymentMethodSelected,
                paymentStatus = if (paymentMethodSelected == "Wallet") "Paid" else "Pending",
                orderStatus = "Order Placed",
                societyName = checkoutAddressStr.value.substringBefore(",")
            )

            val orderId = repository.insertOrder(newOrder, itemsToSave)

            // Auto create active delivery record for simulated partner task
            val finalDelivery = Delivery(
                orderId = orderId.toInt(),
                deliveryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                targetType = "ORDER",
                customerName = user.name,
                phone = user.phone,
                societyName = newOrder.societyName,
                doorNo = "Flat 503",
                deliverySlot = newOrder.deliverySlot,
                status = "Assigned"
            )
            val devId = repository.insertDelivery(finalDelivery)

            // Setup tracking
            liveTrackingDeliveryId.value = devId.toInt()
            trackerProgress.value = 0.1f
            trackerStatus.value = "Order Placed"
            trackerLocationDesc.value = "Indiranagar Kitchen"

            clearCart()
            repository.insertNotification(AppNotification(title = "Order Confirmed!", message = "Your gourmet egg package of Rs. $total is confirmed on ${selectedDeliverySlot.value}! Tracking active."))
        }
    }

    // Subscription actions
    fun subscribePlan() {
        val user = currentUser.value ?: return
        val plan = subPlanSelected.value
        val qty = subQuantityPerDay.value
        val pricePerEgg = 14.0 // discounted egg price for subscription
        val totals = when (plan) {
            "Daily" -> pricePerEgg * qty * 7 // weekly trial count deposit
            "Weekly" -> pricePerEgg * qty * 7
            "Monthly" -> pricePerEgg * qty * 30
            else -> pricePerEgg * qty * 7
        }

        viewModelScope.launch {
            if (user.walletBalance < totals) {
                repository.insertNotification(AppNotification(title = "Subscription Refused", message = "Wallet deposit must have at least Rs. $totals to setup subscription! Please recharge wallet."))
                return@launch
            }

            // Deduct deposit
            repository.updateUser(user.copy(walletBalance = user.walletBalance - totals))
            currentUser.value = repository.getUserById(user.id)

            val society = checkoutAddressStr.value.substringBefore(",")
            val sub = Subscription(
                userId = user.id,
                planName = plan,
                quantityPerDay = qty,
                deliverySlot = selectedDeliverySlot.value,
                societyName = society,
                status = "Active"
            )
            val subId = repository.insertSubscription(sub)

            // Insert daily simulated delivery task
            repository.insertDelivery(Delivery(
                subscriptionId = subId.toInt(),
                deliveryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                targetType = "SUBSCRIPTION",
                customerName = user.name,
                phone = user.phone,
                societyName = society,
                doorNo = "Flat 503",
                deliverySlot = selectedDeliverySlot.value,
                status = "Assigned"
            ))

            repository.insertNotification(AppNotification(
                title = "Andawala subscription initialized!",
                message = "Subscribed to $plan ($qty eggs/day) for doorstep delivery in $society on ${selectedDeliverySlot.value}."
            ))
        }
    }

    fun pauseSubscription(subId: Int) {
        viewModelScope.launch {
            repository.updateSubscriptionStatus(subId, "Paused")
            repository.insertNotification(AppNotification(title = "Subscription Paused", message = "Morning slot egg deliveries skipped temporarily."))
        }
    }

    fun resumeSubscription(subId: Int) {
        viewModelScope.launch {
            repository.updateSubscriptionStatus(subId, "Active")
            repository.insertNotification(AppNotification(title = "Subscription Resumed", message = "Eggs will arrive at your door tomorrow morning!"))
        }
    }

    // Wallet recharge
    fun rechargeWallet() {
        val user = currentUser.value ?: return
        val amount = walletRechargeAmount.value.toDoubleOrNull() ?: 100.0
        viewModelScope.launch {
            repository.updateUser(user.copy(walletBalance = user.walletBalance + amount))
            currentUser.value = repository.getUserById(user.id)
            repository.insertNotification(AppNotification(title = "Wallet Refilled", message = "Successfully added Rs. $amount using instant UPI checkout."))
        }
    }

    // Delivery Partner Actions
    fun markDeliveryCompleted(deliveryId: Int) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(deliveryId, "Completed", SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()))
            // Add delivery partner earnings feedback
            repository.updatePartnerEarnings(1, 45.0) // Ramesh gets Rs. 45 per local society drop
            repository.insertNotification(AppNotification(title = "Egg delivered by रमेश", message = "Ramesh marked your boiling egg delivery package as Completed!"))
        }
    }

    fun markCustomerUnavailable(deliveryId: Int) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(deliveryId, "Customer Unavailable", SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()))
            // Log warning & trigger alternative slots or credit refunds
            repository.insertNotification(AppNotification(title = "Doorstep Locked", message = "Delivery agent couldn't place the eggs. Re-attempt scheduled for Evening shift."))
        }
    }

    // Admin Action: Mutate stock
    fun boilEggsAdmin(count: Int) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.insertInventoryRecord(InventoryRecord(
                recordDate = dateStr,
                rawEggsPurchased = 100,
                eggsBoiled = count,
                eggsWasted = 2,
                remainingStock = count
            ))
            repository.insertNotification(AppNotification(title = "Egg Boiler Initiated", message = "Kitchen master boiled $count eggs for tomorrow's Hennur apartment slots."))
        }
    }

    // AI Prediction via Gemini API
    fun generateAiForecast() {
        isPredictionLoading.value = true
        _generateForecast()
    }

    private fun _generateForecast() {
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback simulation text
                delay(12000) // simulation delay
                aiInsights.value = """
                    📊 ANDAWALA AI DEMAND FORECAST (BENGALURU LOCAL)

                    🌤️ Weather Alert: Light monsoon scattered showers expected tomorrow across Indiranagar & Outer Ring Road.
                    🍗 Subscription Demand: 12 active household orders in Sobha Tulip, Hennur.
                    
                    ✨ RECOMMENDATIONS:
                    1. BOIL RATING: Increase boiler set-point by 15% (Target: 220 eggs total). Rainy breakfasts boost soft-boiled demand!
                    2. DEPLOYMENT ROUTE: Hennur apartments have high bulk volume. Direct রমেশ to complete Hennur first, then HSR layout.
                    3. WASTE PREVENTION FORECAST: Restock salt sachets as pepper drops are expected due to moisture levels.
                """.trimIndent()
                isPredictionLoading.value = false
                return@launch
            }

            try {
                val activeSubsCount = allSubscriptions.value.filter { it.status == "Active" }.size
                val rawCount = inventoryRecords.value.firstOrNull()?.rawEggsPurchased ?: 500

                val prompt = """
                    You are Andawala AI Master, the smart sales forecaster for Bangalore's premier doorstep boiled eggs service "Andawala".
                    Analyze these metrics and provide a short, structured bullet-point operational analysis:
                    - Active Morning Subscriptions: $activeSubsCount households.
                    - Raw egg inventory: $rawCount units.
                    - Bangalore Weather Season: Monsoon (light rain).
                    
                    Format the response under three headers with 2 concise bullets each:
                    1. 📈 DEMAND FORECAST
                    2. 🛣️ ROUTE OPTIMIZATION DETOURS
                    3. 💡 BULK RETENTION INSIGHT
                """.trimIndent()

                val request = GeminiRequest(listOf(GeminiContent(listOf(GeminiPart(prompt)))))
                val response = GeminiClient.service.generateContent(apiKey, request)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!textResponse.isNullOrEmpty()) {
                    aiInsights.value = textResponse
                } else {
                    aiInsights.value = "AI API succeeded but returned empty result content. Please check quotas."
                }
            } catch (e: Exception) {
                aiInsights.value = "AI Engine offline (Exception: ${e.message}). Falling back to Bangalore neighborhood metrics simulation."
            } finally {
                isPredictionLoading.value = false
            }
        }
    }
}
