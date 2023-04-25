package com.example.ft_hangouts.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.databinding.SearchRecyclerItemViewBinding

class SearchRecyclerViewAdapter(private val listener: View.OnClickListener):
    ListAdapter<ContactDomainModel, SearchRecyclerViewAdapter.SearchRecyclerViewHolder>(callback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecyclerViewHolder {
        return SearchRecyclerViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: SearchRecyclerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class SearchRecyclerViewHolder private constructor(
        private val binding: SearchRecyclerItemViewBinding,
        private val listener: View.OnClickListener
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactDomainModel) {
            binding.root.setOnClickListener(listener)
            binding.nameText.text = item.name
            if (item.profile != null)
                binding.profileImg.setImageBitmap(item.profile)
            else
                binding.profileImg.setImageResource(R.drawable.ic_default_profile)
            binding.phoneNumberText.text = item.phoneNumber
        }

        companion object {
            fun from(parent: ViewGroup, listener: OnClickListener): SearchRecyclerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SearchRecyclerItemViewBinding.inflate(layoutInflater)
                binding.profileImg.clipToOutline = true
                return SearchRecyclerViewHolder(binding, listener)
            }
        }
    }

    companion object {
        val callback = object: DiffUtil.ItemCallback<ContactDomainModel>() {
            override fun areItemsTheSame(
                oldItem: ContactDomainModel,
                newItem: ContactDomainModel
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: ContactDomainModel,
                newItem: ContactDomainModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}