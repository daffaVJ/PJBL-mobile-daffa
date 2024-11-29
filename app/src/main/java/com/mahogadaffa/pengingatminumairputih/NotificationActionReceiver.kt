package com.mahogadaffa.pengingatminumairputih

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_SUDAH_MINUM" -> {
                val namaObat = intent.getStringExtra("namaPenginggat")
                // Tutup notifikasi
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NotificationReceiver.NOTIFICATION_ID)

                // Tampilkan konfirmasi
                Toast.makeText(
                    context,
                    "Berhasil mencatat: $namaObat sudah diminum",
                    Toast.LENGTH_SHORT
                ).show()

                // Di sini Anda bisa menambahkan kode untuk mencatat ke database
                // bahwa obat sudah diminum
            }
        }
    }
}