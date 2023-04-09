package com.example.ft_hangouts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.databinding.SmsChatRecyclerReceiveItemViewBinding
import com.example.ft_hangouts.databinding.SmsChatRecyclerSendItemViewBinding
import com.example.ft_hangouts.sms.SmsInfo

class SmsChatRecyclerAdapter(private val scrollToBottom: (Int) -> Unit): ListAdapter<SmsInfo, RecyclerView.ViewHolder>(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> SmsChatRecyclerReceiveViewHolder.from(parent)
            else -> SmsChatRecyclerSendViewHolder.from(parent)
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<SmsInfo>,
        currentList: MutableList<SmsInfo>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        scrollToBottom(currentList.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> (holder as SmsChatRecyclerReceiveViewHolder).bind(currentList[position])
            else -> (holder as SmsChatRecyclerSendViewHolder).bind(currentList[position])
        }
    }

    class SmsChatRecyclerReceiveViewHolder private constructor(
        private val binding: SmsChatRecyclerReceiveItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SmsInfo) {
            binding.smsChatItemReceiveText.text = item.content
        }

        companion object {
            fun from(parent: ViewGroup): SmsChatRecyclerReceiveViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SmsChatRecyclerReceiveItemViewBinding.inflate(layoutInflater, parent, false)
                return SmsChatRecyclerReceiveViewHolder(binding)
            }
        }
    }

    class SmsChatRecyclerSendViewHolder private constructor(
        private val binding: SmsChatRecyclerSendItemViewBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmsInfo) {
            binding.smsChatItemSendText.text = item.content
        }

        companion object {
            fun from(parent: ViewGroup): SmsChatRecyclerSendViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SmsChatRecyclerSendItemViewBinding.inflate(layoutInflater, parent, false)
                return SmsChatRecyclerSendViewHolder(binding)
            }
        }
    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<SmsInfo>() {
            override fun areItemsTheSame(oldItem: SmsInfo, newItem: SmsInfo): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: SmsInfo, newItem: SmsInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}