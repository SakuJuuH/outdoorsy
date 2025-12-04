package com.example.outdoorsy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.TemperatureSystem
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var repository: SettingsRepository
    private lateinit var testDataStore: DataStore<Preferences>

    @Test
    fun `saveLanguage persists correct value`() = runTest {
        createRepository(testScope = this)

        repository.getLanguage().test {
            assertEquals(AppLanguage.ENGLISH.code, awaitItem())

            repository.saveLanguage(AppLanguage.FINNISH.code)

            assertEquals(AppLanguage.FINNISH.code, awaitItem())
        }
    }

    @Test
    fun `saveAppTheme persists correct value`() = runTest {
        createRepository(testScope = this)

        repository.getAppTheme().test {
            assertEquals(AppTheme.SYSTEM.code, awaitItem())

            repository.saveAppTheme(AppTheme.DARK.code)

            assertEquals(AppTheme.DARK.code, awaitItem())
        }
    }

    @Test
    fun `saveCurrency persists correct value`() = runTest {
        createRepository(testScope = this)
        repository.getCurrency().test {
            assertEquals(Currencies.GBP.code, awaitItem())

            repository.saveCurrency(Currencies.EUR.code)

            assertEquals(Currencies.EUR.code, awaitItem())
        }
    }

    @Test
    fun `saveTemperature persists correct value`() = runTest {
        createRepository(testScope = this)

        repository.getTemperatureUnit().test {
            assertEquals(TemperatureSystem.METRIC.code, awaitItem())

            repository.saveTemperatureUnit(TemperatureSystem.IMPERIAL.code)

            assertEquals(TemperatureSystem.IMPERIAL.code, awaitItem())
        }
    }

    @Test
    fun `recent searches flow emits updates as items are added`() = runTest {
        createRepository(testScope = this)

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

    private fun createRepository(testScope: TestScope) {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope + Job(),
            produceFile = {
                File(tmpFolder.newFolder(), "test_settings.preferences_pb")
            }
        )
        repository = SettingsRepository(testDataStore)
    }


}