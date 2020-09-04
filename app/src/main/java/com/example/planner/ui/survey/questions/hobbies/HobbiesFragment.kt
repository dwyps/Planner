package com.example.planner.ui.survey.questions.hobbies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_hobbies.*

@AndroidEntryPoint
class HobbiesFragment : Fragment(R.layout.fragment_hobbies) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners(){

        hobbies_btn_next.setOnClickListener {

            findNavController().navigate(HobbiesFragmentDirections.actionHobbiesFragmentToCategoriesFragment())
        }
    }
}