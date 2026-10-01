package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val recipeId: String? = null,
    val recipeTitle: String? = null,
    val name: String,
    val amount: String,
    val aisle: String,
    val price: Double,
    val isChecked: Boolean = false,
    val supermarket: String = "שופרסל",
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey val recipeId: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val isAvailable: Boolean = true
)
