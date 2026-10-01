package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.ChefRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ChefMarket", appName)
    }

    @Test
    fun `verify sample chef recipes loaded and match target budget`() {
        val recipes = ChefRepository.sampleRecipes
        assertTrue("Should have multiple recipes", recipes.size >= 3)

        val salmon = recipes.find { it.id == "salmon-pan-seared" }
        assertNotNull("Salmon recipe should exist", salmon)
        assertTrue("Short title should contain salmon", salmon!!.shortTitle.contains("סלמון"))
        assertTrue("Price should be within budget of 85", salmon.priceSupermarket <= 85.0)
        assertTrue("Ingredients should be present", salmon.ingredients.isNotEmpty())
        assertTrue("Cooking steps should be present", salmon.steps.isNotEmpty())
    }
}
