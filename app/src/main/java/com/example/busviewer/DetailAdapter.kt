package com.example.busviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailAdapter(private val stopDetails: List<StopDetail>) :
    RecyclerView.Adapter<DetailAdapter.StopDetailViewHolder>() {

    class StopDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopIdTextView: TextView = itemView.findViewById(R.id.stopIdTextView)
        val linesRecyclerView: RecyclerView = itemView.findViewById(R.id.linesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.detail_item, parent, false)
        return StopDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StopDetailViewHolder, position: Int) {
        val currentStopDetail = stopDetails[position]
        holder.stopIdTextView.text = "Kierunek: ${currentStopDetail.headsigns}"

        holder.linesRecyclerView.layoutManager = LinearLayoutManager(holder.linesRecyclerView.context)
        holder.linesRecyclerView.adapter = LineAdapter(currentStopDetail.lines)
    }

    override fun getItemCount() = stopDetails.size
}
