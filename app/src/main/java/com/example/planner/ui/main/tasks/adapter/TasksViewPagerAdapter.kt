package com.example.planner.ui.main.tasks.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.planner.ui.main.tasks.tabs.TasksThisMonthFragment
import com.example.planner.ui.main.tasks.tabs.TasksThisWeekFragment
import com.example.planner.ui.main.tasks.tabs.TasksTodayFragment

class TasksViewPagerAdapter (fragment: Fragment, private val count: Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return count
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {

            0 -> TasksTodayFragment()

            1 -> TasksThisWeekFragment()

            2 -> TasksThisMonthFragment()

            else -> TasksTodayFragment()
        }
    }
}