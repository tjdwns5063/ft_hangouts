package com.example.ft_hangouts.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.ContactDomainModel
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding
import com.example.ft_hangouts.ui.main.ContactRecyclerAdapter.ContactViewHolder.Companion.callback

class ContactRecyclerAdapter(
    private val clickListener: OnClickListener,
    ): ListAdapter<ContactDomainModel, ContactRecyclerAdapter.ContactViewHolder>(callback) {
    private var recentSwipedPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ContactDomainModel>,
        currentList: MutableList<ContactDomainModel>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        if (recentSwipedPosition < 0)
            return
        currentList[recentSwipedPosition].swiped = false
        recentSwipedPosition = -1
    }

    fun getIdByPosition(position: Int): Long {
        return currentList[position].id
    }

    fun redraw(position: Int) {
        val copy: List<ContactDomainModel> = List(currentList.size) { idx ->
            if (idx == position)
                currentList[idx].copy(swiped = true)
            else
                currentList[idx]
        }
        recentSwipedPosition = position
        submitList(copy)
    }

    class ContactViewHolder private constructor(
            private val binding: RecyclerItemViewBinding,
            private val clickListener: OnClickListener
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: ContactDomainModel) {
            if (contact.profile != null) {
                binding.profileImg.setImageBitmap(contact.profile)
            } else {
                binding.profileImg.setImageResource(R.drawable.ic_default_profile)
            }
            binding.nameText.text = contact.name
            binding.root.setOnClickListener(clickListener)
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: OnClickListener): ContactViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemViewBinding.inflate(layoutInflater, parent, false)

                binding.profileImg.clipToOutline = true
                return ContactViewHolder(binding, clickListener)
            }

            val callback = object : DiffUtil.ItemCallback<ContactDomainModel>() {
                override fun areItemsTheSame(oldItem: ContactDomainModel, newItem: ContactDomainModel): Boolean {
                    return oldItem === newItem
                }

                override fun areContentsTheSame(oldItem: ContactDomainModel, newItem: ContactDomainModel): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}