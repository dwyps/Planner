package com.example.planner.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.planner.data.model.Task

@Dao
interface TasksDao {

    @Query("SELECT * FROM task")
    fun getAllTasks() : LiveData<Task>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int) : LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

}