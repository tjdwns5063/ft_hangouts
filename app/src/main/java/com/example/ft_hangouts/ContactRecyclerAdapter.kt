package com.example.ft_hangouts

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.database.Contact
import com.example.ft_hangouts.databinding.RecyclerItemViewBinding

class ContactRecyclerAdapter(
        private val items: MutableList<Contact>,
        private val clickListener: OnClickListener
    ): RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(contact: Contact) {
        items.add(contact)
        notifyItemChanged(items.size)
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