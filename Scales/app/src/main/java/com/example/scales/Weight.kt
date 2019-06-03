package com.example.scales

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import java.util.*

@Entity
data class Weight(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "value") val value: Double
)