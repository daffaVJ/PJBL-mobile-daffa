package com.mahogadaffa.pengingatminumairputih

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "medicine_reminder_channel"
        const val CHANNEL_NAME = "Pengingat air minum"
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "SHOW_NOTIFICATION" -> handleNotificationIntent(context, intent)
            "ACTION_SUDAH_MINUM" -> handleActionSudahMinum(context, intent)
        }
    }

    private fun handleNotificationIntent(context: Context, intent: Intent) {
        val namaObat = intent.getStringExtra("namaObat") ?: "Air Putih"
        val dosisObat = intent.getStringExtra("dosisObat") ?: ""

        createNotificationChannel(context)
        showNotification(context, namaObat, dosisObat)
    }

    private fun handleActionSudahMinum(context: Context, intent: Intent) {
        val namaObat = intent.getStringExtra("namaObat")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        Toast.makeText(context, "Berhasil mencatat: $namaObat sudah diminum", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel untuk pengingat minum obat"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, namaObat: String, dosisObat: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sudahMinumIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "ACTION_SUDAH_MINUM"
            putExtra("namaObat", namaObat)
        }

        val sudahMinumPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            sudahMinumIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Waktunya Minum AirPutih!")
            .setContentText("Saatnya minum $namaObat (Ml: $dosisObat)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.baseline_notifications_active_24,
                "Sudah Minum",
                sudahMinumPendingIntent
            )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}
