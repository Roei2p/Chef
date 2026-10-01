package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        SavedRecipeEntity::class,
        PantryItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChefMarketDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun savedRecipeDao(): SavedRecipeDao
    abstract fun pantryDao(): PantryDao

    companion object {
        @Volatile
        private var INSTANCE: ChefMarketDatabase? = null

        fun getInstance(context: Context): ChefMarketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChefMarketDatabase::class.java,
                    "chefmarket_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
