package com.example.ivan.customviews.bottom_nav

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

    private val circleRadius = 80f
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

        selectedTabCirclePaint.color = Color.CYAN
        selectedTabCirclePaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {

        if (canvas == null) return

        navigationPath.reset()

        val beziePathStartP1X = selectedTab * tabWidth

        navigationPath.moveTo(0f, circleRadius)
        navigationPath.lineTo(beziePathStartP1X, circleRadius)
        navigationPath.cubicTo(
                beziePathStartP1X + circleRadius - circleRadius / 3, circleRadius,
                beziePathStartP1X - circlePadding, circleRadius * 2,
                beziePathStartP1X + tabWidth / 2, circleRadius * 2 + circlePadding
        )

        val beziePathStartP2X = beziePathStartP1X + tabWidth / 2

        navigationPath.cubicTo(
                beziePathStartP2X + tabWidth / 2 - circlePadding, circleRadius * 2,
                beziePathStartP2X + tabWidth / 2 - circleRadius + circleRadius / 3, circleRadius,
                beziePathStartP2X + tabWidth / 2, circleRadius
        )


        navigationPath.lineTo(measuredWidth.toFloat(), circleRadius)
        navigationPath.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
        navigationPath.lineTo(0f, measuredHeight.toFloat())
        navigationPath.lineTo(0f, circleRadius)

        canvas.drawPath(navigationPath, navigationPaint)

        canvas.drawCircle((selectedTab + 1) * tabWidth - tabWidth / 2, circleRadius, circleRadius, selectedTabCirclePaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tabWidth = (w / tabsCount).toFloat()

    }
}