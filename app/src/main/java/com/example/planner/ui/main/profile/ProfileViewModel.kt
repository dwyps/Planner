package com.example.planner.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun setPicture(selectedImage: Uri) {

        viewModelScope.launch {

            repository.setImage(selectedImage)
                .onStart { _userSetPictureLiveData.value = Resource.loading() }
                .catch { _userSetPictureLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _userSetPictureLiveData.value = it }
        }
    }
}