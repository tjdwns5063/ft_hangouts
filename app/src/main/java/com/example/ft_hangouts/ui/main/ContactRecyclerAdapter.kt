package com.example.ft_hangouts.ui.main

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.R
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding
import com.example.ft_hangouts.ui.main.ContactRecyclerAdapter.ContactViewHolder.Companion.callback

class ContactRecyclerAdapter(
    private val clickListener: OnClickListener,
    ): ListAdapter<Contact, ContactRecyclerAdapter.ContactViewHolder>(callback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemId(position: Int): Long {
        return currentList[position].id
    }

    fun redraw(position: Int) {
        notifyItemChanged(position)
    }

    class ContactViewHolder private constructor(
            private val binding: RecyclerItemViewBinding,
            private val clickListener: OnClickListener
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.contact = contact
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

            val callback = object : DiffUtil.ItemCallback<Contact>() {
                override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}