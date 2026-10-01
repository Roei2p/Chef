package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.RecipeCategory

data class FilterChipItem(
    val category: RecipeCategory?,
    val title: String,
    val icon: ImageVector,
    val iconColor: Color
)

@Composable
fun FilterChipsRow(
    selectedCategory: RecipeCategory?,
    onSelectCategory: (RecipeCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val chips = listOf(
        FilterChipItem(
            category = RecipeCategory.BEST_VALUE,
            title = "הכי משתלם",
            icon = Icons.Default.Stars,
            iconColor = Color(0xFFFFB300)
        ),
        FilterChipItem(
            category = RecipeCategory.FAST_PREP,
            title = "מהיר (עד 25 דק׳)",
            icon = Icons.Default.Bolt,
            iconColor = Color(0xFF805200)
        ),
        FilterChipItem(
            category = RecipeCategory.GOURMET,
            title = "יוקרתי",
            icon = Icons.Default.DinnerDining,
            iconColor = MaterialTheme.colorScheme.primary
        ),
        FilterChipItem(
            category = RecipeCategory.HEALTHY,
            title = "בריא וקל",
            icon = Icons.Default.Spa,
            iconColor = Color(0xFF376847)
        )
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            val isSelected = selectedCategory == chip.category

            Surface(
                shape = RoundedCornerShape(50),
                color = if (isSelected) Color(0xFF2C322E) else MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = if (isSelected) 2.dp else 1.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        // Toggle or select
                        if (isSelected) onSelectCategory(null) else onSelectCategory(chip.category)
                    }
                    .testTag("filter_chip_${chip.category?.name ?: "all"}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Icon(
                        imageVector = chip.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else chip.iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = chip.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
