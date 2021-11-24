package com.example.myapplication.data.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.R
import com.example.myapplication.presentation.createTask.CreateTaskActivity
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class NotificationManager(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params)  {

    fun createNotification(task: Task){
        val pending: PendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, CreateTaskActivity::class.java).apply {
                putExtra("task", task)
                putExtra("isTaskDetail", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "TASK_CHANNEL")
            .setSmallIcon(R.drawable.task)
            .setContentTitle(task.titulo)
            .setContentText(task.desc)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    1,
                    Intent(context, CreateTaskActivity::class.java).apply {
                        putExtra("task", task)
                        putExtra("isTaskDetail", true)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)){
            notify(task.id, builder.build())
        }
    }

    override fun doWork(): Result {
        val id = inputData.getInt("notificationID", 0)
        val date = LocalDateTime.parse(inputData.getString("notificationDate")) ?: LocalDateTime.now()
        val description = inputData.getString("notificationDescription") ?: ""
        val title = inputData.getString("notificationTitle") ?: ""

        createNotification(Task(id, title, description, date))

        return Result.success()
    }

}