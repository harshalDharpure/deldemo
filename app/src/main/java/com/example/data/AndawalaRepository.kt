package com.example.data

import kotlinx.coroutines.flow.Flow

class AndawalaRepository(private val dao: AndawalaDao) {

    val allProducts: Flow<List<Product>> = dao.getAllProductsFlow()
    val allOrders: Flow<List<Order>> = dao.getAllOrdersFlow()
    val allSubscriptions: Flow<List<Subscription>> = dao.getAllSubscriptionsFlow()
    val allDeliveries: Flow<List<Delivery>> = dao.getAllDeliveriesFlow()
    val allPartners: Flow<List<DeliveryPartner>> = dao.getAllPartnersFlow()
    val inventoryRecords: Flow<List<InventoryRecord>> = dao.getInventoryFlow()
    val notifications: Flow<List<AppNotification>> = dao.getAllNotificationsFlow()

    fun getOrdersByUserId(userId: Int): Flow<List<Order>> = dao.getOrdersByUserId(userId)
    fun getSubscriptionsByUserId(userId: Int): Flow<List<Subscription>> = dao.getSubscriptionsByUserId(userId)
    fun getAddressesByUserId(userId: Int): Flow<List<Address>> = dao.getAddressesByUserId(userId)
    fun getDeliveriesByPartner(partnerId: Int): Flow<List<Delivery>> = dao.getDeliveriesByPartner(partnerId)
    fun getUserByIdFlow(id: Int): Flow<User?> = dao.getUserByIdFlow(id)

