package com.example.planner.ui.survey.questions.activitites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.planner.R
import com.example.planner.ui.survey.questions.QuestionsViewModel
import com.example.planner.ui.survey.questions.education.EducationFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activities.*
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_education.education_btn_answer1
import kotlinx.android.synthetic.main.fragment_education.education_btn_answer2
import javax.inject.Inject

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    @Inject
    lateinit var questionsViewModel: QuestionsViewModel

    private val args: ActivitiesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners(){

        val category = 2

        activities_btn_answer1.setOnClickListener {

            questionsViewModel.setAnswer(0, category)
            navigateNextQuestion()
        }

        activities_btn_answer2.setOnClickListener {

            questionsViewModel.setAnswer(1, category)
            navigateNextQuestion()
        }

        activities_btn_answer3.setOnClickListener {

            questionsViewModel.setAnswer(2, category)
            navigateNextQuestion()
        }

        activities_btn_answer4.setOnClickListener {

            questionsViewModel.setAnswer(3, category)
            navigateNextQuestion()
        }

        activities_btn_answer5.setOnClickListener {

            questionsViewModel.setAnswer(4, category)
            navigateNextQuestion()
        }
    }

    private fun navigateNextQuestion() {

        when(args.categories.contains(3)) {

            true -> findNavController().navigate(ActivitiesFragmentDirections.actionActivitiesFragmentToEntertainmentFragment())

            else -> findNavController().navigate(ActivitiesFragmentDirections.actionActivitiesFragmentToSummaryFragment())
        }
    }
}