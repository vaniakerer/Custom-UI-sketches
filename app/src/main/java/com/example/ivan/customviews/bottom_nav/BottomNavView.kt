package com.example.ivan.customviews.bottom_nav

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class BottomNavView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val selectedTabCirclePaint = Paint()

    private val navigationPaint = Paint()
    private val navigationPath = Path()

    private val circleRadius = 100f
    private val circlePadding = 10f
    private var tabWidth = 0f

    private val tabsCount = 4
    private var selectedTab = 1

    private val selectedTabCirclePoint = Point()

    init {
        navigationPaint.color = Color.CYAN
        navigationPaint.style = Paint.Style.FILL
        navigationPaint.strokeWidth = 3f
        navigationPaint.isAntiAlias = true

        selectedTabCirclePaint.color = Color.RED
        selectedTabCirclePaint.isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        if (canvas == null) return

        navigationPath.reset()

        val beziePathStartP1X = selectedTab * tabWidth

        navigationPath.moveTo(0f, circleRadius)
        navigationPath.lineTo(100f, circleRadius)
        navigationPath.cubicTo(
                100F + circleRadius * 2 * 0.05F, circleRadius + circleRadius * 4 / 3.05F,
                300F - circleRadius * 2 * 0.05F, circleRadius + circleRadius * 4 / 3.05F,
                300F, circleRadius
        )

        navigationPath.lineTo(measuredWidth.toFloat(), circleRadius)
        navigationPath.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
        navigationPath.lineTo(0f, measuredHeight.toFloat())
        navigationPath.lineTo(0f, circleRadius)

        canvas.drawPath(navigationPath, navigationPaint)

        canvas.drawCircle(200f, circleRadius, circleRadius - 20, selectedTabCirclePaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tabWidth = (w / tabsCount).toFloat()

    }
}