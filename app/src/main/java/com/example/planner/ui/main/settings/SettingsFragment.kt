package com.example.planner.ui.main.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.ui.startup.LoginActivity
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_change_name.view.*
import kotlinx.android.synthetic.main.dialog_change_password.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var settingsViewModel: SettingsViewModel

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initViewModel()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {


        settingsViewModel.userNameLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    showChangeNameDialog()
                }
            }
        })

        settingsViewModel.changePasswordLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    showChangePasswordDialog()
                }
            }


        })

        settingsViewModel.deleteUserLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    showChangeNameDialog()
                }
            }

        })
    }

    @ExperimentalCoroutinesApi
    private fun initListeners() {

        settings_btn_change_name.setOnClickListener {

            showChangeNameDialog()
        }

        settings_btn_delete_account.setOnClickListener {

            showDeleteDialog()
        }

        settings_btn_change_password.setOnClickListener {

            showChangePasswordDialog()
        }

        settings_btn_log_out.setOnClickListener {

            settingsViewModel.signOut()

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    @ExperimentalCoroutinesApi
    private fun showChangePasswordDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Change password")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->

                settingsViewModel.changePassword(
                    dialogView.settings_edit_text_new_password.text.toString().trim(),
                    dialogView.settings_edit_text_confirm_password.text.toString().trim()
                )
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showChangeNameDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_change_name, null)

        AlertDialog.Builder(requireContext())
            .setTitle("Change name")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->

                settingsViewModel.changeName(dialogView.settings_edit_text_change_name.text.toString().trim())
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    @ExperimentalCoroutinesApi
    private fun showDeleteDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Delete account")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Delete") { _, _ ->

                settingsViewModel.deleteAccount()
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}