package com.example.planner.ui.survey.questions

import androidx.lifecycle.ViewModel
import com.example.planner.data.repository.Repository
import javax.inject.Inject

class QuestionsViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    fun setAnswer(answer: Int, category: Int) {

        repository.setAnswer(answer, category)
    }
}