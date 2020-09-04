package com.example.planner.ui.survey.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.planner.data.repository.Repository
import com.example.planner.util.Resource
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    private val repository: Repository
) {

    fun removeFirstTimeUser() {

        repository.removeFirstTimeUser()
    }

    fun removeSurveyFromUser(survey: Boolean) {

        repository.removeSurveyFromUser(survey)
    }
}