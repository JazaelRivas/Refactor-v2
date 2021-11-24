package com.example.myapplication.presentation.main

import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.myapplication.presentation.createTask.CreateTaskActivity
import com.example.myapplication.R
import com.example.myapplication.data.db.Db
import com.example.myapplication.data.util.NotificationManager
import com.example.myapplication.data.util.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.ArrayList
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var taskRcv: RecyclerView
    private lateinit var createTaskBtn: FloatingActionButton
    private lateinit var adapter: TaskAdapter

    private val TASK = "taskList"
    private var taskList = mutableListOf<Task>()
    private lateinit var database: Db

    companion object {
        val NEW_TASK = 200
        val UPDATE_TASK = 201
        val NEW_TASK_KEY = "newtask"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            val savedTasks = it.getParcelableArrayList<Task>(TASK)?.toMutableList() ?: taskList
            taskList = savedTasks
        }

        createNotificationChannel()
        initViews()
        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        database = Room.databaseBuilder(this, Db::class.java, "TASKS_DB").build()
        MainScope().launch {
            taskList = database.taskDao().getTasks().toMutableList()
            setAdapter()
        }
    }

    private fun initViews() {
        createTaskBtn = findViewById(R.id.btn)
        taskRcv = findViewById(R.id.taskRcv)
        createTaskBtn.setOnClickListener {
            startActivityForResult(Intent(this, CreateTaskActivity::class.java), NEW_TASK)
        }
    }

    private fun setAdapter() {
        adapter = TaskAdapter(
            taskList,
            onCheck = {
                task, position ->
                MainScope().launch {
                    adapter.removeTask(position)
                    database.taskDao().updateTask(task.apply {
                        check = false
                    })
                }
            },
            context = this,
            onClick = {
                task ->
                startActivityForResult(Intent(
                    this,
                    CreateTaskActivity::class.java).apply {
                        putExtra("isTaskDetail", true)
                        putExtra("task", task)
                    }, UPDATE_TASK)
            }
        )

        taskRcv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        taskRcv.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelableArrayList(TASK, taskList as ArrayList<Task>)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == NEW_TASK ){
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {
                MainScope().launch(Dispatchers.IO) {
                    val id = database.taskDao().saveTask(it).toInt()

                    val zone = OffsetDateTime.now().offset
                    val selectedMillis = it.date.toInstant(zone)?.toEpochMilli() ?: 0
                    val nowMillis = LocalDateTime.now().toInstant(zone).toEpochMilli()

                    scheduleNotification(selectedMillis - nowMillis, Data.Builder().apply {
                        putString("notificationDate", it.date.toString())
                        putString("notificationDescription", it.desc)
                        putString("notificationTitle", it.titulo)
                        putInt("notificationID", id)
                    }.build())
                }
                MainScope().launch(Dispatchers.Main) {
                    adapter.add(it)
                }

            }
        } else {
            data?.getParcelableExtra<Task>(NEW_TASK_KEY)?.let {
                MainScope().launch(Dispatchers.Main) {
                    adapter.update(it)
                }
                MainScope().launch(Dispatchers.IO) {
                    database.taskDao().updateTask(it)
                }
            }
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "TASK"
            val descriptionText = "Channel of pending tasks"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: android.app.NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(delay: Long, data: Data){
        val notificationWork = OneTimeWorkRequest.Builder(NotificationManager::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()
        val instanceWorkManager = WorkManager.getInstance(this)
        instanceWorkManager.beginUniqueWork(
            "WORK_${data.getInt("notificationID", 0)}",
            ExistingWorkPolicy.APPEND_OR_REPLACE, notificationWork
        ).enqueue()
    }
}