package com.example.busviewer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class DetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var detailAdapter: DetailAdapter
    private val stopDetailsList = ArrayList<StopDetail>()
    private lateinit var backgroundImg: ImageView
    private lateinit var foregroundImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        detailAdapter = DetailAdapter(stopDetailsList)
        recyclerView.adapter = detailAdapter


        val backgroundAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
        backgroundImg = findViewById(R.id.backgroundImageView)
        foregroundImg = findViewById(R.id.foregroundImageView)

        backgroundImg.startAnimation(backgroundAnimation)

        val stopIds = intent.getStringArrayListExtra("stopIds")
        if (!stopIds.isNullOrEmpty()) {
            fetchStopDetails(stopIds)
        } else {
            Toast.makeText(this, "No stop IDs found", Toast.LENGTH_SHORT).show()
        }



    }

    private fun fetchStopDetails(stopIds: ArrayList<String>) {
        val volleyQueue = Volley.newRequestQueue(this)

        stopIds.forEach { stopId ->
            val stopDataUrl = "https://www.poznan.pl/mim/komunikacja/service.html?stop_id=$stopId"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                stopDataUrl,
                null,
                { response ->
                    Log.d("DetailActivity", response.toString())

                    val routesList = ArrayList<Line>()
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
                            val time = departure.getString("hours")+":"+departure.getString("minutes")
                            departuresList.add(time)
                        }

                        val line = Line(name, headsign, departuresList)
                        routesList.add(line)
                    }
                    val stopDetail = StopDetail(stopId, routesList)
                    stopDetailsList.add(stopDetail)

                    detailAdapter.notifyDataSetChanged()
                    backgroundImg.visibility = View.GONE
                    foregroundImg.visibility = View.GONE
                    backgroundImg.clearAnimation()

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
