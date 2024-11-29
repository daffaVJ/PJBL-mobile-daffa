package com.mahogadaffa.pengingatminumairputih

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Riwayat_Activity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var riwayatList: ArrayList<AirPutih>
    private lateinit var adapter: AirPutihAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        recyclerView = findViewById(R.id.recyclerViewRiwayat)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize list and adapter
        riwayatList = ArrayList()
        adapter = AirPutihAdapter(
            riwayatList,
            onItemClick = { airReminder -> tampilkanDetailDialog(airReminder) },
            onCheckboxClick = { airReminder ->
                // Handle checkbox click if needed
            }
        )
        recyclerView.adapter = adapter

        // Get database reference
        database = FirebaseDatabase.getInstance().getReference("riwayatAirPutih")
        muatDataRiwayat()
    }

    private fun muatDataRiwayat() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                riwayatList.clear()
                for (riwayatSnapshot in snapshot.children) {
                    val riwayat = riwayatSnapshot.getValue(AirPutih::class.java)
                    riwayat?.let { riwayatList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Riwayat_Activity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tampilkanDetailDialog(airReminder: AirPutih) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("DETAIL PENGINGAT AIR")
            .setMessage("""
                Nama Pengingat: ${airReminder.namaPengingat}
                Jumlah Air: ${airReminder.jumlahAir}
                Tanggal: ${airReminder.tanggal}
                Waktu: ${airReminder.waktu}
                Status Pengingat: ${airReminder.statusPengingat}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }
}
