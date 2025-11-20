package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.outdoorsy.data.local.entity.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM location")
    fun getAll(): Flow<List<Location>>

    @Query("SELECT * FROM location WHERE name = :name")
    fun getByName(name: String): Flow<Location?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)

    @Query("DELETE FROM location WHERE name = :locationName")
    fun deleteByName(locationName: String)
}
