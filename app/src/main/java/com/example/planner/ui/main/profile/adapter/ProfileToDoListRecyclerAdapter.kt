package com.example.planner.ui.main.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planner.data.model.Task
import com.example.planner.databinding.ItemTaskListBinding

class ProfileToDoListRecyclerAdapter (
    private val listener: OnItemListener? = null
): ListAdapter<Task, ProfileToDoListRecyclerAdapter.ViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, listener)    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        holder.bind(item)
    }

    class ViewHolder private constructor(
        private val binding: ItemTaskListBinding,
        private val listener: OnItemListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task) {
            binding.taskListItem = item

            binding.itemTaskListCardLayout.setOnClickListener {
                listener?.onItemToDoClick(adapterPosition, item)
            }

            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup, listener: OnItemListener?): ViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ItemTaskListBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, listener)
            }
        }
    }

    interface OnItemListener {

        fun onItemToDoClick(position: Int, task: Task)
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