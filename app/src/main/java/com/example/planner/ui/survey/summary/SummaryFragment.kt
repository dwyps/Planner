package com.example.planner.ui.survey.summary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.planner.R
import com.example.planner.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_summary.*
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment : Fragment(R.layout.fragment_summary) {

    @Inject
    lateinit var summaryViewModel: SummaryViewModel

    private val args: SummaryFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
    }

    private fun initViewModel() {

        summaryViewModel.removeFirstTimeUser()
        summaryViewModel.removeSurveyFromUser(args.state)

    }

    private fun initListeners() {

        btn_summary_finish.setOnClickListener {

            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}