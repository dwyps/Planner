package com.example.planner.ui.main.tasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.data.model.Task
import com.example.planner.databinding.ItemTaskBinding

class TasksTaskRecyclerAdapter (
    private val listener: OnItemListener? = null
): ListAdapter<Task, TasksTaskRecyclerAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
    }

    class ViewHolder private constructor(
        private val binding: ItemTaskBinding,
        private val listener: OnItemListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task) {
            binding.taskItem = item

            binding.itemTaskCardLayout.setOnClickListener {
                listener?.onItemClick(adapterPosition, item)
            }

            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup, listener: OnItemListener?): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, listener)
            }
        }
    }

    interface OnItemListener {

        fun onItemClick(position: Int, task: Task)
    }

    class HomeDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}