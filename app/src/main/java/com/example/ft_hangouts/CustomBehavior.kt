package com.example.ft_hangouts

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.ft_hangouts.util.dpToPixel
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.max

class CustomBehavior(private val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView?>(context, attrs) {
    private var startToolbarHeight = 0f
    private var finalToolbarHeight = 0f
    private var initialized = false

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout) {
            initProperties(child, dependency)

            val currentToolbarHeight = max(finalToolbarHeight - dependency.y * 2, finalToolbarHeight)
            val totalMoved = startToolbarHeight - finalToolbarHeight
            val moved = startToolbarHeight - currentToolbarHeight
            val progress = moved / totalMoved

            child.alpha = progress
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    private fun initProperties(
        child: TextView,
        dependency: View
    ) {
        if (!initialized) {
            val tv = TypedValue()
            context.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)
            finalToolbarHeight = TypedValue.complexToDimension(tv.data, context.resources.displayMetrics) + dpToPixel(16f)
            startToolbarHeight = dependency.height.toFloat()
            initialized = true
        }
    }
}

class ReverseCustomBehavior(private val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView?>(context, attrs) {
    private var startToolbarHeight = 0f
    private var finalToolbarHeight = 0f
    private var initialized = false

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean {
        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: TextView,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout) {
            initProperties(child, dependency)
            val currentToolbarHeight = max(finalToolbarHeight - dependency.getY(), finalToolbarHeight)
            val totalMoved = startToolbarHeight - finalToolbarHeight
            val moved = startToolbarHeight - currentToolbarHeight
            val progress = moved / totalMoved

            child.translationY = dpToPixel(16f)
            child.alpha = 1f - progress

            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    private fun initProperties(
        child: TextView,
        dependency: View
    ) {
        if (!initialized) {
            val tv = TypedValue()
            context.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)
            finalToolbarHeight = TypedValue.complexToDimension(tv.data, context.resources.displayMetrics) + dpToPixel(16f)
            startToolbarHeight = dependency.height.toFloat()
            initialized = true
        }
    }
}