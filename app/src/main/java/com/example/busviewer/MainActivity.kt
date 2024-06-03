package com.example.busviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.appcompat.widget.SearchView


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StopsAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var searchView: SearchView
    private val animationFragment = AnimationFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StopsAdapter(ArrayList(), this)
        recyclerView.adapter = adapter
        db = Firebase.firestore

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        supportFragmentManager.beginTransaction()
            .replace(R.id.loading_container, animationFragment)
            .commit()

        main()

    }

    private fun main() {
        db.collection("stops")
            .get()
            .addOnSuccessListener { result ->
                val stopList = mutableListOf<Stop>()
                for (document in result) {
                    val name = document.id.replace('_', ' ')
                    val ids = document.get("ids") as? List<String> ?: emptyList()
                    stopList.add(Stop(name, ids))
                }
                adapter.setData(stopList)
                animationFragment.stopAnimation()
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }
}