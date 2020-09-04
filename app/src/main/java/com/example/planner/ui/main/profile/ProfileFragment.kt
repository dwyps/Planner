package com.example.planner.ui.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.ui.main.MainActivity
import com.example.planner.ui.main.calendar.CalendarFragmentDirections
import com.example.planner.ui.main.profile.adapter.ProfileTasksRecyclerAdapter
import com.example.planner.ui.main.profile.adapter.ProfileToDoListRecyclerAdapter
import com.example.planner.util.Constants.Companion.RESULT_LOAD_IMAGE
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile),
    ProfileToDoListRecyclerAdapter.OnItemListener,
    ProfileTasksRecyclerAdapter.OnItemListener {

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private lateinit var recyclerViewTasksAdapter: ProfileTasksRecyclerAdapter
    private lateinit var recyclerViewToDoAdapter: ProfileToDoListRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).ensureBottomNavigation()
    }

    @ExperimentalCoroutinesApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            val selectedImage: Uri = data.data!!

            profileViewModel.setPicture(selectedImage)
        }

    }

    private fun initViewModel() {

        profileViewModel.getUserName()
        profileViewModel.getPicture()

        profileViewModel.userNameLiveData.observe(viewLifecycleOwner, {

            profile_tv_username.text = it
        })

        profileViewModel.userGetPictureLiveData.observe(viewLifecycleOwner, {

            setProfilePicture(it)
        })

        profileViewModel.userSetPictureLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility =
                    View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    profileViewModel.getPicture()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun initListeners() {

        btn_profile_settings.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
        }

        profile_iv_profile_photo.setOnClickListener {

            val i = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }

        requireActivity().fab.setOnClickListener {

            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToTaskForm())
        }
    }

    private fun setProfilePicture(imageUri: Uri?) {

        Glide.with(profile_iv_profile_photo.context)
            .asDrawable()
            .load(imageUri)
            .apply(
                RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
            )
            .into(profile_iv_profile_photo)
    }


    private fun initRecyclerView() {

        initTasksRecycler()
        initToDoRecycler()
    }

    private fun initTasksRecycler() {

        recyclerViewTasksAdapter = ProfileTasksRecyclerAdapter(this)

        profile_recycler_view_tasks.adapter = recyclerViewTasksAdapter
    }

    private fun initToDoRecycler() {

        recyclerViewToDoAdapter = ProfileToDoListRecyclerAdapter(this)

        profile_recycler_view_to_do_list.adapter = recyclerViewToDoAdapter
    }

    override fun onItemTaskClick(position: Int, task: Task) {}
    override fun onItemToDoClick(position: Int, task: Task) {}
}