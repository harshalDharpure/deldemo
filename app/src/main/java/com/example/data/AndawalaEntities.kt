package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phone: String,
    val name: String,
    val emil: String = "",
    val role: String = "CUSTOMER", // CUSTOMER, DELIVERY, ADMIN
    val walletBalance: Double = 150.0,
    val registeredDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val doorNo: String,
    val societyName: String, // Apartment/Society-wise routing is key in Bangalore
    val landmarks: String = "",
    val pinCode: String = "560001",
    val latitude: Double = 12.9716, // Bangalore Center
    val longitude: Double = 77.5946
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val nameKannada: String,
    val category: String, // EGGS, ADDONS, COMBOS
    val description: String,
    val price: Double,
    val imagePlaceholderRes: String = "ic_egg"
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val orderDate: Long = System.currentTimeMillis(),
    val deliverySlot: String = "Morning (6-9 AM)", // Bangalore morning/evening focus
    val totalAmount: Double,
    val paymentMethod: String = "Cash on Delivery", // COD, UPI, WALLET
    val paymentStatus: String = "Pending", // Pending, Paid, Refunded
    val orderStatus: String = "Order Placed", // Order Placed, Confirmed, Preparing, Out For Delivery, Delivered, Cancelled
    val societyName: String = ""
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Double
)

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val planName: String, // Daily, Weekly, Monthly
    val quantityPerDay: Int, // 2, 4, 6 eggs
    val deliverySlot: String = "Morning (6-9 AM)",
    val societyName: String,
    val autoRenew: Boolean = true,
    val status: String = "Active", // Active, Paused, Cancelled
    val startDate: Long = System.currentTimeMillis(),
    val isVacationMode: Boolean = false
)

@Entity(tableName = "deliveries")
data class Delivery(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int = 0,
    val subscriptionId: Int = 0,
    val deliveryDate: String, // YYYY-MM-DD
    val targetType: String, // ORDER, SUBSCRIPTION
    val partnerId: Int = 1,
    val customerName: String,
    val phone: String,
    val societyName: String,
    val doorNo: String,
    val deliverySlot: String,
    val status: String = "Assigned", // Assigned, Completed, Customer Unavailable
    val actualDeliveryTime: String = "",
    val refundAmount: Double = 0.0
)

@Entity(tableName = "delivery_partners")
data class DeliveryPartner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val avatarUrl: String = "",
    val bikeNo: String = "KA-03-EG-1234",
    val rating: Double = 4.8,
    val todayEarnings: Double = 0.0
)

@Entity(tableName = "inventory")
data class InventoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recordDate: String, // YYYY-MM-DD
    val rawEggsPurchased: Int = 500,
    val eggsBoiled: Int = 450,
    val eggsWasted: Int = 10,
    val remainingStock: Int = 40
)

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
