package com.example.ft_hangouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.max

class CustomBehavior(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView?>(context, attrs) {
    private var startHeight = 0f
    private var startToolbarHeight = 0f
    private val finalToolbarHeight = 249f
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
            val progress = (moved / totalMoved * 100).toInt()

            child.alpha = progress * 0.01f
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    private fun initProperties(
        child: TextView,
        dependency: View
    ) {
        if (!initialized) {
            startHeight = child.height.toFloat()
            startToolbarHeight = dependency.height.toFloat()
            initialized = true
        }
    }
}

class ReverseCustomBehavior(private val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView?>(context, attrs) {
    private var startHeight = 0f
    private var startToolbarHeight = 0f
    private val finalToolbarHeight = 249f
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

            child.translationY = -(moved - (16 * context.resources.displayMetrics.density))
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
            startHeight = child.height.toFloat()

            startToolbarHeight = dependency.height.toFloat()
            initialized = true
        }
    }
}