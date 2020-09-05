package com.example.planner.ui.survey.questions.selfcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.planner.R
import com.example.planner.ui.survey.questions.QuestionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_self_care.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SelfCareFragment : Fragment(R.layout.fragment_self_care) {

    @Inject
    lateinit var questionsViewModel: QuestionsViewModel

    private val args: SelfCareFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners(){

        val category = 0

        self_care_btn_answer1.setOnClickListener {

            questionsViewModel.setAnswer(0, category)
            navigateNextQuestion()
        }

        self_care_btn_answer2.setOnClickListener {

            questionsViewModel.setAnswer(1, category)
            navigateNextQuestion()
        }

        self_care_btn_answer3.setOnClickListener {

            questionsViewModel.setAnswer(2, category)
            navigateNextQuestion()
        }

        self_care_btn_answer4.setOnClickListener {

            questionsViewModel.setAnswer(3, category)
            navigateNextQuestion()
        }

        self_care_btn_answer5.setOnClickListener {

            questionsViewModel.setAnswer(4, category)
            navigateNextQuestion()
        }

    }

    private fun navigateNextQuestion() {

        args.categories.forEach {
            Timber.e(it.toString())
        }

        when(true) {

            args.categories.contains(1) -> findNavController().navigate(SelfCareFragmentDirections.actionSelfCareFragmentToEducationFragment(args.categories))

            args.categories.contains(2) -> findNavController().navigate(SelfCareFragmentDirections.actionSelfCareFragmentToActivitiesFragment(args.categories))

            args.categories.contains(3) -> findNavController().navigate(SelfCareFragmentDirections.actionSelfCareFragmentToEntertainmentFragment())

            else -> findNavController().navigate(SelfCareFragmentDirections.actionSelfCareFragmentToSummaryFragment())
        }
    }
}