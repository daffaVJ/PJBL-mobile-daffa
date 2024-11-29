package com.mahogadaffa.pengingatminumairputih

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PengingatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var airPutihList: ArrayList<AirPutih>
    private lateinit var adapter: AirPutihAdapter
    private lateinit var database: DatabaseReference
    private lateinit var riwayatDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengingat)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        airPutihList = ArrayList()
        adapter = AirPutihAdapter(
            airPutihList,
            onItemClick = { airPutih -> showOptionsDialog(airPutih) },
            onCheckboxClick = { airPutih -> updateAirPutihStatus(airPutih) }
        )

        recyclerView.adapter = adapter
        database = FirebaseDatabase.getInstance().getReference("airPutih")
        riwayatDatabase = FirebaseDatabase.getInstance().getReference("riwayatAirPutih")

        loadAirPutihData()
    }

    private fun loadAirPutihData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                airPutihList.clear()
                for (airPutihSnapshot in snapshot.children) {
                    val airPutih = airPutihSnapshot.getValue(AirPutih::class.java)
                    airPutih?.let { airPutihList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PengingatActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAirPutihStatus(airPutih: AirPutih) {
        val updatedAirPutih = airPutih.copy(statusPengingat = "Sudah diminum")
        database.child(airPutih.id).setValue(updatedAirPutih)
    }

    private fun showOptionsDialog(airPutih: AirPutih) {
        val options = arrayOf("Edit", "Hapus", "Pindah ke Riwayat")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> navigateToEdit(airPutih)
                1 -> deleteSchedule(airPutih)
                2 -> moveToHistory(airPutih)
            }
        }
        builder.show()
    }

    private fun navigateToEdit(airPutih: AirPutih) {
        val intent = Intent(this, JadwalActivity::class.java).apply {
            putExtra("pengingatId", airPutih.id)
            putExtra("jenisAir", airPutih.jenisair)
            putExtra("jumlahAir", airPutih.jumlahAir)
            putExtra("status", "update")
        }
        startActivity(intent)
    }

    private fun deleteSchedule(airPutih: AirPutih) {
        database.child(airPutih.id).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Jadwal dihapus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus jadwal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveToHistory(airPutih: AirPutih) {
        val riwayatKey = riwayatDatabase.push().key
        if (riwayatKey != null) {
            riwayatDatabase.child(riwayatKey).setValue(airPutih)
                .addOnSuccessListener {
                    database.child(airPutih.id).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data dipindahkan ke riwayat", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal memindahkan data ke riwayat", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan data di riwayat", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: Riwayat key tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadAirPutihData()
    }
}
