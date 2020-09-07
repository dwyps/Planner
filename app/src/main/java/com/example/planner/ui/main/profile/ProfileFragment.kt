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
import com.example.planner.data.model.Profile
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile),
    ProfileToDoListRecyclerAdapter.OnItemListener,
    ProfileTasksRecyclerAdapter.OnItemListener {

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private lateinit var recyclerViewTasksAdapter: ProfileTasksRecyclerAdapter
    private lateinit var recyclerViewToDoAdapter: ProfileToDoListRecyclerAdapter

    private val dailyTasks = mutableListOf<Task>()

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        profileViewModel.checkSurveyStatus()
        profileViewModel.getUserName()
        profileViewModel.getPicture()
        profileViewModel.getAllTasks()

        profileViewModel.userSurveyLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    it.data?.let { state -> updateUI(state) }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })

        profileViewModel.surveyProfileLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    if (it.data != null)
                        profileViewModel.getDailyTasks(it.data)

                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        profileViewModel.refreshDailyTaskLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    it.data?.let { state ->

                        if (state)
                            profileViewModel.getSurveyProfile()
                    }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        profileViewModel.userNameLiveData.observe(viewLifecycleOwner, {

            profile_tv_username.text = it
        })

        profileViewModel.userGetPictureLiveData.observe(viewLifecycleOwner, {

            setProfilePicture(it)
        })

        profileViewModel.userSetPictureLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    profileViewModel.getPicture()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })

        profileViewModel.tasksLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    val dailyTasks = mutableListOf<Task>()

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    it.data?.forEach{ dailyTask ->

                        if (dailyTask.reminder == 1 && dailyTask.allDay)
                            dailyTasks.add(dailyTask)
                    }

                    recyclerViewTasksAdapter.submitList(dailyTasks)
                    recyclerViewTasksAdapter.notifyDataSetChanged()

                    recyclerViewToDoAdapter.submitList(it.data)
                    recyclerViewToDoAdapter.notifyDataSetChanged()
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })

        profileViewModel.dailyTasksLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    it.data?.forEach { task ->

                        dailyTasks.add(task)
                    }

                    it.data?.first()?.let { task ->

                        profileViewModel.getTaskID(task)
                    }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        profileViewModel.taskIDLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    dailyTasks[0].id = it.data?.id

                    dailyTasks.forEachIndexed { index, task ->

                        if (index != 0)
                            if (task.id == null)
                                task.id = dailyTasks[0].id?.plus(index)
                    }

                    dailyTasks.forEach { task ->

                        profileViewModel.addDailyTask(task)
                    }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })

        profileViewModel.dailyTaskLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    profileViewModel.getAllTasks()
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

    @ExperimentalCoroutinesApi
    private fun updateUI(state: Boolean) {

        if (state) {

            profile_tv_daily_duties.visibility = View.VISIBLE
            profile_recycler_view_tasks.visibility = View.VISIBLE
            profileViewModel.checkDailyTasks()

        } else {

            profile_tv_daily_duties.visibility = View.GONE
            profile_recycler_view_tasks.visibility = View.GONE
        }
    }

    override fun onItemTaskClick(position: Int, task: Task) {}
    override fun onItemToDoClick(position: Int, task: Task) {}
}