package com.example.ivan.customviews.anim_check_box

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AnimCheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isCkecked = false

    private var checkedColor = Color.CYAN
    private var unCheckedColor = Color.GRAY

    private var size: Float = 150F
    private var radus: Float = 75F

    private val checkedPaint = Paint()
    private val unCheckedPaint = Paint()

    private var checkOffset = 0F //0..1

    private var checkedChangeValueAnimator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(radus, radus, radus * checkOffset, if (isCkecked) checkedPaint else unCheckedPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initPaints()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = radus.toInt()
        val desiredWidth = radus.toInt()

        setMeasuredDimension(
                measureSize(desiredWidth, widthMeasureSpec),
                measureSize(desiredHeight, heightMeasureSpec)
        )
    }

    fun setChecked(checked: Boolean) {
        checkedChangeValueAnimator?.cancel()

        if (checked) {
            checkedChangeValueAnimator = ValueAnimator.ofFloat(checkOffset, 1F)
        } else {
            checkedChangeValueAnimator = ValueAnimator.ofFloat(1F, checkOffset)
        }

        checkedChangeValueAnimator?.addUpdateListener {
            checkOffset = it.animatedValue as Float
            invalidate()
        }

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

        unCheckedPaint.color = unCheckedColor
        unCheckedPaint.isAntiAlias = true
    }
}
