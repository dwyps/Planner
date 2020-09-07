package com.example.planner.ui.main.tasks.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.ui.main.tasks.TasksFragmentDirections
import com.example.planner.ui.main.tasks.TasksViewModel
import com.example.planner.ui.main.tasks.adapter.TasksTaskRecyclerAdapter
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tasks_recycler_view_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class TasksThisMonthFragment : Fragment(R.layout.tasks_recycler_view_fragment), TasksTaskRecyclerAdapter.OnItemListener {

    @Inject
    lateinit var tasksViewModel: TasksViewModel

    private lateinit var taskRecyclerAdapter: TasksTaskRecyclerAdapter

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {

        tasks_recycler_view.apply {

            taskRecyclerAdapter = TasksTaskRecyclerAdapter(this@TasksThisMonthFragment)

            adapter = taskRecyclerAdapter
        }
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        tasksViewModel.getAllTasks()

        tasksViewModel.tasksLiveData.observe(viewLifecycleOwner, {

            when(it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility = View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    if(it.data != null) {

                        taskRecyclerAdapter.submitList(tasksViewModel.getThisMonthTasks(it.data))
                        taskRecyclerAdapter.notifyDataSetChanged()
                    }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        })
    }

    override fun onItemClick(position: Int, task: Task) {

        findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToTaskForm(task.id!!))
    }
}