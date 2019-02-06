package com.example.ivan.customviews.anim_check_box

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.DashPathEffect


class AnimCheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isChecked = false

    private var checkedColor = Color.CYAN
    private var unCheckedColor = Color.BLUE
    private var checkIconColor = Color.WHITE

    private var radius: Float = 0F
    private var strokeWidth = 8F //todo hardcoded for xxhdpi

    private val checkedPaint = Paint()
    private val borderPaint = Paint()
    private val checkIconPaint = Paint()

    private val fullArea = Path()

    private val checkIconPath = Path()
    private val checIconPathMeasure = PathMeasure()

    private val centerPoint = PointF()

    private var checkOffset = 0F //0..1

    private var checkIconDrawOffset = 0F

    private var checkedChangeValueAnimator: ValueAnimator? = null
    private var checkIconDrawOffsetValueAnimator: ValueAnimator? = null

    init {
        setOnClickListener { setChecked(!isChecked) }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        drawCircleBackground(canvas)
        drawBorder(canvas)
        drawCheckIconPath(canvas)
    }

    private fun drawCircleBackground(canvas: Canvas) {
        canvas.drawCircle(
                centerPoint.x,
                centerPoint.y,
                radius * checkOffset,
                checkedPaint
        )
    }

    private fun drawBorder(canvas: Canvas) {
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, borderPaint)
    }

    private fun drawCheckIconPath(canvas: Canvas) {
        val pathLength = checIconPathMeasure.length
        checkIconPaint.pathEffect = DashPathEffect(floatArrayOf(pathLength, pathLength),
                Math.max(checkIconDrawOffset * pathLength, checkIconDrawOffset))
        canvas.drawPath(checkIconPath, checkIconPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initPaints()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = radius.toInt()
        val desiredWidth = radius.toInt()

        setMeasuredDimension(
                measureSize(desiredWidth, widthMeasureSpec),
                measureSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = w / 2 - strokeWidth / 2
        centerPoint.x = (w / 2).toFloat()
        centerPoint.y = (h / 2).toFloat()

        initCheckIconPath()
    }

    fun setChecked(checked: Boolean) {
        this.isChecked = checked
        startBackgroundAnimation()
    }

    private fun startBackgroundAnimation() {
        checkedChangeValueAnimator?.cancel()
        checkIconDrawOffsetValueAnimator?.cancel()

        val endValue = if (isChecked) 1F else 0F
        checkedChangeValueAnimator = ValueAnimator.ofFloat(checkOffset, endValue)

        checkedChangeValueAnimator?.addUpdateListener {
            checkOffset = it.animatedValue as Float
            invalidate()
        }

        checkedChangeValueAnimator?.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animation: Animator?) {
                startIconDrawAnimation()
            }
        })

        checkedChangeValueAnimator?.duration = 100
        checkedChangeValueAnimator?.start()

    }

    private fun startIconDrawAnimation() {
        checkIconDrawOffsetValueAnimator?.cancel()
        val endValue = if (isChecked) 0F else 1F

        checkIconDrawOffsetValueAnimator = ValueAnimator.ofFloat(checkIconDrawOffset, endValue)

        checkIconDrawOffsetValueAnimator?.addUpdateListener {
            checkIconDrawOffset = it.animatedValue as Float
            invalidate()
        }
        checkedChangeValueAnimator?.duration = 150

        checkIconDrawOffsetValueAnimator?.start()
    }

    private fun measureSize(desiredSize: Int, measureSpec: Int): Int {
        val specSize = MeasureSpec.getSize(measureSpec)
        val specMode = MeasureSpec.getMode(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> Math.min(specSize, desiredSize)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> 0
        }
    }

    private fun initPaints() {
        checkedPaint.color = checkedColor
        checkedPaint.isAntiAlias = true

        borderPaint.color = unCheckedColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = strokeWidth
        borderPaint.isAntiAlias = true

        checkIconPaint.strokeWidth = 10F
        checkIconPaint.style = Paint.Style.STROKE
        checkIconPaint.color = checkIconColor
        checkIconPaint.isAntiAlias = true

        fullArea.addCircle(radius, radius, radius, Path.Direction.CW)
    }

    private fun initCheckIconPath() {

        //todo hardcoded values
        checkIconPath.moveTo(centerPoint.x - 40, centerPoint.y)
        checkIconPath.lineTo(centerPoint.x - 10, centerPoint.y + 30)
        checkIconPath.lineTo((measuredWidth - 40).toFloat(), centerPoint.y - 40)

        checIconPathMeasure.setPath(checkIconPath, false)
    }

    override fun isHardwareAccelerated(): Boolean {
        return true
    }
}
