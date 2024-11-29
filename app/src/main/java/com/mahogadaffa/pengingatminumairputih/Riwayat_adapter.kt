package com.mahogadaffa.pengingatminumairputih

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Riwayat_adapter(
    private val riwayatList: ArrayList<AirPutih>,
    private val onItemClick: (AirPutih) -> Unit
) : RecyclerView.Adapter<Riwayat_adapter.RiwayatViewHolder>() {

    class RiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaObat: TextView = itemView.findViewById(R.id.textNamaObat)
        val dosisObat: TextView = itemView.findViewById(R.id.textDosisObat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return RiwayatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val itemSekarang = riwayatList[position]

        holder.namaObat.text = itemSekarang.namaPengingat
        holder.dosisObat.text = "jumlah: ${itemSekarang.jumlahAir}"

        holder.itemView.setOnClickListener {
            onItemClick(itemSekarang)
        }
    }

    override fun getItemCount(): Int = riwayatList.size
}
