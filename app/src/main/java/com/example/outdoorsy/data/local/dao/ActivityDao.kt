package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.outdoorsy.data.local.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity")
    fun getAll(): Flow<List<Activity>>

    @Query("SELECT * FROM activity WHERE name = :name")
    fun getByName(name: String): Flow<Activity>

    @Query("SELECT * FROM activity WHERE id = :id")
    suspend fun getById(id: Int): Activity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertActivity(activity: Activity)

    @Delete
    fun deleteActivity(activity: Activity)
}
