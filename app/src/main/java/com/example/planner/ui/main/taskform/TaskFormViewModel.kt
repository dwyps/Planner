package com.example.planner.ui.main.taskform

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

class TaskFormViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _addTaskLiveData = MutableLiveData<Resource<Task>>()
    val addTaskLiveData: LiveData<Resource<Task>> = _addTaskLiveData

    private val _getTaskLiveData = MutableLiveData<Resource<Task>>()
    val getTaskLiveData: LiveData<Resource<Task>> = _getTaskLiveData

    @ExperimentalCoroutinesApi
    fun addTask(task: Task) {

        viewModelScope.launch {

            repository.addTask(task)
                .onStart { _addTaskLiveData.value = Resource.loading() }
                .catch { _addTaskLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _addTaskLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getTask(id: Int) {

        viewModelScope.launch {

            repository.getTaskByID(id)
                .onStart { _getTaskLiveData.value = Resource.loading() }
                .catch { _getTaskLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _getTaskLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getTaskID(task: Task) {

        viewModelScope.launch {

            repository.getTaskID(task)
                .onStart { _addTaskLiveData.value = Resource.loading() }
                .catch { _addTaskLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _addTaskLiveData.value = it }
        }
    }
}