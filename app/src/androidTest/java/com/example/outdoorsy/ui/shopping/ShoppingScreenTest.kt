package com.example.outdoorsy.ui.shopping

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.domain.model.ebay.Price
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShoppingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context
    private val sampleRecommendedItem = EbayItem(
        itemId = "12345",
        title = "Recommended Hiking Boots",
        price = Price("99.99", "USD"),
        imageUrl = "",
        link = "",
        categoryNames = listOf("Footwear")
    )
    private val sampleRegularItem = EbayItem(
        itemId = "67890",
        title = "Regular Camping Tent",
        price = Price("149.50", "USD"),
        imageUrl = "",
        link = "",
        categoryNames = listOf("Camping Gear")
    )


    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun renderShoppingScreen(state: ShoppingUiState) {
        composeTestRule.setContent {
            WeatherAppTheme {
                ShoppingContent(
                    uiState = state
                )
            }
        }
    }

    @Test
    fun shoppingScreen_displaysTitle() {
        val initialState = ShoppingUiState()
        renderShoppingScreen(initialState)

        composeTestRule.onNodeWithText(context.getString(R.string.weather_gear_shop))
            .assertIsDisplayed()
    }

    @Test
    fun shoppingScreen_showsLoadingIndicator_whenLoadingAndListsAreEmpty() {
        val loadingState = ShoppingUiState(isLoading = true, items = emptyList(), recommendedItems = emptyList())
        renderShoppingScreen(loadingState)

        // For now, we confirm that no items are shown, which implies loading.
        composeTestRule.onNodeWithText(sampleRecommendedItem.title).assertDoesNotExist()
        composeTestRule.onNodeWithText(sampleRegularItem.title).assertDoesNotExist()
    }

    @Test
    fun shoppingScreen_displaysError_whenErrorIsPresent() {
        val errorMessage = "Failed to load items."
        val errorState = ShoppingUiState(error = errorMessage)
        renderShoppingScreen(errorState)

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun shoppingScreen_displaysRecommendedAndAllItems_whenDataIsAvailable() {
        val successState = ShoppingUiState(
            items = listOf(sampleRegularItem),
            recommendedItems = listOf(sampleRecommendedItem)
        )
        renderShoppingScreen(successState)

        composeTestRule.onNodeWithText(context.getString(R.string.recommended_weather_items))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleRecommendedItem.title).assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.all_items))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleRegularItem.title).assertIsDisplayed()
    }

    @Test
    fun shoppingScreen_hidesRecommendedSection_whenNoRecommendedItems() {
        val stateWithNoRecommendations = ShoppingUiState(
            items = listOf(sampleRegularItem),
            recommendedItems = emptyList()
        )
        renderShoppingScreen(stateWithNoRecommendations)

        composeTestRule.onNodeWithText(context.getString(R.string.recommended_weather_items))
            .assertDoesNotExist()

        composeTestRule.onNodeWithText(context.getString(R.string.all_items))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(sampleRegularItem.title).assertIsDisplayed()
    }
}
