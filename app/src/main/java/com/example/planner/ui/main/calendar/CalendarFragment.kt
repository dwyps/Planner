package com.example.planner.ui.main.calendar

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.example.planner.R
import com.example.planner.data.model.Task
import com.example.planner.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    @Inject
    lateinit var calendarViewModel: CalendarViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initListeners()
        initCalendar()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).ensureBottomNavigation()
    }

    private fun initViewModel() {

    }

    private fun initCalendar() {

        addEvents()

        calendar_view.setOnDayClickListener(object : OnDayClickListener {

            override fun onDayClick(eventDay: EventDay) {
                Timber.e("Event: %s", eventDay.calendar.time)

//                calendarViewModel.getTasks(eventDay.calendar.time)
                getTasks(eventDay.calendar.time)
            }
        })
    }

    private fun addEvents() {

//        val tasks = calendarViewModel.getAllTasks()
        val taskList = getAllTasks()
        val calendar = Calendar.getInstance()

        calendar.set(2020,8,3)

        val eventList = mutableListOf<EventDay>()

        taskList.forEach {

            eventList.add(EventDay(calendar, it.icon, it.color))
        }

        calendar_view.setEvents(eventList)
    }

    private fun getAllTasks(): List<Task> {

        val taskList = mutableListOf<Task>()

//        taskList.add(
//            Task(
//                1,
//                true,
//                "Clean House",
//                "Clean Whole House",
//                R.drawable.ic_birthday,
//                Color.BLUE,
//                Date(2020, 9, 3),
//                true,
//                1
//            )
//        )
//
//        taskList.add(
//            Task(
//                1,
//                true,
//                "Clean House",
//                "Clean Whole House",
//                R.drawable.ic_drink,
//                Color.BLUE,
//                Date(2020, 9, 3),
//                true,
//                1
//            )
//        )

        return taskList
    }

    private fun getTasks(date: Date) {

        val taskList = mutableListOf<Task>()

//        taskList.add(
//            Task(
//                1,
//                true,
//                "Clean House",
//                "Clean Whole House",
//                R.drawable.ic_birthday,
//                Color.BLUE,
//                Date(2020, 9, 2),
//                true,
//                1
//            )
//        )
//
//        taskList.add(
//            Task(
//                1,
//                true,
//                "Clean House",
//                "Clean Whole House",
//                R.drawable.ic_drink,
//                Color.BLUE,
//                Date(2020, 9, 7),
//                true,
//                1
//            )
//        )
//
//        if (date == taskList[0].date)
//            calendar_tv_tasks.visibility = View.VISIBLE
//        else
//            calendar_tv_tasks.visibility = View.GONE

    }

    private fun initListeners() {

        requireActivity().fab.setOnClickListener {

            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToTaskForm())
        }
    }
}