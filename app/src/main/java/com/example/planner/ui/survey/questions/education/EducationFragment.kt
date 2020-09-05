package com.example.planner.ui.survey.questions.education

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.planner.R
import com.example.planner.ui.survey.questions.QuestionsViewModel
import com.example.planner.ui.survey.questions.selfcare.SelfCareFragmentArgs
import com.example.planner.ui.survey.questions.selfcare.SelfCareFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_education.*
import javax.inject.Inject

@AndroidEntryPoint
class EducationFragment : Fragment(R.layout.fragment_education) {

    @Inject
    lateinit var questionsViewModel: QuestionsViewModel

    private val args: EducationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners(){

        val category = 1

        education_btn_answer1.setOnClickListener {

            questionsViewModel.setAnswer(0, category)
            navigateNextQuestion()
        }

        education_btn_answer2.setOnClickListener {

            questionsViewModel.setAnswer(1, category)
            navigateNextQuestion()
        }

        education_btn_answer3.setOnClickListener {

            questionsViewModel.setAnswer(2, category)
            navigateNextQuestion()
        }

        education_btn_answer4.setOnClickListener {

            questionsViewModel.setAnswer(3, category)
            navigateNextQuestion()
        }
    }

    private fun navigateNextQuestion() {

        when(true) {

            args.categories.contains(2) -> findNavController().navigate(EducationFragmentDirections.actionEducationFragmentToActivitiesFragment(args.categories))

            args.categories.contains(3) -> findNavController().navigate(EducationFragmentDirections.actionEducationFragmentToEntertainmentFragment())

            else -> findNavController().navigate(EducationFragmentDirections.actionEducationFragmentToSummaryFragment())
        }
    }

}