package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.Location as LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationModel?

    suspend fun getCoordinatesByLocation(query: String): LocationModel?

    suspend fun getLocationByCoordinates(latitude: Double, longitude: Double): LocationModel?

    fun getAllLocations(): Flow<List<LocationModel>>

    fun getLocationByName(name: String): Flow<LocationModel?>

    suspend fun saveLocation(location: LocationModel)

    suspend fun deleteLocation(location: LocationModel)

    suspend fun deleteByName(locationName: String)
}
