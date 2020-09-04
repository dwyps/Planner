package com.example.planner.ui.main.settings

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

class SettingsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _userNameLiveData = MutableLiveData<Resource<Boolean>>()
    val userNameLiveData : LiveData<Resource<Boolean>> = _userNameLiveData

    private val _deleteUserLiveData = MutableLiveData<Resource<Boolean>>()
    val deleteUserLiveData : LiveData<Resource<Boolean>> = _deleteUserLiveData

    private val _changePasswordLiveData = MutableLiveData<Resource<Boolean>>()
    val changePasswordLiveData : LiveData<Resource<Boolean>> = _changePasswordLiveData

    fun signOut() {
        repository.firebaseSignOut()
    }

    @ExperimentalCoroutinesApi
    fun deleteAccount() {

        viewModelScope.launch {

            repository.deleteAccount()
                .onStart { _deleteUserLiveData.value = Resource.loading() }
                .catch { _deleteUserLiveData.value = Resource.error("Failed to delete account!") }
                .collect { _deleteUserLiveData.value = it }
        }
    }

    @ExperimentalCoroutinesApi
    fun changePassword(password: String, confirmPassword: String) {

        viewModelScope.launch {

            if (password != confirmPassword) {

                _changePasswordLiveData.value =
                    Resource.error("Passwords don't match!")

            } else if (password.isEmpty() || confirmPassword.isEmpty()) {

                _changePasswordLiveData.value =
                    Resource.error("Fields can't be empty!")

            } else if (password.length < 8) {

                _changePasswordLiveData.value =
                    Resource.error("Password has to be at least 8 characters long!")

            } else {

                repository.changePassword(password)
                    .onStart { _changePasswordLiveData.value = Resource.loading() }
                    .catch { _changePasswordLiveData.value = Resource.error( it.localizedMessage) }
                    .collect { _changePasswordLiveData.value = it }
            }
        }
    }

    fun changeName(name: String) {

        _userNameLiveData.value = Resource.loading()

        if (name.isEmpty())
            _userNameLiveData.value = Resource.error("Name can't be empty!")
        else {
            repository.changeName(name)
            _userNameLiveData.value = Resource(Resource.Status.SUCCESS, null, "Name successfully changed!")
        }
    }

}