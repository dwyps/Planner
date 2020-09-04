package com.example.planner.ui.survey.categories

import androidx.lifecycle.ViewModel
import com.example.planner.data.repository.Repository
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun setCategories(categories: List<Int>) {

        repository.setCategories(categories)
    }
}