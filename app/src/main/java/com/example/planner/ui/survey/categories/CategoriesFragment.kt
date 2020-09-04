package com.example.planner.ui.survey.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.model.Profile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_categories.*
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories) {

    @Inject
    lateinit var categoriesViewModel: CategoriesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {

        categories_btn_continue.setOnClickListener {

            val checkedCategories = listOf(
                categories_checkbox_self_care.isChecked,
                categories_checkbox_education.isChecked,
                categories_checkbox_activities.isChecked,
                categories_checkbox_entertainment.isChecked
            )
            val categories = mutableListOf<Int>()

            checkedCategories.forEachIndexed { index, b ->

                if (b)
                    categories.add(index)
            }

            categoriesViewModel.setCategories(categories)

            when(categories.first()) {

                0 -> findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToSelfCareFragment())
                1 -> findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToEducationFragment())
                2 -> findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToActivitiesFragment())
                3 -> findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToEntertainmentFragment())
            }
        }
    }
}