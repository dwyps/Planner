package com.example.planner.ui.startup.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.data.model.RegistrationCredentials
import com.example.planner.data.model.User
import com.example.planner.data.repository.Repository
import com.example.planner.util.Resource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _createUserLiveData = MutableLiveData<Resource<User>>()
    val createUserLiveData: LiveData<Resource<User>> = _createUserLiveData

    fun signUp(registrationCredentials: RegistrationCredentials) {

        viewModelScope.launch {

            if (registrationCredentials.password != registrationCredentials.confirmPassword) {

                _createUserLiveData.value =
                    Resource(Resource.Status.ERROR, null, "Passwords don't match!")

            } else if (registrationCredentials.name.isEmpty() || registrationCredentials.email.isEmpty()
                || registrationCredentials.password.isEmpty() || registrationCredentials.confirmPassword.isEmpty()) {

                _createUserLiveData.value =
                    Resource(Resource.Status.ERROR, null, "Fields can't be empty!")

            } else if (registrationCredentials.password.length < 8) {

                _createUserLiveData.value =
                    Resource(Resource.Status.ERROR, null, "Password has to be at least 8 characters long!")

            } else {

                repository.firebaseSignUp(registrationCredentials)
                    .onStart { _createUserLiveData.value = Resource(Resource.Status.LOADING, null, null) }
                    .catch { _createUserLiveData.value = Resource(Resource.Status.ERROR, null, it.localizedMessage) }
                    .collect { _createUserLiveData.value = it }
            }
        }
    }

    fun currentUser() = repository.firebaseCurrentUser()
}