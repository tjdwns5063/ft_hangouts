package com.example.ft_hangouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.databinding.SmsChatRecyclerItemViewBinding

class SmsChatRecyclerAdapter: RecyclerView.Adapter<SmsChatRecyclerAdapter.SmsChatRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsChatRecyclerViewHolder {
        return SmsChatRecyclerViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: SmsChatRecyclerViewHolder, position: Int) {

    }

    class SmsChatRecyclerViewHolder private constructor(
        private val binding: SmsChatRecyclerItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.smsChatItemReceiveText.text = item
        }

        companion object {
            fun from(parent: ViewGroup): SmsChatRecyclerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SmsChatRecyclerItemViewBinding.inflate(layoutInflater, parent, false)
                return SmsChatRecyclerViewHolder(binding)
            }
        }
    }
}