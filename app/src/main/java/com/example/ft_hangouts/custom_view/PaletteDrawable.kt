package com.example.ft_hangouts.custom_view

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import kotlin.math.min

class PaletteDrawable(resources: Resources, bitmap: Bitmap): BitmapDrawable(resources, bitmap) {
    private val huePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val saturationPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val radius = min(width, height) * 0.5f

        val sweepShader: Shader = SweepGradient(
            centerX,
            centerY,
            intArrayOf(Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED),
            floatArrayOf(0.000f, 0.166f, 0.333f, 0.499f, 0.666f, 0.833f, 0.999f) // 각 6등분 지점에..
        )

        val saturationShader: Shader = RadialGradient(
            centerX, centerY, radius, Color.WHITE, 0x00FFFFFF, Shader.TileMode.CLAMP
        )

        huePaint.shader = sweepShader
        saturationPaint.shader = saturationShader

        canvas.drawCircle(centerX, centerY, radius, huePaint)
        canvas.drawCircle(centerX, centerY, radius, saturationPaint)
    }
}