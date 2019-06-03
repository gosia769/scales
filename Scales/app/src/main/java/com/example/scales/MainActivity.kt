package com.example.scales

import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.net.http.HttpResponseCache.install
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "weightDB"
        ).allowMainThreadQueries().build()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
