package com.example.ft_hangouts.ui.main

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ft_hangouts.App
import com.example.ft_hangouts.R
import com.example.ft_hangouts.util.dpToPixel

fun interface ItemTouchHelperListener {
    fun onItemSwipe(position: Int, direction: Int)
}
class ContactTouchHelperCallback: ItemTouchHelper.Callback() {
    private var listener: ItemTouchHelperListener = ItemTouchHelperListener { position: Int, direction: Int -> }
    private val paint =  Paint()
    private var startX: Float = 0f

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
        listener.onItemSwipe(viewHolder.absoluteAdapterPosition, direction)
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
        if (startX == 0f)
            startX = dX

        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE)
            return
        val isRightSwipe = dX < 0
        val width = viewHolder.itemView.width

        when (isRightSwipe) {
            true -> {
                drawSms(c, viewHolder)
            }
            false -> {
                drawCall(c, viewHolder)
            }
        }
        val dX = if (isRightSwipe) -dX else dX
        val progress = dX / width

        viewHolder.itemView.alpha = 1f - progress
    }

    fun setSwipeListener(_listener: ItemTouchHelperListener) {
        listener = _listener
    }

    private fun drawSms(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        drawBackground(c, viewHolder, Color.CYAN)
        val drawable = ResourcesCompat.getDrawable(App.INSTANCE.resources, R.drawable.baseline_email_32, null) ?: return
        val drawableBounds = Rect(
            viewHolder.itemView.right - dpToPixel(56),
            viewHolder.itemView.top + dpToPixel(8),
            viewHolder.itemView.right - dpToPixel(16),
            viewHolder.itemView.bottom - dpToPixel(8)
        )

        drawDrawable(c, drawable, drawableBounds)

        val text = viewHolder.itemView.resources.getString(R.string.sms)
        drawText(c, text, viewHolder.itemView.right - dpToPixel(108f), viewHolder.itemView.bottom - dpToPixel(24f))
    }

    private fun drawCall(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        drawBackground(c, viewHolder, Color.GREEN)
        val drawable = ResourcesCompat.getDrawable(App.INSTANCE.resources, R.drawable.baseline_call_24, null) ?: return
        val bounds = Rect(
            viewHolder.itemView.left + dpToPixel(16),
            viewHolder.itemView.top + dpToPixel(8),
            viewHolder.itemView.left + dpToPixel(56),
            viewHolder.itemView.bottom - dpToPixel(8)
        )
        drawDrawable(c, drawable, bounds)

        val text = viewHolder.itemView.resources.getString(R.string.call)
        drawText(c, text, viewHolder.itemView.left + dpToPixel(66f), viewHolder.itemView.bottom - dpToPixel(24f))
    }

    private fun drawBackground(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        color: Int
    ) {
        val itemView = viewHolder.itemView

        val leftButton = RectF(
            (itemView.left).toFloat(),
            (itemView.top).toFloat(),
            (itemView.right).toFloat(),
            (itemView.bottom).toFloat()
        )

        paint.color = color
        c.drawRect(leftButton, paint)
    }

    private fun drawDrawable(
        c: Canvas,
        drawable: Drawable,
        bounds: Rect
    ) {
        drawable.setTintList(ColorStateList.valueOf(Color.WHITE))
        drawable.bounds = bounds
        drawable.draw(c)
    }

    private fun drawText(
        c: Canvas,
        text: String,
        x: Float,
        y: Float
    ) {
        paint.color = Color.WHITE
        paint.isFakeBoldText = true
        paint.textSize = dpToPixel(24f)
        c.drawText(text, x, y, paint)
    }
}