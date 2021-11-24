package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.util.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task WHERE `check` = 1")
    suspend fun getTasks(): List<Task>

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTask(task: Task): Long


}