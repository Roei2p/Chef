package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChefRecipe
import com.example.data.model.RecipeCategory
import com.example.ui.components.ChefTipCard
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.RecipeCardItem

@Composable
fun ChefRecommendationsScreen(
    recipes: List<ChefRecipe>,
    savedRecipeIds: List<String>,
    budgetLimit: Double,
    selectedCategory: RecipeCategory?,
    onSelectCategory: (RecipeCategory?) -> Unit,
    onRecipeClick: (ChefRecipe) -> Unit,
    onAddToCart: (ChefRecipe) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onOpenBudgetTuning: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("chef_recommendations_screen"),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Editorial Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Smart culinary match pill badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 1.dp,
                    modifier = Modifier.testTag("smart_match_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "התאמה קולינרית חכמה בזמן אמת",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Main Heading
                Text(
                    text = "התאמנו עבורכם ${recipes.size} מנות שף מושלמות",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("recommendations_headline")
                )

                // Supermarket Budget subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "במחיר שופרסל מדויק לתקציב של ₪${budgetLimit.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Chef Pro Tip Card
        item {
            ChefTipCard(
                onCardClick = onOpenBudgetTuning,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Filter Chips Row
        item {
            FilterChipsRow(
                selectedCategory = selectedCategory,
                onSelectCategory = onSelectCategory,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Recipe Cards List
        if (recipes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "לא נמצאו מנות תואמות לתקציב או לסינון הנוכחי",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recipes, key = { it.id }) { recipe ->
                val isFav = savedRecipeIds.contains(recipe.id)
                RecipeCardItem(
                    recipe = recipe,
                    isFavorite = isFav,
                    onRecipeClick = { onRecipeClick(recipe) },
                    onAddToCartClick = { onAddToCart(recipe) },
                    onToggleFavorite = { onToggleFavorite(recipe.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
