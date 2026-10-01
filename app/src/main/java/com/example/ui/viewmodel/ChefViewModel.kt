package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CartItemEntity
import com.example.data.local.ChefMarketDatabase
import com.example.data.model.ChefRecipe
import com.example.data.model.RecipeCategory
import com.example.data.repository.ChefRepository
import com.example.ui.timer.CookingTimerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val titleHe: String) {
    RECOMMENDATIONS("הצעות שף"),
    MEAL_BUILDER("בניית מנה"),
    GROCERY_CART("סל קניות"),
    PROFILE("פרופיל ושמורים")
}

data class ChefUiState(
    val currentTab: AppTab = AppTab.RECOMMENDATIONS,
    val selectedCategory: RecipeCategory? = RecipeCategory.BEST_VALUE,
    val searchQuery: String = "",
    val budgetLimit: Double = 85.0,
    val dinersCount: Int = 2,
    val selectedCuisine: String = "הכל",
    val selectedDietary: String = "הכל",
    val availablePantryStaples: Set<String> = setOf("שמן זית", "מלח ים", "פלפל שחור", "שיני שום"),
    val selectedRecipe: ChefRecipe? = null,
    val isCookingModeActive: Boolean = false,
    val currentCookingStepIndex: Int = 0,
    val activeSupermarket: String = "שופרסל"
)

class ChefViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChefMarketDatabase.getInstance(application)
    private val repository = ChefRepository(
        cartDao = db.cartDao(),
        savedRecipeDao = db.savedRecipeDao(),
        pantryDao = db.pantryDao()
    )

    val timerManager = CookingTimerManager(application, viewModelScope)

    private val _uiState = MutableStateFlow(ChefUiState())
    val uiState: StateFlow<ChefUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    val cartItems: StateFlow<List<CartItemEntity>> = repository.allCartItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedRecipeIds: StateFlow<List<String>> = repository.savedRecipeIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredRecipes: StateFlow<List<ChefRecipe>> = combine(
        _uiState,
        savedRecipeIds
    ) { state, _ ->
        var list = repository.getAllRecipes()

        // Filter category
        if (state.selectedCategory != null) {
            list = list.filter { it.category == state.selectedCategory }
        }

        // Search filter
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.dietaryBadge.lowercase().contains(q)
            }
        }

        // Budget check
        list = list.filter { it.priceSupermarket <= state.budgetLimit + 5.0 }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getAllRecipes()
    )

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectCategory(category: RecipeCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setBudgetLimit(budget: Double) {
        _uiState.value = _uiState.value.copy(budgetLimit = budget)
    }

    fun setDinersCount(diners: Int) {
        _uiState.value = _uiState.value.copy(dinersCount = diners)
    }

    fun setCuisine(cuisine: String) {
        _uiState.value = _uiState.value.copy(selectedCuisine = cuisine)
    }

    fun setDietary(dietary: String) {
        _uiState.value = _uiState.value.copy(selectedDietary = dietary)
    }

    fun togglePantryStaple(staple: String) {
        val current = _uiState.value.availablePantryStaples.toMutableSet()
        if (current.contains(staple)) {
            current.remove(staple)
        } else {
            current.add(staple)
        }
        _uiState.value = _uiState.value.copy(availablePantryStaples = current)
    }

    fun openRecipeDetail(recipe: ChefRecipe) {
        _uiState.value = _uiState.value.copy(
            selectedRecipe = recipe,
            isCookingModeActive = false,
            currentCookingStepIndex = 0
        )
    }

    fun closeRecipeDetail() {
        _uiState.value = _uiState.value.copy(
            selectedRecipe = null,
            isCookingModeActive = false,
            currentCookingStepIndex = 0
        )
    }

    fun startCookingMode() {
        _uiState.value = _uiState.value.copy(
            isCookingModeActive = true,
            currentCookingStepIndex = 0
        )
    }

    fun setCookingStep(stepIndex: Int) {
        val total = _uiState.value.selectedRecipe?.steps?.size ?: 1
        if (stepIndex in 0 until total) {
            _uiState.value = _uiState.value.copy(currentCookingStepIndex = stepIndex)
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId)
        }
    }

    fun addRecipeToCart(recipe: ChefRecipe) {
        viewModelScope.launch {
            repository.addIngredientsToCart(recipe)
            _snackbarEvent.emit("מצרכי ${recipe.shortTitle} הועברו לסל שופרסל בהצלחה!")
        }
    }

    fun toggleCartItemChecked(item: CartItemEntity) {
        viewModelScope.launch {
            repository.toggleCartItemChecked(item)
        }
    }

    fun deleteCartItem(id: String) {
        viewModelScope.launch {
            repository.deleteCartItem(id)
        }
    }

    fun clearCheckedCartItems() {
        viewModelScope.launch {
            repository.clearCheckedCartItems()
            _snackbarEvent.emit("מצרכים שנרכשו נוקו מהסל")
        }
    }

    fun clearAllCartItems() {
        viewModelScope.launch {
            repository.clearAllCartItems()
            _snackbarEvent.emit("סל הקניות אופס")
        }
    }

    fun addCustomCartItem(name: String, price: Double, aisle: String) {
        viewModelScope.launch {
            repository.addCustomCartItem(name, price, aisle)
            _snackbarEvent.emit("נוסף מצרך חדש לסל: $name")
        }
    }

    fun selectSupermarket(name: String) {
        _uiState.value = _uiState.value.copy(activeSupermarket = name)
    }
}
