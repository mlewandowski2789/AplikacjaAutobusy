package com.example.busviewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LineAdapter(private val lines: List<Line>) :
    RecyclerView.Adapter<LineAdapter.LineViewHolder>() {

    class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lineNameTextView: TextView = itemView.findViewById(R.id.lineNameTextView)
        val headsignTextView: TextView = itemView.findViewById(R.id.headsignTextView)
        val departuresTextView: TextView = itemView.findViewById(R.id.departuresTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.line_item, parent, false)
        return LineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentLine = lines[position]
        holder.lineNameTextView.text = currentLine.name
        holder.headsignTextView.text = currentLine.headsign
        holder.departuresTextView.text = currentLine.departures.joinToString(", ")
    }

    override fun getItemCount() = lines.size
}