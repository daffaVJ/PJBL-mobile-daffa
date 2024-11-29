package com.mahogadaffa.pengingatminumairputih

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class JadwalActivity : AppCompatActivity() {

    private lateinit var btnWaktu: Button
    private lateinit var btnTanggal: Button
    private lateinit var title: TextView
    private lateinit var jenisair: EditText
    private lateinit var btnSimpan: Button
    private lateinit var jumlahAirEditText: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var selectedTime: String
    private lateinit var selectedDate: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal)

        // Inisialisasi tampilan dan referensi Firebase
        btnWaktu = findViewById(R.id.btnWaktu)
        btnTanggal = findViewById(R.id.btnTanggal)
        btnSimpan = findViewById(R.id.btnSimpanJadwal)
        title=findViewById(R.id.titlejadwal)
        jenisair=findViewById(R.id.jenisAir)
        jumlahAirEditText = findViewById(R.id.jumlahAir)
        database = FirebaseDatabase.getInstance().getReference("airPutih")

//kondisi title berubah tambah / update
        if (intent.hasExtra("status") && intent.getStringExtra("status") == "update"){
            title.setText("Update Jadwal")

            jenisair.setText(intent.getStringExtra("jenisAir"))
            jumlahAirEditText.setText(intent.getStringExtra("jumlahAir"))
            btnSimpan.setText("update")
        }

        // Menambahkan listener untuk waktu dan tanggal
        btnWaktu.setOnClickListener { showTimePickerDialog() }
        btnTanggal.setOnClickListener { showDatePickerDialog() }
        btnSimpan.setOnClickListener { saveSchedule() }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            Toast.makeText(this, "Waktu: $selectedTime", Toast.LENGTH_SHORT).show()
        }, hour, minute, true).show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            Toast.makeText(this, "Tanggal: $selectedDate", Toast.LENGTH_SHORT).show()
        }, year, month, day).show()
    }

    private fun saveSchedule() {
        val jumlahAir = jumlahAirEditText.text.toString()

        val jenisAir = jenisair.text.toString()
        val statusPengingat = "belum di minum"

        if (jumlahAir.isEmpty() || selectedTime.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Silakan lengkapi semua data ", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahAirInt = jumlahAir.toIntOrNull()
        if (jumlahAirInt == null) {
            Toast.makeText(this, "Jumlah air harus berupa angka", Toast.LENGTH_SHORT).show()
            return
        }

        val id = database.push().key ?: return
        val pengingat = AirPutih(id, jenisAir, jumlahAir, selectedDate, selectedTime, statusPengingat, "minum air putih")

        database.child(id).setValue(pengingat).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Jadwal disimpan", Toast.LENGTH_SHORT).show()
                setAlarm(pengingat)
                startActivity(Intent(this, PengingatActivity::class.java))
            } else {
                Toast.makeText(this, "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAlarm(pengingat: AirPutih) {
        val calendar = Calendar.getInstance()

        // Atur kalender sesuai dengan tanggal dan waktu yang dipilih
        val (day, month, year) = pengingat.tanggal.split("/").map { it.toInt() }
        val (hour, minute) = pengingat.waktu.split(":").map { it.toInt() }
        calendar.set(year, month - 1, day, hour, minute, 0)

        val alarmIntent = Intent(this, NotificationReceiver::class.java).apply {
            action = "SHOW_NOTIFICATION"
            putExtra("namaObat", pengingat.namaPengingat)
            putExtra("dosisObat", pengingat.jumlahAir)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            pengingat.id.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
