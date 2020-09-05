package com.example.planner.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.model.Profile
import com.example.planner.data.model.Task
import com.example.planner.data.repository.Repository
import com.example.planner.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _userNameLiveData = MutableLiveData<String>()
    val userNameLiveData : LiveData<String> = _userNameLiveData

    private val _userSurveyLiveData = MutableLiveData<Resource<Boolean>>()
    val userSurveyLiveData : LiveData<Resource<Boolean>> = _userSurveyLiveData

    private val _surveyProfileLiveData = MutableLiveData<Resource<Profile>>()
    val surveyProfileLiveData : LiveData<Resource<Profile>> = _surveyProfileLiveData

    private val _refreshDailyTaskLiveData = MutableLiveData<Resource<Boolean>>()
    val refreshDailyTaskLiveData : LiveData<Resource<Boolean>> = _refreshDailyTaskLiveData

    private val _tasksLiveData = MutableLiveData<Resource<List<Task>>>()
    val tasksLiveData : LiveData<Resource<List<Task>>> = _tasksLiveData

    private val _taskIDLiveData = MutableLiveData<Resource<Task>>()
    val taskIDLiveData : LiveData<Resource<Task>> = _taskIDLiveData

    private val _dailyTasksLiveData = MutableLiveData<Resource<List<Task>>>()
    val dailyTasksLiveData : LiveData<Resource<List<Task>>> = _dailyTasksLiveData

    private val _dailyTaskLiveData = MutableLiveData<Resource<Boolean>>()
    val dailyTaskLiveData : LiveData<Resource<Boolean>> = _dailyTaskLiveData

    private val _userGetPictureLiveData = MutableLiveData<Uri>()
    val userGetPictureLiveData : LiveData<Uri> = _userGetPictureLiveData

    private val _userSetPictureLiveData = MutableLiveData<Resource<Boolean>>()
    val userSetPictureLiveData : LiveData<Resource<Boolean>> = _userSetPictureLiveData

    fun getUserName() {
        _userNameLiveData.value = repository.getUserName()
    }

    fun getPicture() {
        _userGetPictureLiveData.value = repository.getImage()
    }

    @ExperimentalCoroutinesApi
    fun checkSurveyStatus() {

        viewModelScope.launch {

            repository.checkSurveyStatus()
                .onStart { _userSurveyLiveData.value = Resource.loading() }
                .catch { _userSurveyLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _userSurveyLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getSurveyProfile() {

        viewModelScope.launch {

            repository.getSurveyProfile()
                .onStart { _surveyProfileLiveData.value = Resource.loading() }
                .catch { _surveyProfileLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _surveyProfileLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun checkDailyTasks() {

        viewModelScope.launch {

            repository.checkDailyTasks()
                .onStart { _refreshDailyTaskLiveData.value = Resource.loading() }
                .catch { _refreshDailyTaskLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _refreshDailyTaskLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun setPicture(selectedImage: Uri) {

        viewModelScope.launch {

            repository.setImage(selectedImage)
                .onStart { _userSetPictureLiveData.value = Resource.loading() }
                .catch { _userSetPictureLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _userSetPictureLiveData.value = it }
        }
    }

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
    fun addDailyTask(task: Task) {

        viewModelScope.launch {

            repository.addDailyTask(task)
                .onStart { _dailyTaskLiveData.value = Resource.loading() }
                .catch { _dailyTaskLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _dailyTaskLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getDailyTasks(profile: Profile) {

        viewModelScope.launch {

            repository.getDailyTasks(profile)
                .onStart { _dailyTasksLiveData.value = Resource.loading() }
                .catch { _dailyTasksLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _dailyTasksLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun getTaskID(task: Task) {

        viewModelScope.launch {

            repository.getTaskID(task)
                .onStart { _taskIDLiveData.value = Resource.loading() }
                .catch { _taskIDLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _taskIDLiveData.value = it }
        }
    }
}