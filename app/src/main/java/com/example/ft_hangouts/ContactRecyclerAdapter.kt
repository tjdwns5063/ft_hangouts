package com.example.ft_hangouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding

class ContactRecyclerAdapter(val items: MutableList<Contact>): RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(comps: MutableList<Contact>) {
        for (i in items.size until comps.size)
            items += comps[i]
        notifyItemChanged(items.size)
    }

    class ContactViewHolder private constructor(private val binding: RecyclerItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.idText.text = contact.id.toString()
            binding.nameText.text = contact.name
            binding.phoneNumberText.text = contact.phoneNumber
        }

        companion object {
            fun from(parent: ViewGroup): ContactViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemViewBinding.inflate(layoutInflater, parent, false)

                return ContactViewHolder(binding)
            }
        }
    }
}