package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items ORDER BY aisle ASC, addedTimestamp DESC")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CartItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Update
    suspend fun updateItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM cart_items WHERE isChecked = 1")
    suspend fun clearCheckedItems()

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()
}

@Dao
interface SavedRecipeDao {
    @Query("SELECT recipeId FROM saved_recipes ORDER BY savedTimestamp DESC")
    fun getSavedRecipeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipe(item: SavedRecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE recipeId = :recipeId")
    suspend fun removeRecipe(recipeId: String)

    @Query("SELECT COUNT(*) FROM saved_recipes WHERE recipeId = :recipeId")
    suspend fun isRecipeSaved(recipeId: String): Int
}

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items")
    fun getAllPantryItems(): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItems(items: List<PantryItemEntity>)

    @Update
    suspend fun updatePantryItem(item: PantryItemEntity)
}
