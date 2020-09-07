package com.example.planner.ui.startup.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.model.RegistrationCredentials
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    @Inject
    lateinit var registerViewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()

    }

    private fun initViewModel() {

        if (registerViewModel.currentUser() != null)
            findNavController().popBackStack()

        registerViewModel.createUserLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_login_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_login_spinner.visibility = View.GONE

                    findNavController().popBackStack()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_login_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }

            }
        })

    }

    private fun initListeners() {

        register_btn_sign_up.setOnClickListener {

            registerUser()
        }
    }

    private fun registerUser() {

        val name = register_edit_text_name.text.toString().trim()
        val email = register_edit_text_email.text.toString().trim()
        val password = register_edit_text_password.text.toString().trim()
        val confirmPassword = register_edit_text_check_password.text.toString().trim()

        val registrationCredentials = RegistrationCredentials(name, email, password, confirmPassword)

        registerViewModel.signUp(registrationCredentials)
    }
}