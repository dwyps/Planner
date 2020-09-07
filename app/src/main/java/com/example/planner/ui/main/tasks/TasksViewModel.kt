package com.example.planner.ui.main.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.model.Task
import com.example.planner.data.repository.Repository
import com.example.planner.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<Resource<List<Task>>>()
    val tasksLiveData : LiveData<Resource<List<Task>>> = _tasksLiveData

    @ExperimentalCoroutinesApi
    fun getAllTasks() {

        viewModelScope.launch {

            repository.getAllTasks()
                .onStart { _tasksLiveData.value = Resource.loading() }
                .catch { _tasksLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _tasksLiveData.value = it }
        }
    }

    fun getTodayTasks(tasks: List<Task>): List<Task> {

           return repository.getTodayTasks(tasks)
    }

    fun getThisWeekTasks(tasks: List<Task>): List<Task> {

        return repository.getThisWeekTasks(tasks)
    }

    fun getThisMonthTasks(tasks: List<Task>): List<Task> {

        return repository.getThisMonthTasks(tasks)
    }

    fun getEvents() = repository.getEvents()
}