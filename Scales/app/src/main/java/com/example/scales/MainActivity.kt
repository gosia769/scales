package com.example.scales

import android.arch.persistence.room.Room
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "weightDB"
        ).allowMainThreadQueries().build()

//        db.weightDao().insertAll(
//            Weight(0, Date(), 55.0),
//            Weight(1, Date(), 54.0)
//        )

        if (db.weightDao().getAll().isNotEmpty()) {
            val lastWeight = db.weightDao().loadAllByIds(IntArray(db.weightDao().getAll().lastIndex))[0]
            findViewById<TextView>(R.id.lastWeightDate).text = lastWeight.date.toString()
            findViewById<TextView>(R.id.lastWeightValue).text = lastWeight.value.toString() + " kg"

//            val size = db.weightDao().getAll().size
//            val id = db.weightDao().getAll()[size - 1].id
//            val lastWeight = db.weightDao().loadAllByIds(intArrayOf(id))[0]
//
//            val textView1: TextView = findViewById(R.id.lastWeightValue)
//            textView1.text = lastWeight.value.toString() + " kg"
//            val textView2: TextView = findViewById(R.id.lastWeightDate)
//            textView2.text = lastWeight.date.toString()
        }

    }

    fun onButtonHistoryClick(view: View){
        val myIntent = Intent(baseContext, HistoryActivity::class.java)
        startActivity(myIntent)
    }


    fun onButtonScalesClick(view: View){
        val myIntent = Intent(baseContext, ScalesActivity::class.java)
        startActivity(myIntent)
    }

}
