package com.example.ivan.customviews.bottom_nav

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.ivan.customviews.R

public class BottomNavV2 @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val BIZIER_CURVE_FIRST_POINT_X_COEF = 2 * 0.05F
        val BIZIER_CURVE_FIRST_POINT_Y_COEF = 4 / 3.05F
    }

    init {
        attrs?.let { obtainAttributes(it) }
        checkObtainedAttributes()
        initPaints()
    }

    //provided in attributes
    private var tabsCount = 0
    private var selectedTab = 0

    private var tabBarColor: Int = 0
    private var selectedTabCircleColor: Int = 0

    //calculated
    private var tabWidth = 0
    private var selectedTabCircleRadius = 0F
    private var selectedTabCircleDiametr = 0F
    private var selectedTabCircleOffset = 0F
    private var tabBarHeight = 0F

    private var tabBarStartPoint = PointF()

    private lateinit var tabBarPaint: Paint
    private lateinit var selectedTabCirclePaint: Paint

    private val tabBarPath = Path()

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawTabBar(it)
            drawSelectedTabCircle(it)
        }
    }

    private fun drawTabBar(canvas: Canvas) {
        val selectedTabStartX = (paddingLeft + selectedTab * tabWidth).toFloat()
        val selectedTabEndX = (selectedTabStartX + tabWidth)

        val curveStartX = selectedTabStartX + selectedTabCircleRadius - selectedTabCircleOffset
        val curveEndX = curveStartX + selectedTabCircleRadius + selectedTabCircleOffset * 2

        val tabBarCropCircleRadius = (selectedTabCircleRadius + selectedTabCircleOffset)

        val bizierCurveFirstPoint = PointF()
        bizierCurveFirstPoint.x = curveStartX + curveStartX * BIZIER_CURVE_FIRST_POINT_X_COEF
        bizierCurveFirstPoint.y = tabBarCropCircleRadius + tabBarCropCircleRadius * BIZIER_CURVE_FIRST_POINT_Y_COEF

        val bizierCurveSecondPoint = PointF()
        bizierCurveSecondPoint.x = selectedTabEndX - selectedTabStartX * BIZIER_CURVE_FIRST_POINT_X_COEF
        bizierCurveSecondPoint.y = tabBarCropCircleRadius + tabBarCropCircleRadius * BIZIER_CURVE_FIRST_POINT_Y_COEF

        tabBarPath.reset()
        tabBarPath.moveTo(tabBarStartPoint.x, tabBarStartPoint.y)
        tabBarPath.lineTo(selectedTabStartX, selectedTabCircleRadius)
        tabBarPath.cubicTo(
                bizierCurveFirstPoint.x, bizierCurveFirstPoint.y,
                bizierCurveSecondPoint.x, bizierCurveSecondPoint.y,
                selectedTabEndX, selectedTabCircleRadius
        )
        tabBarPath.lineTo((width - paddingRight).toFloat(), selectedTabCircleRadius)
        tabBarPath.lineTo((width - paddingRight).toFloat(), height.toFloat())
        tabBarPath.lineTo(paddingLeft.toFloat(), height.toFloat())
        tabBarPath.close()

        canvas.drawPath(tabBarPath, tabBarPaint)

        canvas.drawCircle(selectedTabStartX, selectedTabCircleRadius, 5f, selectedTabCirclePaint)
        canvas.drawCircle(bizierCurveFirstPoint.x, bizierCurveFirstPoint.y, 5f, selectedTabCirclePaint)
        canvas.drawCircle(bizierCurveSecondPoint.x, bizierCurveSecondPoint.y, 5f, selectedTabCirclePaint)
        canvas.drawCircle(selectedTabEndX, tabBarCropCircleRadius, 5f, selectedTabCirclePaint)

        canvas.drawCircle(curveStartX, selectedTabCircleRadius, 5f, selectedTabCirclePaint)
        canvas.drawCircle(curveEndX, selectedTabCircleRadius, 5f, selectedTabCirclePaint)
    }

    private fun drawSelectedTabCircle(canvas: Canvas) {

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        tabWidth = (w - paddingLeft - paddingRight) / tabsCount

        tabBarStartPoint.x = paddingLeft.toFloat()
        tabBarStartPoint.y = paddingTop + selectedTabCircleRadius
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (selectedTabCircleRadius + tabBarHeight).toInt() + paddingTop + paddingBottom

        setMeasuredDimension(
                measureSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec),
                measureSize(desiredHeight, heightMeasureSpec)
        )
    }

    private fun measureSize(desiredSize: Int, measuredSpec: Int): Int {
        val measureSize = MeasureSpec.getSize(measuredSpec)
        val measureMode = MeasureSpec.getMode(measuredSpec)

        return when (measureMode) {
            MeasureSpec.AT_MOST -> measureSize
            MeasureSpec.EXACTLY -> Math.min(desiredSize, measureSize)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> measureSize
        }
    }

    private fun obtainAttributes(attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BottomNavV2)

        try {
            tabsCount = typedArray.getInteger(R.styleable.BottomNavV2_tabs_count, 0)
            selectedTab = typedArray.getInteger(R.styleable.BottomNavV2_selected_tab, -1)
            selectedTabCircleRadius = typedArray.getDimension(R.styleable.BottomNavV2_selected_tab_circle_radius, 0F)
            selectedTabCircleDiametr = selectedTabCircleRadius * 2
            selectedTabCircleOffset = typedArray.getDimension(R.styleable.BottomNavV2_selected_tab_circle_offset, 0F)
            tabBarHeight = typedArray.getDimension(R.styleable.BottomNavV2_tab_bar_height, -1F)
            tabBarColor = typedArray.getColor(R.styleable.BottomNavV2_tab_bar_color, Color.WHITE)
            selectedTabCircleColor = typedArray.getColor(R.styleable.BottomNavV2_selected_tab_circle_color, Color.WHITE)
        } finally {
            typedArray.recycle()
        }
    }

    private fun checkObtainedAttributes() {
        if (tabsCount <= 0) throw IllegalArgumentException("Tabs count must be > 0")
        if (selectedTab <= 0 || selectedTab >= tabsCount) throw IllegalArgumentException("Incorect tabs cunt value. Tabs count should be > 0 and < ${tabsCount - 1}")
        if (selectedTabCircleRadius <= 0) throw IllegalArgumentException("Circle radius must be > 0")
        if (selectedTabCircleOffset <= 0) throw IllegalArgumentException("Circle offset must be > 0")
        if (tabBarHeight <= 0) throw IllegalArgumentException("Tab bar height must be > 0")
    }

    private fun initPaints() {
        tabBarPaint = Paint(ANTI_ALIAS_FLAG)
        tabBarPaint.style = Paint.Style.FILL //todo make fill and stroke style
        tabBarPaint.color = tabBarColor

        selectedTabCirclePaint = Paint(ANTI_ALIAS_FLAG)
        selectedTabCirclePaint.style = Paint.Style.FILL //todo make fill and stroke style
        selectedTabCirclePaint.color = selectedTabCircleColor
    }
}