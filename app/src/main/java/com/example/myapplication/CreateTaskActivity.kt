package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myapplication.MainActivity.Companion.NEW_TASK
import com.example.myapplication.MainActivity.Companion.NEW_TASK_KEY
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var Title: TextInputEditText
    private lateinit var Description: TextInputEditText
    private lateinit var Date: TextInputEditText
    private lateinit var Time: TextInputEditText
    private lateinit var CreateBtn: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        Description = findViewById(R.id.desc)
        Title = findViewById(R.id.title)
        Time = findViewById(R.id.time)
        CreateBtn = findViewById(R.id.task)
        Date = findViewById(R.id.date)

        Time.setOnClickListener {
            val nowTime = LocalTime.now()
            TimePickerDialog(
                this,
                { _, h, m ->
                    var nh = ""
                    var nm = ""
                    if ( m in 0..9 && h in 0..9 ) {
                        nm = "0$m"
                        nh = "0$h"
                    } else if ( m in 0..9 ) {
                        nm = "0$m"
                        nh = "$h"
                    } else if ( h in 0..9 ) {
                        nh = "0$h"
                        nm = "$m"
                    } else {
                        nh = "$h"
                        nm = "$m"
                    }
                    Time.setText("$nh:$nm")
                },
                nowTime.hour,
                nowTime.minute,
                true
            ).show()
        }

        CreateBtn.setOnClickListener {
            if (Title.text.toString() == "" || Time.text.toString() == "" || Description.text.toString() == "" || Date.text.toString() == "")
                return@setOnClickListener
            else {
                setResult(
                    NEW_TASK,
                    Intent().putExtra(
                        NEW_TASK_KEY,
                        Task(
                            Title.text.toString(),
                            Description.text.toString(),
                            LocalDateTime.of(
                                LocalDate.parse(
                                    Date.text,
                                    DateTimeFormatter.ofPattern("yyyy/MM/dd")
                                ),
                                LocalTime.parse(
                                    Time.text,
                                    DateTimeFormatter.ofPattern("HH:mm"))
                            )
                        )
                    )
                )
                finish()
            }
        }

        Date.setOnClickListener {
            val actualDate = LocalDate.now()
            val dialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    Date.setText("$year/$month/$day")
                },
                actualDate.year,
                actualDate.monthValue - 1,
                actualDate.dayOfMonth
            )


            dialog.datePicker.minDate = System.currentTimeMillis() - 10000
            dialog.show()
        }


    }
}