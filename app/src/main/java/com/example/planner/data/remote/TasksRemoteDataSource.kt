package com.example.planner.data.remote

import javax.inject.Inject

class TasksRemoteDataSource @Inject constructor(
    private val networkService: NetworkService
): BaseDataSource() {

    suspend fun getTasks(id: Int) = getResult{ networkService.getTasks(id) }
}