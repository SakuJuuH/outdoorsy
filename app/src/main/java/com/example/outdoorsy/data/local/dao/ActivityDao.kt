package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.outdoorsy.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activity WHERE id = :id")
    fun getById(id: Int): Flow<ActivityEntity>

    @Query("SELECT * FROM activity WHERE name = :name")
    fun getByName(name: String): Flow<ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activity WHERE name = :name")
    fun deleteActivityByName(name: String)
}
