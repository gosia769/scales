package com.example.scales

import android.arch.persistence.room.Room
import android.content.Intent
import android.databinding.Bindable
import android.databinding.DataBindingUtil
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

        if (db.weightDao().getAll().isNotEmpty()) {
            print(db.weightDao().getAll().size.toString())
            val lastWeight = db.weightDao().loadAllByIds(IntArray(db.weightDao().getAll().lastIndex))[0]
            findViewById<TextView>(R.id.lastWeightDate).text = lastWeight.date.toString()
            findViewById<TextView>(R.id.lastWeightValue).text = lastWeight.value.toString()
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
