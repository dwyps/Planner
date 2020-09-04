package com.example.planner.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.planner.R
import com.example.planner.data.model.Task
import kotlinx.android.synthetic.main.item_icon.view.*

class GridViewAdapter constructor(
    private val icons: List<Int>
) : BaseAdapter() {

    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = View.inflate(parent.context, R.layout.item_icon, null)

        view.icon_iv.setImageResource(icons[position])

        return view
    }
}