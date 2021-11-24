package com.example.myapplication

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

class TaskAdapter(val list: MutableList<Task>) :  RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_list, parent, false))

    fun add(task: Task) {
        list.add(task)
        notifyItemInserted(list.size -1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
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
                list.removeAt(position)
                notifyItemRemoved(position)

                // * Avisa que todos las posiciones cambiaros desde el que se elimino
                notifyItemRangeChanged(position, list.size)
            }

            rootView.setOnClickListener {

            }
        }

    }

}