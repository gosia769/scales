package com.example.scales

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight")
    fun getAll(): List<Weight>

    @Query("SELECT * FROM weight WHERE id IN (:weightIds)")
    fun loadAllByIds(weightIds: IntArray): List<Weight>

    @Insert
    fun insertAll(vararg weight: Weight)

    @Delete
    fun delete(weight: Weight)
}