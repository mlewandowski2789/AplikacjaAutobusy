package com.example.busviewer

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var detailAdapter: DetailAdapter
    private val stopDetailsList = ArrayList<StopDetail>()
    private val animationFragment = AnimationFragment()
    private val rightNow = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailAdapter = DetailAdapter(stopDetailsList)
        recyclerView.adapter = detailAdapter

        rightNow.timeZone = TimeZone.getTimeZone("CET");

        supportFragmentManager.beginTransaction()
            .replace(R.id.loading_container, animationFragment)
            .commit()

        val stopIds = intent.getStringArrayListExtra("stopIds")
        if (!stopIds.isNullOrEmpty()) {
            fetchStopDetails(stopIds)
        } else {
            Toast.makeText(this, "No stop IDs found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchStopDetails(stopIds: ArrayList<String>) {
        val volleyQueue = Volley.newRequestQueue(this)
        val currentTime = rightNow.get(Calendar.HOUR_OF_DAY)*60 + rightNow.get(Calendar.MINUTE)
        Log.d("currentTime", rightNow.get(Calendar.HOUR_OF_DAY).toString())

        stopIds.forEach { stopId ->
            val stopDataUrl = "https://www.poznan.pl/mim/komunikacja/service.html?stop_id=$stopId"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                stopDataUrl,
                null,
                { response ->
                    val routesList = ArrayList<Line>()
                    val headsigns:MutableSet<String> = mutableSetOf()
                    val routes = response.getJSONArray("routes")
                    for (i in 0 until routes.length()){
                        val route = routes.getJSONObject(i)
                        val name = route.getString("name")
                        val variants = route.getJSONArray("variants").getJSONObject(0)
                        val headsign = variants.getString("headsign")
                        val departures = variants.getJSONArray("services").getJSONObject(0).getJSONArray("departures")
                        val departuresList = ArrayList<String>()
                        for (j in 0 until departures.length()){
                            val departure = departures.getJSONObject(j)
                            val departureTime = departure.getString("hours").toInt()*60 + departure.getString("minutes").toInt()
                            Log.d("departureTime", departureTime.toString())

                            if(currentTime < departureTime && departureTime <= currentTime+60){
                                val time = departure.getString("hours")+":"+departure.getString("minutes")
                                departuresList.add(time)
                            }

                        }
                        if(departuresList.size > 0){
                            headsigns.add(headsign)
                            val line = Line(name, headsign, departuresList)
                            routesList.add(line)
                        }
                    }
                    if(routesList.size > 0){
                        val stopDetail = StopDetail(stopId, routesList, headsigns)
                        stopDetailsList.add(stopDetail)
                    }


                    detailAdapter.notifyDataSetChanged()
                    animationFragment.stopAnimation()

                },
                { error ->
                    Toast.makeText(this, "API Error", Toast.LENGTH_LONG).show()
                    Log.e("DetailActivity", "API Error: ${error.localizedMessage}")
                }
            )
            volleyQueue.add(jsonObjectRequest)
        }

    }
}

