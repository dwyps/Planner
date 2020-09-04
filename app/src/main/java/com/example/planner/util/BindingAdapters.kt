package com.example.planner.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.planner.R
import com.example.planner.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("bindThumbnail")
fun bindThumbnail(imgView: ImageView, task: Task) {

    task.let {

        Glide.with(imgView.context)
            .load(task.icon)
            .apply(
                RequestOptions()
                    .placeholder(R.color.colorPrimary)
                    .error(R.drawable.ic_profile)
            )
            .into(imgView)
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("bindTaskDate")
fun bindDate(textView: TextView, date: Long) {


    val calendar = Calendar.getInstance()

    date.let {

        if (calendar.timeInMillis == date) {

            textView.text = "TODAY"

        } else {

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            val dateString: String = simpleDateFormat.format(it)

            textView.text = dateString
        }
    }
}