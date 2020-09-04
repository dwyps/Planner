package com.example.planner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task (
    @PrimaryKey
    var id: Int?,
    var type: Boolean,
    var title: String,
    var description: String,
    var icon: Int,
    var color: Int,
    var dateFrom: Long,
    var dateTo: Long,
    var allDay: Boolean,
    var reminder: Int
)