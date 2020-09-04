package com.example.planner.ui.survey.introduction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_introduction.*

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {

        introduction_btn_skip.setOnClickListener {

            findNavController().navigate(IntroductionFragmentDirections.actionHobbiesFragmentToSummaryFragment(false))
        }

        introduction_btn_continue.setOnClickListener {
            findNavController().navigate(IntroductionFragmentDirections.actionIntroductionFragmentToHobbiesFragment())
        }
    }
}