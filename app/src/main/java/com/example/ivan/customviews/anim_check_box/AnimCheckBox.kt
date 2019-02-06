package com.example.ivan.customviews.anim_check_box

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


class AnimCheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isChecked = false

    private var checkedColor = Color.CYAN
    private var unCheckedColor = Color.GRAY

    private var radius: Float = 0F
    private var strokeWidth = 8F //todo hardcoded for xxhdpi

    private val checkedPaint = Paint()
    private val borderPaint = Paint()

    private val fullArea = Path()

    private val centerPoint = PointF()

    private var checkOffset = 0F //0..1

    private var checkedChangeValueAnimator: ValueAnimator? = null

    init {
        setOnClickListener { setChecked(!isChecked) }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(
                centerPoint.x,
                centerPoint.y,
                radius * checkOffset,
                checkedPaint
        )
        canvas?.drawCircle(centerPoint.x, centerPoint.y, radius, borderPaint)
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
    }

    fun setChecked(checked: Boolean) {
        checkedChangeValueAnimator?.cancel()

        if (checked) {
            checkedChangeValueAnimator = ValueAnimator.ofFloat(checkOffset, 1F)
        } else {
            checkedChangeValueAnimator = ValueAnimator.ofFloat(checkOffset, 0F)
        }

        checkedChangeValueAnimator?.addUpdateListener {
            checkOffset = it.animatedValue as Float
            invalidate()
        }

        checkedChangeValueAnimator?.duration = 300

        this.isChecked = checked

        checkedChangeValueAnimator?.start()
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

        fullArea.addCircle(radius, radius, radius, Path.Direction.CW)
    }

    override fun isHardwareAccelerated(): Boolean {
        return true
    }
}
