package com.example.scales

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(entities = [Weight::class], version = 1)
@TypeConverters(Converters::class)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
}