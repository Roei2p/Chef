package com.example.data.model

data class RecipeIngredient(
    val id: String,
    val name: String,
    val amount: String,
    val aisle: String,
    val estimatedPrice: Double,
    val isPantryStaple: Boolean = false,
    val supermarketBrand: String = "שופרסל"
)

data class CookingStep(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val chefSecret: String? = null,
    val timerDurationSeconds: Int? = null,
    val flameLevel: String? = null
)

enum class RecipeCategory(val titleHe: String) {
    BEST_VALUE("הכי משתלם"),
    FAST_PREP("מהיר (עד 25 דק׳)"),
    GOURMET("יוקרתי"),
    HEALTHY("בריא וקל")
}

data class ChefRecipe(
    val id: String,
    val title: String,
    val shortTitle: String,
    val description: String,
    val imageUrl: String,
    val chefName: String = "שף ירון ברנר",
    val priceSupermarket: Double,
    val originalPrice: Double,
    val savingsBadge: String,
    val remainingBudget: Double,
    val timeMinutes: Int,
    val difficulty: String,
    val servings: Int,
    val category: RecipeCategory,
    val dietaryBadge: String,
    val isChefRecommended: Boolean = false,
    val supermarketIngredientsCount: Int,
    val pantryStaplesCount: Int,
    val chefTip: String,
    val restaurantPriceEstimate: Double = 180.0,
    val ingredients: List<RecipeIngredient>,
    val steps: List<CookingStep>
)
