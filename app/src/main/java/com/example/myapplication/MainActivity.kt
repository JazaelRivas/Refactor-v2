package com.example.myapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.util.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var taskRcv: RecyclerView
    private lateinit var createTaskBtn: FloatingActionButton
    private lateinit var adapter: TaskAdapter

    private val TASK = "taskList"
    private var taskList = mutableListOf(
        Task("Juagr lol", "Juagr lol para subir de liga", LocalDateTime.now()),
        Task("Android", "Hacer tarea para la clase", LocalDateTime.now())
    )

    companion object {
        val NEW_TASK = 200
        val NEW_TASK_KEY = "newtask"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            val savedTasks = it.getParcelableArrayList<Task>(TASK)?.toMutableList() ?: taskList
            taskList = savedTasks
        }

        initViews()
        setAdapter()
    }

    private fun initViews() {
        createTaskBtn = findViewById(R.id.btn)
        taskRcv = findViewById(R.id.taskRcv)


        createTaskBtn.setOnClickListener {
            startActivityForResult(Intent(this, CreateTaskActivity::class.java), NEW_TASK)
        }
    }

    private fun setAdapter() {
        adapter = TaskAdapter(taskList)

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
                adapter.add(it)
            }
        }
    }
}