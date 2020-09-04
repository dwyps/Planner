package com.example.planner.ui.main.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.ui.main.MainActivity
import com.example.planner.ui.main.calendar.CalendarFragmentDirections
import com.example.planner.ui.main.tasks.adapter.TasksTaskRecyclerAdapter
import com.example.planner.ui.main.tasks.adapter.TasksViewPagerAdapter
import com.example.planner.util.Resource
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksTaskRecyclerAdapter.OnItemListener {

    @Inject
    lateinit var tasksViewModel: TasksViewModel

    private lateinit var viewPagerAdapter: TasksViewPagerAdapter

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViewPager()
        initRecyclerView()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).ensureBottomNavigation()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        tasksViewModel.tasksLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                }
            }
        })
    }

    private fun initListeners() {

        requireActivity().fab.setOnClickListener {

            findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToTaskForm())
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = TasksViewPagerAdapter(this, 3)

        tasks_view_pager.adapter = viewPagerAdapter
        tasks_view_pager.isUserInputEnabled = false
        tasks_view_pager.requestDisallowInterceptTouchEvent(true)

        TabLayoutMediator(tasks_tab_layout, tasks_view_pager) { tab, position ->

            when(position) {

                0 -> {
                    tab.text = "Today"
                }

                1 -> {
                    tab.text = "This week"
                }

                2 -> {
                    tab.text = "This month"
                }
            }

        }.attach()

    }

    private fun initRecyclerView() {}
    override fun onItemClick(position: Int, task: Task) {}
}