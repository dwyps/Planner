package com.example.planner.ui.startup.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.model.LoginCredentials
import com.example.planner.ui.main.MainActivity
import com.example.planner.ui.survey.SurveyActivity
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()

    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        if (loginViewModel.currentUser() != null)
            loginViewModel.firstTime()

        loginViewModel.authenticatedUserLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_login_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_login_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    if (loginViewModel.currentUser() != null)
                        successfulSignIn(it.data!!)
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_login_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    @ExperimentalCoroutinesApi
    private fun initListeners() {

        login_btn_sign_in.setOnClickListener {

            signIn()
        }

        login_btn_forgot_password.setOnClickListener {

            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_forgot_password, null)

            AlertDialog.Builder(requireContext())
                .setTitle("Forgot Password")
                .setView(dialogView)
                .setPositiveButton("Reset password") { dialog, _ ->

                    loginViewModel.forgotPassword(dialogView.login_edit_text_forgot_password.text.toString().trim())

                    dialog.cancel()
                }
                .setNegativeButton("Cancel") {dialog, _ -> dialog.cancel() }
                .show()

        }

        login_btn_sign_up.setOnClickListener {

            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    @ExperimentalCoroutinesApi
    private fun signIn() {

        val credentials =
            LoginCredentials(email = login_edit_text_email.text.toString().trim(), password = login_edit_text_password.text.toString().trim())

        loginViewModel.signIn(credentials)
    }

    private fun successfulSignIn(firstTime: Boolean) {

        if (firstTime)
            startActivity(Intent(requireContext(), SurveyActivity::class.java))
        else
            startActivity(Intent(requireContext(), MainActivity::class.java))

        requireActivity().finish()
    }
}