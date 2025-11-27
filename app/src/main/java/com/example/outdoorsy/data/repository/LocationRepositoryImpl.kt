package com.example.outdoorsy.data.repository

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.outdoorsy.data.local.dao.LocationDao
import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.data.remote.dto.geocoding.toDomain
import com.example.outdoorsy.domain.model.Location
import com.example.outdoorsy.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application,
    private val locationDao: LocationDao,
    private val weatherApi: WeatherApiService
) : LocationRepository {
    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAccessCoarseLocationPermission && !hasAccessFineLocationPermission) {
            return null
        }

        return suspendCoroutine { continuation ->
            locationClient.lastLocation.addOnSuccessListener { location ->
                continuation.resume(
                    Location(
                        name = null,
                        country = null,
                        state = null,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            }.addOnFailureListener {
                continuation.resume(null)
            }.addOnCanceledListener {
                continuation.resume(null)
            }
        }
    }

    override suspend fun getCoordinatesByLocation(query: String): Location? = try {
        weatherApi.getCoordinatesByCity(query, limit = 1).firstOrNull()?.toDomain()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    override suspend fun getLocationByCoordinates(latitude: Double, longitude: Double): Location? =
        try {
            weatherApi.getCityByCoordinates(latitude, longitude, limit = 1).firstOrNull()
                ?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    override fun getAllLocations(): Flow<List<Location>> = locationDao.getAll().map { entityList ->
        entityList.map { entity -> entity.toDomain() }
    }

    override fun getLocationByName(name: String): Flow<Location?> =
        locationDao.getByName(name).map { entity ->
            entity?.toDomain()
        }

    override suspend fun saveLocation(location: Location) {
        locationDao.insertLocation(location.toEntity())
    }

    override suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location.toEntity())
    }

    override suspend fun deleteByName(locationName: String) {
        locationDao.deleteByName(locationName)
    }
}
