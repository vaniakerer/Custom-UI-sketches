package com.example.ivan.customviews.bottom_nav

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.example.ivan.customviews.R

public class BottomNavV2 @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val BIZIER_CURVE_FIRST_POINT_X_SHIFT = 2 * 0.05F
        val BIZIER_CURVE_FIRST_POINT_Y_SHIFT = 4 / 3.1F
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
    private var selectedTabStartPoint = PointF()
    private var selectedTabEndPoint = PointF()
    private var selectedTabCurveStartPoint = PointF()
    private var selectedTabCurveEndPoint = PointF()
    //curve intermediate points
    private var selectedTabCurveFirstIntermediatePoint = PointF()
    private var selectedTabCurveSecondIntermediatePoint = PointF()

    private var selectedTabCircleCenterPoint = PointF()

    private lateinit var tabBarPaint: Paint
    private lateinit var selectedTabCirclePaint: Paint

    private val tabBarPath = Path()

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawTabBar(it)
            drawSelectedTabCircle(it)
            Handler().postDelayed({ selectTab(if (selectedTab == tabsCount - 1) 0 else ++selectedTab) }, 3000)
        }
    }

    private fun drawTabBar(canvas: Canvas) {

        tabBarPath.reset()
        tabBarPath.moveTo(tabBarStartPoint.x, tabBarStartPoint.y)
        tabBarPath.lineTo(selectedTabCurveStartPoint.x, selectedTabCurveStartPoint.y)
         tabBarPath.cubicTo(
                 selectedTabCurveFirstIntermediatePoint.x, selectedTabCurveFirstIntermediatePoint.y,
                 selectedTabCurveSecondIntermediatePoint.x, selectedTabCurveSecondIntermediatePoint.y,
                 selectedTabCurveEndPoint.x, selectedTabCurveEndPoint.y
         )
        tabBarPath.lineTo((width - paddingRight).toFloat(), selectedTabCircleRadius)
        tabBarPath.lineTo((width - paddingRight).toFloat(), height.toFloat())
        tabBarPath.lineTo(paddingLeft.toFloat(), height.toFloat())
        tabBarPath.close()

        canvas.drawPath(tabBarPath, tabBarPaint)
    }

    private fun drawSelectedTabCircle(canvas: Canvas) {
        canvas.drawCircle(selectedTabCircleCenterPoint.x, selectedTabCircleCenterPoint.y, selectedTabCircleRadius, selectedTabCirclePaint)
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

    public fun selectTab(tabNumber: Int) {
        setSelectedTab(tabNumber)
        invalidateTabPoints()
        invalidate()
    }

    private fun setSelectedTab(tabNumber: Int) {
        if (tabNumber > tabsCount - 1 || tabNumber < 0)
            throw IllegalArgumentException("Invalid tab number. Max = ${tabsCount - 1}, Min = 0")
        selectedTab = tabNumber
    }

    private fun invalidateTabPoints() {

        selectedTabStartPoint.x = (tabWidth * selectedTab + paddingRight).toFloat()
        selectedTabStartPoint.y = selectedTabCircleRadius

        selectedTabEndPoint.x = selectedTabStartPoint.x + tabWidth
        selectedTabEndPoint.y = selectedTabCircleRadius

        selectedTabCurveStartPoint.x = selectedTabStartPoint.x + (tabWidth - getCurveCircleRadius() * 2) / 2
        selectedTabCurveStartPoint.y = selectedTabCircleRadius

        selectedTabCurveEndPoint.x = selectedTabCurveStartPoint.x + getCurveCircleRadius() * 2
        selectedTabCurveEndPoint.y = selectedTabCircleRadius

        selectedTabCircleCenterPoint.x = selectedTabEndPoint.x - tabWidth / 2 //todo mb calculate not by tab point coordinates
        selectedTabCircleCenterPoint.y = selectedTabCircleRadius

        selectedTabCurveFirstIntermediatePoint.x =
                BezierUtil.getBezierCurveFirstIntermediatePointShiftedX(selectedTabCurveStartPoint.x,getCurveCircleDiametr())
        selectedTabCurveFirstIntermediatePoint.y =
                BezierUtil.getBezierCurveFirstIntermediatePointShiftedY(selectedTabCurveStartPoint.y, getCurveCircleRadius())

        selectedTabCurveSecondIntermediatePoint.x =
                BezierUtil.getBezierCurveSecondIntermediatePointShiftedX(selectedTabCurveEndPoint.x, getCurveCircleDiametr())
        selectedTabCurveSecondIntermediatePoint.y =
                BezierUtil.getBezierCurveSecondIntermediatePointShiftedY(selectedTabCurveEndPoint.y, getCurveCircleRadius())
    }


    //return draw tabBar size with excluded vertical offsets
    private fun getTabBarWidth() = width - paddingRight - paddingLeft

    //return full cropped tab bar circle radius
    private fun getCurveCircleRadius() = selectedTabCircleRadius + selectedTabCircleOffset

    private fun getCurveCircleDiametr() = getCurveCircleRadius() * 2

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
            setSelectedTab(typedArray.getInteger(R.styleable.BottomNavV2_selected_tab, -1))
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
        if (selectedTab <= 0 || selectedTab >= tabsCount) throw IllegalArgumentException("Incorrect tabs cunt value. Tabs count should be > 0 and < ${tabsCount - 1}")
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