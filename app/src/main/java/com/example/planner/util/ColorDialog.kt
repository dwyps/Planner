package com.example.planner.util

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import com.example.planner.R
import kotlinx.android.synthetic.main.dialog_task_icons.*

class ColorDialog constructor(
    activity: Activity,
    private val adapter: GridViewAdapter,
    private val listener: OnItemListener
): Dialog(activity), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_task_icons)

        initGrid()
    }

    private fun initGrid() {

        dialog_icon_grid_view.adapter = adapter
        dialog_icon_grid_view.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->

                listener.onColorClick(position)
                dismiss()
            }
    }

    interface OnItemListener {

        fun onColorClick(position: Int)
    }

    override fun onClick(v: View?) {
        dismiss()
    }
}