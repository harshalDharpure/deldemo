package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Address::class,
        Product::class,
        Order::class,
        OrderItem::class,
        Subscription::class,
        Delivery::class,
        DeliveryPartner::class,
        InventoryRecord::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AndawalaDatabase : RoomDatabase() {

    abstract fun dao(): AndawalaDao

    companion object {
        @Volatile
        private var INSTANCE: AndawalaDatabase? = null

        fun getDatabase(context: Context): AndawalaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AndawalaDatabase::class.java,
                    "andawala_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
