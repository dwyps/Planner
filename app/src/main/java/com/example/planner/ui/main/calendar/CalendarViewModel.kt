package com.example.planner.ui.main.calendar

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
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _tasksLiveData = MutableLiveData<Resource<List<Task>>>()
    val tasksLiveData: LiveData<Resource<List<Task>>> = _tasksLiveData

    private val _currentDayTasksLiveData = MutableLiveData<Resource<List<Task>>>()
    val currentDayTasksLiveData: LiveData<Resource<List<Task>>> = _currentDayTasksLiveData

    @ExperimentalCoroutinesApi
    fun getAllTasks() {

        viewModelScope.launch {

            repository.getAllTasks()
                .onStart { _tasksLiveData.value = Resource.loading() }
                .catch { _tasksLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _tasksLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getCurrentDayTasks(date: Date) {

        viewModelScope.launch {

            repository.getCurrentDayTasks(date)
                .onStart { _currentDayTasksLiveData.value = Resource.loading() }
                .catch { _currentDayTasksLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _currentDayTasksLiveData.value = it }
        }
    }
}