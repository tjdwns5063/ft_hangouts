package com.example.ft_hangouts.ui.main

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.*
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

fun interface ItemTouchHelperListener {
    fun onItemSwipe(position: Int, direction: Int)
}
class ContactTouchHelperCallback(private val listener: ItemTouchHelperListener): ItemTouchHelper.Callback() {
    private val animator = ValueAnimator.ofArgb(Color.WHITE, Color.GREEN).apply {
        duration = 1000
    }
    private var startX: Float = 0f
    private var startY : Float = 0f
    private var buttonState = ItemTouchHelper.ACTION_STATE_IDLE
    private val leftPaint =  Paint()
    private val rightPaint = Paint()

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            0, // drag unused.
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.i("swipe", "onSwiped Called")
        listener.onItemSwipe(viewHolder.absoluteAdapterPosition, direction)
        buttonState = direction
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//        Log.i("swipe", "onChildDraw Called")
        if (startX == 0f && startY == 0f) {
            startX = viewHolder.itemView.x
            startY = viewHolder.itemView.y
        }
//        Log.i("swipe", "startX: $startX startY: $startY")
//        Log.i("swipe", "dx: $dX dy: $dY")
//        Log.i("swipe", "width: ${viewHolder.itemView.width} height: ${viewHolder.itemView.height}")
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < startX) {
                buttonState = ItemTouchHelper.LEFT
            } else {
                buttonState = ItemTouchHelper.RIGHT
            }
            when (buttonState) {
                ItemTouchHelper.LEFT -> {
                    val width = viewHolder.itemView.width.toFloat()
                    if (dX.toInt() >= 0)
                        return
                    val progress = -dX / width
                    val alpha = (progress * 100).toInt()

                    viewHolder.itemView.backgroundTintList = ColorStateList.valueOf(Color.YELLOW).withAlpha(alpha)
                }

                ItemTouchHelper.RIGHT -> {
                    val width = viewHolder.itemView.width.toFloat()
                    if (dX.toInt() == 0)
                        return
                    val progress = dX / width
                    val alpha = (progress * 100).toInt()

                    viewHolder.itemView.backgroundTintList = ColorStateList.valueOf(Color.GREEN).withAlpha(alpha)
                }
            }
        } else {
            buttonState = ItemTouchHelper.ACTION_STATE_IDLE
        }
    }
}

class ContactRecyclerAdapter(
        private val clickListener: OnClickListener
    ): ListAdapter<ContactDomainModel, ContactRecyclerAdapter.ContactViewHolder>(callback) {
//    private var items: List<ContactDomainModel> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun getIdByPosition(position: Int): Long {
        return currentList[position].id
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