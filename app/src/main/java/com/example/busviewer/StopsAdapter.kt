package com.example.busviewer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StopsAdapter(private var stops: List<Stop>, private val context: Context) : RecyclerView.Adapter<StopsAdapter.StopViewHolder>(), Filterable {

    private var filteredStops: List<Stop> = stops

    fun setData(newStops: List<Stop>) {
        stops = newStops
        filteredStops = newStops
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stop_item, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = filteredStops[position]
        holder.bind(stop)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putStringArrayListExtra("stopIds", ArrayList(stop.ids))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredStops.size
    }

    inner class StopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stopNameTextView: TextView = itemView.findViewById(R.id.stopNameTextView)

        fun bind(stop: Stop) {
            stopNameTextView.text = stop.name
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim()
                val filteredList = if (query.isNullOrEmpty()) {
                    stops
                } else {
                    stops.filter {
                        it.name.lowercase().contains(query)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredStops = results?.values as List<Stop>
                notifyDataSetChanged()
            }
        }
    }
}
