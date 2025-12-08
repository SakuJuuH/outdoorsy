package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.EbayApiService
import com.example.outdoorsy.data.remote.EbayAuthService
import com.example.outdoorsy.data.remote.dto.ebay.EbayItemResponseDto
import com.example.outdoorsy.data.remote.dto.ebay.EbayTokenResponseDto
import com.example.outdoorsy.data.remote.dto.ebay.components.CategoryDto
import com.example.outdoorsy.data.remote.dto.ebay.components.ImageDto
import com.example.outdoorsy.data.remote.dto.ebay.components.ItemSummaryDto
import com.example.outdoorsy.data.remote.dto.ebay.components.PriceDto
import com.example.outdoorsy.di.EbayTokenHolder
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class EbayRepositoryImplTest {

    // 1. Mocks for all dependencies of the repository
    private lateinit var mockApiService: EbayApiService
    private lateinit var mockAuthService: EbayAuthService
    private lateinit var mockTokenHolder: EbayTokenHolder
    private lateinit var repository: EbayRepositoryImpl

    // We also need to mock the Lazy wrappers
    private lateinit var lazyApiService: Lazy<EbayApiService>
    private lateinit var lazyAuthService: Lazy<EbayAuthService>

    @Before
    fun setup() {
        // Initialize mocks before each test
        mockApiService = mockk()
        mockAuthService = mockk()
        lazyApiService = mockk()
        lazyAuthService = mockk()
        mockTokenHolder = mockk(relaxed = true)

        // When .get() is called on the lazy mock, return our actual service mock
        every { lazyApiService.get() } returns mockApiService
        every { lazyAuthService.get() } returns mockAuthService

        repository = EbayRepositoryImpl(lazyApiService, lazyAuthService, mockTokenHolder)
    }

    @Test
    fun `getItems fetches new token when current token is invalid`() = runTest {
        // --- Arrange ---
        every { mockTokenHolder.isValid() } returns false
        coEvery { mockAuthService.getToken() } returns EbayTokenResponseDto(
            accessToken = "new-dummy-token",
            expiresIn = 3600,
            tokenType = "Application"
        )
        coEvery {
            mockApiService.getItems(any(), any(), any(), any(), any())
        } returns Response.success(EbayItemResponseDto(itemSummaries = emptyList()))

        // --- Act ---
        repository.getItems("hiking", 10)

        // --- Assert ---
        coVerify(exactly = 1) { mockAuthService.getToken() }
        coVerify(exactly = 1) { mockTokenHolder.updateToken("new-dummy-token", 3600) }
    }

    @Test
    fun `getItems does NOT fetch new token when current token is valid`() = runTest {
        // --- Arrange ---
        every { mockTokenHolder.isValid() } returns true
        coEvery {
            mockApiService.getItems(any(), any(), any(), any(), any())
        } returns Response.success(EbayItemResponseDto(itemSummaries = emptyList()))

        // --- Act ---
        repository.getItems("hiking", 10)

        // --- Assert ---
        coVerify(exactly = 0) { mockAuthService.getToken() }
    }

    @Test
    fun `getItems returns mapped domain models on successful API response`() = runTest {
        // --- Arrange ---
        every { mockTokenHolder.isValid() } returns true

        val fakeApiResponse = EbayItemResponseDto(
            itemSummaries = listOf(
                ItemSummaryDto(
                    itemId = "123",
                    title = "Hiking Boots",
                    price = PriceDto("99.99", "USD"),
                    image = ImageDto("http://example.com/image.jpg"),
                    url = "http://example.com/item/123",
                    categories = listOf(CategoryDto("1", "Footwear")),
                    thumbnailImage = emptyList()
                )
            )
        )

        // Mock the API service call, now using specific matchers for clarity
        coEvery {
            mockApiService.getItems(query = "boots", limit = 1, filter = any())
        } returns Response.success(fakeApiResponse)

        // --- Act ---
        val result = repository.getItems("boots", 1)

        // --- Assert ---
        assertEquals(1, result.size)
        assertEquals("123", result.first().itemId)
        assertEquals("Hiking Boots", result.first().title)
        assertEquals("99.99", result.first().price.value)
        assertEquals("http://example.com/image.jpg", result.first().imageUrl)
    }

    @Test
    fun `getItems returns empty list on API failure`() = runTest {
        // --- Arrange ---
        every { mockTokenHolder.isValid() } returns true
        coEvery {
            mockApiService.getItems(any(), any(), any(), any(), any())
        } returns Response.error(404, mockk(relaxed = true))

        // --- Act ---
        val result = repository.getItems("query", 10)

        // --- Assert ---
        assertEquals(true, result.isEmpty())
    }
}
