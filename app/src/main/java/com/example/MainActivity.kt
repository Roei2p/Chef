package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BudgetTuningSheet
import com.example.ui.components.ChefBottomNavBar
import com.example.ui.components.ChefTopBar
import com.example.ui.components.SmartTimerFloatingBar
import com.example.ui.screens.ChefRecommendationsScreen
import com.example.ui.screens.GroceryCartScreen
import com.example.ui.screens.MealBuilderScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RecipeDetailModal
import com.example.ui.theme.ChefMarketTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.ChefViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: ChefViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ChefMarketTheme {
                    ChefMarketApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ChefMarketApp(
    viewModel: ChefViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val savedRecipeIds by viewModel.savedRecipeIds.collectAsStateWithLifecycle()
    val timerState by viewModel.timerManager.timerState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showBudgetTuningSheet by remember { mutableStateOf(false) }

    // Listen for snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware back button behavior
    BackHandler(enabled = uiState.selectedRecipe != null || uiState.currentTab != AppTab.RECOMMENDATIONS) {
        if (uiState.selectedRecipe != null) {
            viewModel.closeRecipeDetail()
        } else {
            viewModel.setTab(AppTab.RECOMMENDATIONS)
        }
    }

    Scaffold(
        topBar = {
            ChefTopBar(
                searchQuery = uiState.searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onTuneClick = { showBudgetTuningSheet = true },
                onAvatarClick = { viewModel.setTab(AppTab.PROFILE) }
            )
        },
        bottomBar = {
            ChefBottomNavBar(
                currentTab = uiState.currentTab,
                cartItemCount = cartItems.size,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content depending on Tab
            Crossfade(
                targetState = uiState.currentTab,
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    AppTab.RECOMMENDATIONS -> {
                        ChefRecommendationsScreen(
                            recipes = recipes,
                            savedRecipeIds = savedRecipeIds,
                            budgetLimit = uiState.budgetLimit,
                            selectedCategory = uiState.selectedCategory,
                            onSelectCategory = { viewModel.selectCategory(it) },
                            onRecipeClick = { viewModel.openRecipeDetail(it) },
                            onAddToCart = { viewModel.addRecipeToCart(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onOpenBudgetTuning = { showBudgetTuningSheet = true }
                        )
                    }

                    AppTab.MEAL_BUILDER -> {
                        MealBuilderScreen(
                            budgetLimit = uiState.budgetLimit,
                            dinersCount = uiState.dinersCount,
                            selectedCuisine = uiState.selectedCuisine,
                            selectedDietary = uiState.selectedDietary,
                            pantryStaples = uiState.availablePantryStaples,
                            matchingRecipes = recipes,
                            savedRecipeIds = savedRecipeIds,
                            onBudgetChange = { viewModel.setBudgetLimit(it) },
                            onDinersChange = { viewModel.setDinersCount(it) },
                            onCuisineChange = { viewModel.setCuisine(it) },
                            onDietaryChange = { viewModel.setDietary(it) },
                            onTogglePantryStaple = { viewModel.togglePantryStaple(it) },
                            onRecipeClick = { viewModel.openRecipeDetail(it) },
                            onAddToCart = { viewModel.addRecipeToCart(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                    }

                    AppTab.GROCERY_CART -> {
                        GroceryCartScreen(
                            cartItems = cartItems,
                            budgetLimit = uiState.budgetLimit,
                            activeSupermarket = uiState.activeSupermarket,
                            onSelectSupermarket = { viewModel.selectSupermarket(it) },
                            onToggleChecked = { viewModel.toggleCartItemChecked(it) },
                            onDeleteItem = { viewModel.deleteCartItem(it) },
                            onClearChecked = { viewModel.clearCheckedCartItems() },
                            onClearAll = { viewModel.clearAllCartItems() },
                            onAddCustomItem = { name, price, aisle -> viewModel.addCustomCartItem(name, price, aisle) }
                        )
                    }

                    AppTab.PROFILE -> {
                        val savedList = recipes.filter { savedRecipeIds.contains(it.id) }
                        ProfileScreen(
                            savedRecipes = savedList,
                            savedRecipeIds = savedRecipeIds,
                            onRecipeClick = { viewModel.openRecipeDetail(it) },
                            onAddToCart = { viewModel.addRecipeToCart(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onStartTimer = { title, seconds ->
                                viewModel.timerManager.startTimer(title, seconds)
                            }
                        )
                    }
                }
            }

            // Floating Smart Timer Bar (Visible when timer is active)
            SmartTimerFloatingBar(
                timerState = timerState,
                onTogglePlayPause = {
                    if (timerState.isRunning) viewModel.timerManager.pauseTimer()
                    else viewModel.timerManager.resumeTimer()
                },
                onAddMinute = { viewModel.timerManager.addSeconds(60) },
                onReset = { viewModel.timerManager.resetTimer() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Recipe Detail Modal
    uiState.selectedRecipe?.let { recipe ->
        val isFav = savedRecipeIds.contains(recipe.id)
        RecipeDetailModal(
            recipe = recipe,
            isFavorite = isFav,
            onDismiss = { viewModel.closeRecipeDetail() },
            onAddToCart = { viewModel.addRecipeToCart(it) },
            onToggleFavorite = { viewModel.toggleFavorite(recipe.id) },
            onStartStepTimer = { title, seconds ->
                viewModel.timerManager.startTimer(title, seconds, recipe.shortTitle)
            }
        )
    }

    // Budget & Preferences Tuning Bottom Sheet
    if (showBudgetTuningSheet) {
        BudgetTuningSheet(
            currentBudget = uiState.budgetLimit,
            currentDiners = uiState.dinersCount,
            currentSupermarket = uiState.activeSupermarket,
            onBudgetChange = { viewModel.setBudgetLimit(it) },
            onDinersChange = { viewModel.setDinersCount(it) },
            onSupermarketChange = { viewModel.selectSupermarket(it) },
            onDismiss = { showBudgetTuningSheet = false }
        )
    }
}
