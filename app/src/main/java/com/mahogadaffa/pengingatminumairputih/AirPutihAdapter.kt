package com.mahogadaffa.pengingatminumairputih

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AirPutihAdapter(
    private val airPutihList: ArrayList<AirPutih>,
    private val onItemClick: (AirPutih) -> Unit,
    private val onCheckboxClick: (AirPutih) -> Unit
) : RecyclerView.Adapter<AirPutihAdapter.AirPutihViewHolder>() {

    class AirPutihViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPengingat: TextView = itemView.findViewById(R.id.namaPengingatList)
        val tvJumlahAir: TextView = itemView.findViewById(R.id.jumlahAirList)
        val tvWaktu: TextView = itemView.findViewById(R.id.waktuList)
        val tvTanggal: TextView = itemView.findViewById(R.id.tanggalList)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxList)
        val imgChecked: ImageView = itemView.findViewById(R.id.imgCheckedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirPutihViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_air, parent, false)
        return AirPutihViewHolder(view)
    }

    override fun onBindViewHolder(holder: AirPutihViewHolder, position: Int) {
        val airPutih = airPutihList[position]

        holder.apply {
//            tvNamaPengingat.text = airPutih.namaPengingat
            tvJumlahAir.text = "ML: ${airPutih.jumlahAir}"
            tvWaktu.text = "Waktu: ${airPutih.waktu}"
            tvTanggal.text = "Tanggal: ${airPutih.tanggal}"

            // Handle checkbox and completed status
            val isCompleted = airPutih.statusPengingat == "Sudah diminum"
            checkBox.isChecked = isCompleted

            if (isCompleted) {
                checkBox.visibility = View.GONE
                imgChecked.visibility = View.VISIBLE
                itemView.alpha = 0.7f
                tvNamaPengingat.setTextColor(Color.GRAY)
            } else {
                checkBox.visibility = View.VISIBLE
                imgChecked.visibility = View.GONE
                itemView.alpha = 1.0f
                tvNamaPengingat.setTextColor(Color.BLACK)
            }

            // Handle click listeners
            checkBox.setOnClickListener {
                if (!isCompleted) {
                    onCheckboxClick(airPutih)
                }
            }

            itemView.setOnClickListener {
                onItemClick(airPutih)
            }
        }
    }

    override fun getItemCount() = airPutihList.size

    fun updateList(newList: List<AirPutih>) {
        airPutihList.clear()
        airPutihList.addAll(newList)
        notifyDataSetChanged()
    }
}