package com.example.planner.ui.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.planner.R
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : FragmentActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FULLSCREEN
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onStart() {
        super.onStart()

        setupNavigation()
    }

    private fun setupNavigation() {

        navController = findNavController(R.id.nav_host_main)
        bottom_nav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id) {

                R.id.taskForm -> {

                    bottom_nav.visibility = View.GONE
                    fab.visibility = View.GONE
                }

                R.id.settingsFragment -> {

                    bottom_nav.visibility = View.GONE
                    fab.visibility = View.GONE
                }

                else -> {

                    bottom_nav.visibility = View.VISIBLE
                    fab.visibility = View.VISIBLE
                }
            }

        }
    }

    fun ensureBottomNavigation() {

        if(bottom_nav.translationY != 0f) {

            val layoutBottomParams = bottom_nav.layoutParams as CoordinatorLayout.LayoutParams
            val behaviorBottom = layoutBottomParams.behavior as HideBottomViewOnScrollBehavior

            val layoutFabParams = fab.layoutParams as CoordinatorLayout.LayoutParams
            val behaviorFab = layoutFabParams.behavior as HideBottomViewOnScrollBehavior

            behaviorBottom.slideUp(bottom_nav)
            behaviorFab.slideUp(fab)
        }
    }
}