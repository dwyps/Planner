package com.example.planner.ui.startup.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.model.LoginCredentials
import com.example.planner.data.repository.Repository
import com.example.planner.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _authenticatedUserLiveData = MutableLiveData<Resource<Boolean>>()
    val authenticatedUserLiveData : LiveData<Resource<Boolean>> = _authenticatedUserLiveData


    @ExperimentalCoroutinesApi
    fun signIn(loginCredentials: LoginCredentials) {

        viewModelScope.launch {

            if (loginCredentials.email.isEmpty()) {

                _authenticatedUserLiveData.value = Resource.error("Email can't be empty!")

            } else if (loginCredentials.password.isEmpty()) {

                _authenticatedUserLiveData.value = Resource.error("Password can't be empty!")

            } else if (loginCredentials.email.isNotEmpty() && loginCredentials.password.isNotEmpty()) {

                repository.firebaseSignIn(loginCredentials)
                    .onStart { _authenticatedUserLiveData.value = Resource.loading() }
                    .catch { _authenticatedUserLiveData.value = Resource.error(it.localizedMessage)}
                    .collect { _authenticatedUserLiveData.value = it }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun firstTime() {

        viewModelScope.launch {

            repository.firebaseCheckFirstTime()
                .onStart { _authenticatedUserLiveData.value = Resource.loading() }
                .catch { _authenticatedUserLiveData.value = Resource.error(it.localizedMessage) }
                .collect { _authenticatedUserLiveData.value = it }
        }
    }

    fun currentUser() = repository.firebaseCurrentUser()

    fun forgotPassword(email: String) = repository.forgotPassword(email)
}