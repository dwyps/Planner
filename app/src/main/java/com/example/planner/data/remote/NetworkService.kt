package com.example.planner.data.remote

import com.example.planner.data.model.Task
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkService {

    @GET("task")
    suspend fun getAllTasks(): Response<List<Task>>

    @GET("task/{id}")
    suspend fun getTasks(@Path("id") id: Int): Response<Task>
}