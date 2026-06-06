package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AndawalaDao {

    // --- USER ---
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: Int): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    // --- ADDRESS ---
    @Query("SELECT * FROM addresses WHERE userId = :userId")
    fun getAddressesByUserId(userId: Int): Flow<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: Address): Long

    // --- PRODUCT ---
    @Query("SELECT * FROM products ORDER BY category ASC, id ASC")
    fun getAllProductsFlow(): Flow<List<Product>>

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    // --- ORDER ---
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUserId(userId: Int): Flow<List<Order>>

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrdersFlow(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Int): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, status: String)

    @Query("UPDATE orders SET paymentStatus = :paymentStatus WHERE id = :id")
    suspend fun updateOrderPaymentStatus(id: Int, paymentStatus: String)

    // --- ORDER ITEMS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Int): List<OrderItem>

    // --- SUBSCRIPTION ---
    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY startDate DESC")
    fun getSubscriptionsByUserId(userId: Int): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions ORDER BY startDate DESC")
    fun getAllSubscriptionsFlow(): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Query("UPDATE subscriptions SET status = :status WHERE id = :id")
    suspend fun updateSubscriptionStatus(id: Int, status: String)

    @Query("UPDATE subscriptions SET isVacationMode = :vacationMode WHERE id = :id")
    suspend fun updateSubscriptionVacation(id: Int, vacationMode: Int)

    // --- DELIVERIES ---
    @Query("SELECT * FROM deliveries ORDER BY deliveryDate DESC")
    fun getAllDeliveriesFlow(): Flow<List<Delivery>>

    @Query("SELECT * FROM deliveries WHERE partnerId = :partnerId ORDER BY deliveryDate DESC")
    fun getDeliveriesByPartner(partnerId: Int): Flow<List<Delivery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: Delivery): Long

    @Query("UPDATE deliveries SET status = :status, actualDeliveryTime = :time WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Int, status: String, time: String)

    // --- DELIVERY PARTNER ---
    @Query("SELECT * FROM delivery_partners")
    fun getAllPartnersFlow(): Flow<List<DeliveryPartner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: DeliveryPartner): Long

    @Query("UPDATE delivery_partners SET todayEarnings = :earning WHERE id = :id")
    suspend fun updatePartnerEarnings(id: Int, earning: Double)

    // --- INVENTORY ---
    @Query("SELECT * FROM inventory ORDER BY recordDate DESC")
    fun getInventoryFlow(): Flow<List<InventoryRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryRecord(record: InventoryRecord): Long

    // --- NOTIFICATION ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long
}
