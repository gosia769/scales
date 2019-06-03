package com.example.scales

import android.arch.persistence.room.Room
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import kotlin.collections.ArrayList


class HistoryActivity : AppCompatActivity() {

    private var weights : ArrayList<Weight> = arrayListOf()
    private lateinit var db: AppDatabase


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "weightDB"
        ).allowMainThreadQueries().build()
        weights = db.weightDao().getAll() as ArrayList<Weight>
        print(weights)

        val adapter = WeightAdapter(this, weights)
        val listView: ListView = findViewById(R.id.list_weight)
        listView.adapter = adapter
    }

}