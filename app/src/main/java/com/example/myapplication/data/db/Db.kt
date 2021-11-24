package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.util.Task
import com.example.myapplication.data.dao.TaskDao
import com.example.myapplication.data.util.LocalDateTimeConverter

@Database(entities = [Task::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class Db : RoomDatabase() {

    abstract fun taskDao() : TaskDao

}