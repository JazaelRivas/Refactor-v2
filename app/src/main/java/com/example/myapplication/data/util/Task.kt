package com.example.myapplication.data.util

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity
@RequiresApi(Build.VERSION_CODES.O)
class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String = "",
    val desc: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    var check: Boolean = true
) : Parcelable