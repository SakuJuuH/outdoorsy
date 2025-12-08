package com.example.outdoorsy.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.TemperatureSystem
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var repository: SettingsRepositoryImpl
    private lateinit var testDataStore: DataStore<Preferences>

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_settings.preferences_pb") }
        )

        repository = SettingsRepositoryImpl(testDataStore)
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
        unmockkStatic(Log::class)
    }

    @Test
    fun `saveLanguage persists correct value`() = runTest {
        repository.getLanguage().test {
            assertEquals(AppLanguage.ENGLISH.code, awaitItem())

            repository.saveLanguage(AppLanguage.FINNISH.code)

            assertEquals(AppLanguage.FINNISH.code, awaitItem())
        }
    }

    @Test
    fun `saveAppTheme persists correct value`() = runTest {
        repository.getAppTheme().test {
            assertEquals(AppTheme.SYSTEM.code, awaitItem())

            repository.saveAppTheme(AppTheme.DARK.code)

            assertEquals(AppTheme.DARK.code, awaitItem())
        }
    }

    @Test
    fun `saveCurrency persists correct value`() = runTest {
        repository.getCurrency().test {
            assertEquals(Currencies.GBP.code, awaitItem())

            repository.saveCurrency(Currencies.EUR.code)

            assertEquals(Currencies.EUR.code, awaitItem())
        }
    }

    @Test
    fun `saveTemperature persists correct value`() = runTest {
        repository.getTemperatureUnit().test {
            assertEquals(TemperatureSystem.METRIC.code, awaitItem())

            repository.saveTemperatureUnit(TemperatureSystem.IMPERIAL.code)

            assertEquals(TemperatureSystem.IMPERIAL.code, awaitItem())
        }
    }

    @Test
    fun `recent searches flow emits updates as items are added`() = runTest {


        repository.getRecentSearches().test {
            assertEquals(emptyList<String>(), awaitItem())

            repository.addRecentSearch("Helsinki")
            val list1 = awaitItem()
            assertEquals(1, list1.size)
            assertEquals("Helsinki", list1[0])

            repository.addRecentSearch("London")
            val list2 = awaitItem()
            assertEquals(2, list2.size)
            assertEquals("London", list2[0])
            assertEquals("Helsinki", list2[1])
        }
    }

    @Test
    fun `getLanguage handles IOException by returning default value`() = runTest {
        val mockDataStore = mockk<DataStore<Preferences>>()
        every { mockDataStore.data } returns flow {
            throw IOException("Simulated disk read error")
        }

        val brokenRepository = SettingsRepositoryImpl(mockDataStore)

        brokenRepository.getLanguage().test {
            assertEquals(AppLanguage.ENGLISH.code, awaitItem())
            awaitComplete()
        }
    }

    @Test(expected = Exception::class)
    fun `getLanguage throws other exceptions`() = runTest {
        val mockDataStore = mockk<DataStore<Preferences>>()
        every { mockDataStore.data } returns flow {
            throw RuntimeException("Critical system failure")
        }

        val brokenRepository = SettingsRepositoryImpl(mockDataStore)

        brokenRepository.getLanguage().collect()
    }
}