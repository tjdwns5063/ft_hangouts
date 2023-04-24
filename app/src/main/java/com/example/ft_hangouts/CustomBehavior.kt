package com.example.ft_hangouts

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

class CustomBehavior(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<TextView?>(context, attrs) {
    private var startHeight = 0f
    private var startToolbarHeight = 0f

    private val maxToolbarHeight = 150 * context.resources.displayMetrics.density
    private val minToolbarHeight = 249f

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
            Log.i("behavior", "changed called")
            initProperties(child, dependency)

            val currentToolbarHeight =
                startToolbarHeight + dependency.getY() // current expanded height of toolbar

            Log.i("behavior", "currHeight: $currentToolbarHeight")

            //531, 249
            val childAlpha = (currentToolbarHeight / minToolbarHeight) - 1
            Log.i("behavior", "alpha: $childAlpha")
            child.alpha = childAlpha

            return true
        }
        return super.onDependentViewChanged(parent!!, child, dependency)
    }

    private fun initProperties(
        child: TextView,
        dependency: View
    ) {
        if (!initialized) {
            // form initial layout
            startHeight = child.height.toFloat()
            startToolbarHeight = dependency.height.toFloat()
            // some calculated fields
            initialized = true
        }
    }
}