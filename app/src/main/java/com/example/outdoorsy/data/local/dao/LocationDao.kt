package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.outdoorsy.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM location WHERE name = :name")
    fun getByName(name: String): Flow<LocationEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location: LocationEntity)

    @Delete
    fun deleteLocation(location: LocationEntity)

    @Query("DELETE FROM location WHERE name = :locationName")
    fun deleteByName(locationName: String)
}
