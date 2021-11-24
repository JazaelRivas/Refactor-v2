package com.example.myapplication.presentation.main

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.myapplication.R
import com.example.myapplication.data.util.Task
import java.time.format.DateTimeFormatter

class TaskAdapter(
    val list: MutableList<Task>,
    val context: Context,
    val onCheck: (task: Task, position: Int) -> Unit,
    val onClick: (task: Task) -> Unit
) :  RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_list, parent, false))

    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size -1)
    }

    fun update(task: Task) {
        val index = list.indexOfFirst { it.id == task.id }
        list[index] = task
        notifyItemChanged(index)
    }

    fun removeTask(position: Int) {
        val task = list[position]
        list.removeAt(position)
        notifyItemRemoved(position)
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("WORK_${task.id}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() = list.size

    inner class TaskViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Task, position: Int) = view.apply {
            val title: TextView = findViewById(R.id.title)
            val dateTime: TextView = findViewById(R.id.date)
            val checkBox: CheckBox = findViewById(R.id.check)

            dateTime.text = data.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a"))
            title.text = data.titulo

            checkBox.setOnClickListener {
                onCheck(data, position)
            }

            rootView.setOnClickListener {
                onClick(data)
            }
        }

    }

}