    suspend fun getUserByPhone(phone: String): User? = dao.getUserByPhone(phone)
    suspend fun getUserById(id: Int): User? = dao.getUserById(id)
    suspend fun insertUser(user: User): Long = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)

    suspend fun insertAddress(address: Address): Long = dao.insertAddress(address)

    suspend fun insertOrder(order: Order, items: List<OrderItem>): Long {
        val orderId = dao.insertOrder(order).toInt()
        val itemsWithId = items.map { it.copy(orderId = orderId) }
        dao.insertOrderItems(itemsWithId)
        return orderId.toLong()
    }

    suspend fun getOrderItems(orderId: Int): List<OrderItem> = dao.getOrderItems(orderId)
    suspend fun getOrderById(orderId: Int): Order? = dao.getOrderById(orderId)

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun updateOrderPaymentStatus(orderId: Int, paymentStatus: String) {
        dao.updateOrderPaymentStatus(orderId, paymentStatus)
    }

    suspend fun insertSubscription(subscription: Subscription): Long = dao.insertSubscription(subscription)
    suspend fun updateSubscriptionStatus(subId: Int, status: String) = dao.updateSubscriptionStatus(subId, status)
    suspend fun updateSubscriptionVacation(subId: Int, isVacation: Boolean) {
        dao.updateSubscriptionVacation(subId, if (isVacation) 1 else 0)
    }

    suspend fun insertDelivery(delivery: Delivery): Long = dao.insertDelivery(delivery)
    suspend fun updateDeliveryStatus(deliveryId: Int, status: String, time: String) = dao.updateDeliveryStatus(deliveryId, status, time)

    suspend fun insertPartner(partner: DeliveryPartner): Long = dao.insertPartner(partner)
    suspend fun updatePartnerEarnings(partnerId: Int, earning: Double) = dao.updatePartnerEarnings(partnerId, earning)

    suspend fun insertInventoryRecord(record: InventoryRecord): Long = dao.insertInventoryRecord(record)
    suspend fun insertNotification(notification: AppNotification): Long = dao.insertNotification(notification)

    suspend fun seedDatabaseIfEmpty() {
        // Only seed if empty
        val existingProducts = dao.getAllProducts()
        if (existingProducts.isEmpty()) {
            // Seed Products
            dao.insertProduct(Product(name = "Boiled Egg (Single)", nameKannada = "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆ (ಒಂದು)", category = "EGGS", price = 15.0, description = "Freshly boiled organic farm-fresh egg, peeled and ready to eat."))
            dao.insertProduct(Product(name = "Boiled Egg (2 Eggs Pack)", nameKannada = "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆ ಪ್ಯಾಕ್ (೨)", category = "EGGS", price = 28.0, description = "Two freshly boiled warm eggs, sprinkled with salt & pepper."))
            dao.insertProduct(Product(name = "Boiled Egg (4 Eggs Pack)", nameKannada = "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆ ಪ್ಯಾಕ್ (೪)", category = "EGGS", price = 52.0, description = "Pack of 4 high-protein farm boiled eggs."))
            dao.insertProduct(Product(name = "Boiled Egg (6 Eggs Pack)", nameKannada = "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆ ಪ್ಯಾಕ್ (೬)", category = "EGGS", price = 75.0, description = "Pack of 6 boiled eggs ideal for standard breakfast."))
            dao.insertProduct(Product(name = "Boiled Egg (12 Eggs Pack)", nameKannada = "ಬೇಯಿಸಿದ ಮೊಟ್ಟೆ ಪ್ಯಾಕ್ (೧೨)", category = "EGGS", price = 140.0, description = "Dozen family pack of perfectly boiled nutritious eggs."))
            
            dao.insertProduct(Product(name = "Black Salt Sachet", nameKannada = "ಕಪ್ಪು ಉಪ್ಪು ಪ್ಯಾಕೇಟ್", category = "ADDONS", price = 2.0, description = "Traditional Ayurvedic salt mix to elevate digestability."))
            dao.insertProduct(Product(name = "Pepper Sachet", nameKannada = "ಮೆಣಸಿನ ಪುಡಿ ಪ್ಯಾಕೇಟ್", category = "ADDONS", price = 2.0, description = "Premium black pepper powder for some gentle heat."))
            dao.insertProduct(Product(name = "Andawala Masala Mix", nameKannada = "ಮಸಾಲಾ ಮಿಕ್ಸ್", category = "ADDONS", price = 5.0, description = "Special in-house secret spice blend crafted for eggs."))
            
            dao.insertProduct(Product(name = "Protein Combo Pack", nameKannada = "ಪ್ರೋಟೀನ್ ಕಾಂಬೊ ಪ್ಯಾಕ್", category = "COMBOS", price = 99.0, description = "4 Boiled Eggs + Special Masala + 1 Butter Milk Sachet."))
            dao.insertProduct(Product(name = "Fitness Pack", nameKannada = "ಫಿಟ್ನೆಸ್ ಪ್ಯಾಕ್", category = "COMBOS", price = 149.0, description = "6 Boiled Eggs (perfectly textured yolk) + Pepper Mix + Chia seed packet."))
            dao.insertProduct(Product(name = "Family Breakfast Combo", nameKannada = "ಕುಟುಂಬದ ಉಪಹಾರ ಕಾಂಬೊ", category = "COMBOS", price = 249.0, description = "12 perfect Boiled eggs + 2 boxes of Bengaluru pepper sprinkle."))

            // Seed Delivery Partners
            dao.insertPartner(DeliveryPartner(id = 1, name = "Ramesh Gowda", phone = "+91 98765 43210", bikeNo = "KA-03-EG-4567", rating = 4.9))
            dao.insertPartner(DeliveryPartner(id = 2, name = "Manjunath K.", phone = "+91 99001 12233", bikeNo = "KA-05-EG-8901", rating = 4.7))

            // Seed Local Inventory
            dao.insertInventoryRecord(InventoryRecord(recordDate = "2026-06-06", rawEggsPurchased = 600, eggsBoiled = 550, eggsWasted = 15, remainingStock = 85))

            // Seed Default Admin (for simulation)
            dao.insertUser(User(id = 999, phone = "9999999999", name = "Andawala Master Admin", role = "ADMIN", walletBalance = 10000.0))

            // Seed Notifications
            dao.insertNotification(AppNotification(title = "Welcome to Andawala!", message = "Pre-peeled nutritious boiled eggs delivered daily to your apartment doorstep in Bangalore! Select English or ಕನ್ನಡ above."))
        }
    }
}
