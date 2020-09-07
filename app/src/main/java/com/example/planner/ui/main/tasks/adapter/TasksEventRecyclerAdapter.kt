package com.example.planner.ui.main.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.data.model.Event
import com.example.planner.databinding.ItemEventBinding

class TasksEventRecyclerAdapter ()
    : ListAdapter<Event, TasksEventRecyclerAdapter.ViewHolder>(TasksEventRecyclerAdapter.HomeDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksEventRecyclerAdapter.ViewHolder {
        return TasksEventRecyclerAdapter.ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TasksEventRecyclerAdapter.ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
    }

    class ViewHolder private constructor(
        private val binding: ItemEventBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Event) {
            binding.eventItem = item

            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ItemEventBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    class HomeDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}