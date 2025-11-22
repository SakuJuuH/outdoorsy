package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.outdoorsy.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_log")
    fun getAll(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertActivityLog(activityLog: ActivityLogEntity)

    @Delete
    fun deleteActivityLog(activityLog: ActivityLogEntity)
}