package com.example.ft_hangouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.databinding.SmsChatRecyclerReceiveItemViewBinding
import com.example.ft_hangouts.databinding.SmsChatRecyclerSendItemViewBinding
import com.example.ft_hangouts.sms.SmsInfo

class SmsChatRecyclerAdapter(val currentList: MutableList<SmsInfo>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> SmsChatRecyclerReceiveViewHolder.from(parent)
            else -> SmsChatRecyclerSendViewHolder.from(parent)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
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

    fun update(list: MutableList<SmsInfo>) {
        var cnt: Int = 0
        for (i in currentList.size until list.size) {
            currentList += list[i]
            ++cnt
        }
        notifyItemRangeChanged(currentList.size, cnt)
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
}
