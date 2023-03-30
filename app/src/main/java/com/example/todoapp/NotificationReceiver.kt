package com.example.todoapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationId = 1
const val channelId = "channel"
const val titleExtra = "title"
const val messageExtra = "message"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        //val i = Intent(context,MainActivity::class.java)
        //val pendingIntent = PendingIntent.getActivity(context,0,i ,0)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.calendar_24)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setAutoCancel(true)
            .build()

        //.setContentIntent(pendingIntent)
        //.setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId,notification)
    }
}