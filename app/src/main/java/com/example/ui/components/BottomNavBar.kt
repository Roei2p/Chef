package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.AppTab

@Composable
fun ChefBottomNavBar(
    currentTab: AppTab,
    cartItemCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("chef_bottom_nav_bar")
    ) {
        // Tab 1: Meal Builder
        NavigationBarItem(
            selected = currentTab == AppTab.MEAL_BUILDER,
            onClick = { onTabSelected(AppTab.MEAL_BUILDER) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.MEAL_BUILDER) Icons.Filled.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                    contentDescription = "בניית מנה"
                )
            },
            label = {
                Text(
                    text = "בניית מנה",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == AppTab.MEAL_BUILDER) FontWeight.Bold else FontWeight.Medium
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_meal_builder")
        )

        // Tab 2: Chef Recommendations (Home)
        NavigationBarItem(
            selected = currentTab == AppTab.RECOMMENDATIONS,
            onClick = { onTabSelected(AppTab.RECOMMENDATIONS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.RECOMMENDATIONS) Icons.Filled.LocalDining else Icons.Outlined.LocalDining,
                    contentDescription = "הצעות שף"
                )
            },
            label = {
                Text(
                    text = "הצעות שף",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == AppTab.RECOMMENDATIONS) FontWeight.Bold else FontWeight.Medium
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_recommendations")
        )

        // Tab 3: Grocery Cart
        NavigationBarItem(
            selected = currentTab == AppTab.GROCERY_CART,
            onClick = { onTabSelected(AppTab.GROCERY_CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(text = "$cartItemCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == AppTab.GROCERY_CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                        contentDescription = "סל קניות"
                    )
                }
            },
            label = {
                Text(
                    text = "סל קניות",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == AppTab.GROCERY_CART) FontWeight.Bold else FontWeight.Medium
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_grocery_cart")
        )

        // Tab 4: Profile & Saved
        NavigationBarItem(
            selected = currentTab == AppTab.PROFILE,
            onClick = { onTabSelected(AppTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == AppTab.PROFILE) Icons.Filled.BookmarkBorder else Icons.Outlined.BookmarkBorder,
                    contentDescription = "פרופיל ושמורים"
                )
            },
            label = {
                Text(
                    text = "פרופיל ושמורים",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (currentTab == AppTab.PROFILE) FontWeight.Bold else FontWeight.Medium
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}
