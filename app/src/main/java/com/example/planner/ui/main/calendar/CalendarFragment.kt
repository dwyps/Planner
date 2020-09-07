package com.example.planner.ui.main.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.ui.main.MainActivity
import com.example.planner.ui.main.profile.adapter.ProfileTasksRecyclerAdapter
import com.example.planner.ui.main.profile.adapter.ProfileToDoListRecyclerAdapter
import com.example.planner.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar),
    ProfileToDoListRecyclerAdapter.OnItemListener {

    @Inject
    lateinit var calendarViewModel: CalendarViewModel

    private lateinit var recyclerViewToDoListAdapter: ProfileToDoListRecyclerAdapter

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
        initCalendar()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).ensureBottomNavigation()
    }

    @ExperimentalCoroutinesApi
    private fun initViewModel() {

        calendarViewModel.getAllTasks()


        calendarViewModel.tasksLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility =
                    View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    it.data?.let { tasks -> addEvents(tasks) }
                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                }
            }
        })

        calendarViewModel.currentDayTasksLiveData.observe(viewLifecycleOwner, {

            when (it.status) {

                Resource.Status.LOADING -> requireActivity().activity_main_spinner.visibility =
                    View.VISIBLE

                Resource.Status.SUCCESS -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE

                    if (it.data.isNullOrEmpty()) {

                        recyclerViewToDoListAdapter.submitList(it.data)
                        recyclerViewToDoListAdapter.notifyDataSetChanged()

                    } else {

                        recyclerViewToDoListAdapter.submitList(it.data)
                        recyclerViewToDoListAdapter.notifyDataSetChanged()
                    }

                }

                Resource.Status.ERROR -> {

                    requireActivity().activity_main_spinner.visibility = View.GONE
                }
            }
        })

    }

    @ExperimentalCoroutinesApi
    private fun initCalendar() {

        calendar_view.setOnDayClickListener(object : OnDayClickListener {

            override fun onDayClick(eventDay: EventDay) {

                calendarViewModel.getCurrentDayTasks(eventDay.calendar.time)
            }
        })
    }

    private fun addEvents(tasks: List<Task>) {

        val eventList = mutableListOf<EventDay>()

        tasks.forEach {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.dateFrom

            eventList.add(EventDay(calendar, it.icon, it.color))
        }

        calendar_view.setEvents(eventList)
    }

    private fun initListeners() {

        requireActivity().fab.setOnClickListener {

            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToTaskForm())
        }
    }

    private fun initRecyclerView() {
        recyclerViewToDoListAdapter = ProfileToDoListRecyclerAdapter(this)

        calendar_recycler_view_to_do_list.adapter = recyclerViewToDoListAdapter
    }

    override fun onItemToDoClick(position: Int, task: Task) {}
}