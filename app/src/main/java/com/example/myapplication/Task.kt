package com.example.myapplication

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Task(
    val titulo: String,
    val desc: String,
    val date: LocalDateTime
) : Parcelable