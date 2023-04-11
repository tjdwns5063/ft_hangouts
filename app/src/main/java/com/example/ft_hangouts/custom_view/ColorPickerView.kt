package com.example.ft_hangouts.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import com.example.ft_hangouts.R
import kotlin.math.*

/*
 * 엄재웅님의 ColorPickerView를 참고하여 만들었습니다. https://github.com/skydoves/ColorPickerView
 */

fun interface ColorListener {
    fun onColorListener(@ColorInt color: Int)
}

class ColorPickerView(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs) {
    private val palette: ImageView = ImageView(context)
    private val selector: ImageView = ImageView(context)
    private var paletteDrawable: Drawable? = null
    private var selectorDrawable: Drawable? = null
    private var selectorSize: Int = 10
    private var colorListener: ColorListener = ColorListener {}
    @ColorInt private var selectedColor: Int = Color.HSVToColor(floatArrayOf(0f, 0f, 1f))

    init {
        getAttrs(attrs)
        initialize()
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView)

        try {
            if (a.hasValue(R.styleable.ColorPickerView_selectorSize)) {
                selectorSize = a.getDimensionPixelSize(R.styleable.ColorPickerView_selectorSize, 10)
            }
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context): this(context, null) {
        initialize()
    }

    private fun initialize() {
        selectorDrawable = AppCompatResources.getDrawable(context, R.drawable.selector_drawable)!!
        palette.setImageDrawable(paletteDrawable)
        selector.setImageDrawable(selectorDrawable)

        val paletteParam = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val selectorParam = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        paletteParam.gravity = Gravity.CENTER
        selectorParam.gravity = Gravity.CENTER
        selectorParam.width = selectorSize
        selectorParam.height = selectorSize

        addView(palette, paletteParam)
        addView(selector, selectorParam)
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?:  return false
        return when (event.actionMasked) {
            MotionEvent.ACTION_UP -> {
                true
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                onTouchReceived(event)
                true
            }
            else -> false
        }
    }

    private fun onTouchReceived(event: MotionEvent) {
        val touchPoint = revisePoint(Point(event.x.toInt(), event.y.toInt()))
        val color = findProperColor(touchPoint.x.toFloat(), touchPoint.y.toFloat())

        selector.x = touchPoint.x - (selector.width * 0.5f)
        selector.y = touchPoint.y - (selector.height * 0.5f)
        selectedColor = color
        colorListener.onColorListener(selectedColor)
        invalidate()
    }

    private fun revisePoint(point: Point): Point {
        val centerX = measuredWidth * 0.5f
        val centerY = measuredHeight * 0.5f
        val radius = min(measuredWidth, measuredHeight) * 0.5f

        var x = point.x - centerX // abs하면 안됐네.... ㅠㅠ
        var y = point.y - centerY
        val r = sqrt(x*x + y*y)

        if (r > radius) { // 터치된 좌표가 원의 반지름보다 크면
            x *= radius / r // 큰 비율만큼 줄여줌
            y *= radius / r
        }
        return Point((x + centerX).toInt(), (y + centerY).toInt())
    }

    private fun findProperColor(x: Float, y: Float): Int {
        val hsv = floatArrayOf(0f, 0f, 1f)
        val nx = (x - width * 0.5f)
        val ny = (y - height * 0.5f)
        val r = sqrt((nx * nx + ny * ny).toDouble())
        val radius = min(width, height) * 0.5f

        hsv[0] = (atan2(ny.toDouble(), -nx.toDouble()) / PI * 180f).toFloat() + 180// x(Degree) = PI / radian * 180
        hsv[1] = max(0f, min(1f, (r / radius).toFloat()))

        return Color.HSVToColor(hsv)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (palette.drawable == null) {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            paletteDrawable = PaletteDrawable(resources, bitmap)
            palette.setImageDrawable(PaletteDrawable(resources, bitmap))
        }
    }



    fun setOnColorListener(OnColorListener: ColorListener) {
        colorListener = OnColorListener
    }
}