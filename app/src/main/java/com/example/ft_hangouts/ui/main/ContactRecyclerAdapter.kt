package com.example.ft_hangouts.ui.main

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.data.contact_database.Contact
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding

class ContactRecyclerAdapter(
        private val clickListener: OnClickListener
    ): RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder>() {
    private var items: List<Contact> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(contacts: List<Contact>) {
        items = contacts
        notifyDataSetChanged()
    }

    fun getIdByPosition(position: Int): Long {
        return items[position].id
    }

    class ContactViewHolder private constructor(
            private val binding: RecyclerItemViewBinding,
            val clickListener: OnClickListener
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.idText.text = contact.id.toString()
            binding.nameText.text = contact.name
            binding.root.setOnClickListener(clickListener)
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: OnClickListener): ContactViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemViewBinding.inflate(layoutInflater, parent, false)

                return ContactViewHolder(binding, clickListener)
            }
        }
    }
}