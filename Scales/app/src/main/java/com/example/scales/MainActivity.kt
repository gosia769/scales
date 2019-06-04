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
        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "weightDB"
        ).allowMainThreadQueries().build()

//        db.weightDao().insertAll(
//            Weight(0, Date(), 55.0),
//            Weight(1, Date(), 54.0)
//        )


//        val binding: ActivityMainBinding = DataBindingUtil.setContentView<Any>(
//            this, R.layout.activity_main)

        if (db.weightDao().getAll().isNotEmpty()) {
            print(db.weightDao().getAll().size.toString())
            var lastWeight = db.weightDao().loadAllByIds(IntArray(db.weightDao().getAll().lastIndex)).get(0)

//            binding.weight = Weight(lastWeight.id, lastWeight.date, lastWeight.value)


//            findViewById<TextView>(R.id.lastWeightDate).apply {
//                text = lastWeight.date.toString()
//            }
//            findViewById<TextView>(R.id.lastWeightValue).apply {
//                text = lastWeight.value.toString()
//            }
        }

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
