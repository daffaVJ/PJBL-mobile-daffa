package com.mahogadaffa.pengingatminumairputih

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menghubungkan tombol-tombol dengan ID-nya
        val btnReminder: ImageButton = findViewById(R.id.btnReminder)
        val btnJadwal: ImageButton = findViewById(R.id.btnJadwal)
        val btnRiwayat: ImageButton = findViewById(R.id.btnRiwayat)
        val btnLogin: ImageButton = findViewById(R.id.Logout)

        // Menambahkan fungsi klik untuk setiap tombol
        btnReminder.setOnClickListener {
            val intent = Intent(this, PengingatActivity::class.java)
            startActivity(intent)
        }

        btnJadwal.setOnClickListener {
            val intent = Intent(this, JadwalActivity::class.java)
            startActivity(intent)
        }

        btnRiwayat.setOnClickListener {
            val intent = Intent(this, Riwayat_Activity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, Keluar_Activity::class.java)
            startActivity(intent)
        }

    }
}