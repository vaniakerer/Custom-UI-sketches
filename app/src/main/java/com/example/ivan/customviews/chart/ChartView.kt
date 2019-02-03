package com.example.ivan.customviews.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import com.example.ivan.customviews.R
import com.example.ivan.customviews.toColorInt

class ChartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var markers: List<Marker>? = null
        set(value) {
            field = value
            calculateMarkersPositions()
            animateChart()
        }

    private var pxPerUnit: Int? = null
    private var zeroY: Int? = 0

    private val pointsPaint = Paint()

    private val pointsPath = Path()

    private val gradientPaint = Paint()

    private val linePaint = Paint()

    private val separatorPaint = Paint()

    private val weeksPaint = Paint()

    private var weekBounds = Rect()

    lateinit var gradient: LinearGradient

    private val pointPadding = 20//todo move to params

    lateinit var textLog: TextView

    private lateinit var weeks: ArrayList<String>

    private val chartProgressValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var drawProgressOffset = 0f//0..1

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        drawGradient(canvas)
        drawSeparators(canvas)
        drawLine(canvas)
        drawPoints(canvas)
        try {
            drawWeeks(canvas)
        } catch (ex: Exception) {
            textLog.setText(ex.message)
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun drawLine(canvas: Canvas) {
        if (markers == null) return
        var previousMarker: Marker? = null
        for (currentMarker in markers!!) {
            if (previousMarker != null) {
                var currentX = currentMarker.point.x
                val shouldStopDraw = currentX > measuredWidth * drawProgressOffset
                if(shouldStopDraw)
                    currentX = measuredWidth * drawProgressOffset

                canvas.drawLine(
                        previousMarker.point.x, previousMarker.point.y,
                        currentX, currentMarker.point.y,
                        linePaint
                )

                textLog.text = currentX.toString()

                if (shouldStopDraw) return
            }
            previousMarker = currentMarker
        }
    }

    private fun drawPoints(canvas: Canvas) {
        markers?.forEach {
            if (it.point.x / measuredWidth < drawProgressOffset)
                canvas.drawCircle(it.point.x, it.point.y, 10f, pointsPaint)
        }
    }

    private fun drawGradient(canvas: Canvas) {
        markers ?: return

        pointsPath.reset()
        pointsPath.moveTo(paddingLeft.toFloat(), zeroY!!.toFloat())

        for (marker in markers!!) {
            pointsPath.lineTo(marker.point.x, marker.point.y)
        }

        // close the pointsPath
        pointsPath.lineTo(markers!!.last().point.x, zeroY!!.toFloat())
        pointsPath.lineTo(paddingLeft.toFloat(), zeroY!!.toFloat())

        canvas.drawPath(pointsPath, gradientPaint)
    }

    private fun drawSeparators(canvas: Canvas) {
        try {
            val separatorsAmount = markers?.size!! / 7
            for (i in 1..separatorsAmount) {
                val x = (measuredWidth / separatorsAmount * i).toFloat()
                canvas.drawLine(x, 0f, x, zeroY!!.toFloat(), separatorPaint)
            }
        } catch (ex: Exception) {
            Toast
                    .makeText(context, "asfasf ${ex.message}", Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun drawWeeks(canvas: Canvas) {
        val padding = 20f
        for ((i, week) in weeks.withIndex()) {
            weeksPaint.getTextBounds(week, 0, week.length, weekBounds)
            val x = middle(i) ?: return

            val y = zeroY!! + weekBounds.height()
            val halfWidth = weekBounds.width() / 2f
            val halfHeight = weekBounds.height() / 2f
            val left = x - halfWidth - padding
            val top = y - halfHeight - padding
            val right = x + halfWidth + padding
            val bottom = y + halfHeight + padding
            weekBounds.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            weeksPaint.color = Color.CYAN
            weeksPaint.style = Paint.Style.FILL
            val weekBoundsRect = RectF()
            weekBoundsRect.left = weekBounds.left.toFloat()
            weekBoundsRect.top = weekBounds.top.toFloat()
            weekBoundsRect.right = weekBounds.right.toFloat()
            weekBoundsRect.bottom = weekBounds.bottom.toFloat()
            canvas.drawRoundRect(weekBoundsRect, 40f, 40f, weeksPaint)
            weeksPaint.color = Color.LTGRAY
            weeksPaint.style = Paint.Style.STROKE
            canvas.drawRoundRect(weekBoundsRect, 40f, 40f, weeksPaint)
            canvas.drawText(week, x, 0f, weeksPaint)
        }
    }

    private fun middle(weekNumber: Int) = markers?.get(weekNumber * 7 + 3)?.point?.x

    private fun calculateMarkersPositions() {

        markers ?: return

        val max = markers?.maxBy { it.value }
        val min = markers?.minBy { it.value }

        max?.value ?: return
        min?.value ?: return

        pxPerUnit = measuredHeight / (max.value - min.value)
        zeroY = max.value * pxPerUnit!! + paddingTop

        val step = (measuredWidth - 2 * pointPadding) / (markers!!.size - 1)

        for ((i, marker) in markers!!.withIndex()) {
            val x = step * i + paddingLeft
            val y = zeroY!! - marker.value * pxPerUnit!! + 10
            marker.point.x = x.toFloat()
            marker.point.y = y.toFloat()
        }
        invalidate()
    }

    private fun animateChart() {
        chartProgressValueAnimator.duration = 3000
        chartProgressValueAnimator.interpolator = AccelerateInterpolator()
        chartProgressValueAnimator.addUpdateListener {
            drawProgressOffset = it.animatedValue as Float
            invalidate()
        }
        chartProgressValueAnimator.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val exceptWidth = measureDimention(desiredWidth, widthMeasureSpec)
        val exceptHeight = measureDimention(desiredHeight, heightMeasureSpec)


        setMeasuredDimension(exceptWidth, exceptHeight)

        initPaths()
        initWeeks()
    }

    private fun measureDimention(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(desiredSize, specSize)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> throw IllegalArgumentException("Cannot measure ${javaClass.name}")
        }
    }

    private fun initPaths() {
        pointsPaint.color = R.color.colorChartPoint.toColorInt(context)
        pointsPaint.isAntiAlias = true

        linePaint.color = R.color.colorChartLine.toColorInt(context)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 3f
        linePaint.isAntiAlias = true

        gradient = LinearGradient(
                0f,
                paddingTop.toFloat(),
                0f,
                measuredHeight.toFloat(),
                R.color.colorChartGradientStart.toColorInt(context),
                R.color.colorChartGradientEnd.toColorInt(context),
                Shader.TileMode.CLAMP
        )

        gradientPaint.style = Paint.Style.FILL
        gradientPaint.shader = gradient
        gradientPaint.isAntiAlias = true

        separatorPaint.color = Color.GRAY
        separatorPaint.style = Paint.Style.STROKE
        separatorPaint.strokeWidth = 3f//todo hardcode
        separatorPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private fun initWeeks() {
        weeks = arrayListOf()
        weeks.add("week 1")
        weeks.add("week 2")
        weeks.add("week 3")
        weeks.add("week 4")
        weeks.add("week 5")
        weeks.add("week 6")
        weeks.add("week 7")
    }
}