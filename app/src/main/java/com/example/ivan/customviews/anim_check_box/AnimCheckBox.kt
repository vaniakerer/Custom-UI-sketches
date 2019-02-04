package com.example.ivan.customviews.anim_check_box

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class AnimCheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isChecked = false

    private var checkedColor = Color.CYAN
    private var unCheckedColor = Color.GRAY

    private var size: Float = 150F
    private var radus: Float = 75F
    private var strokeWidth = 60F

    private val checkedPaint = Paint()
    private val unCheckedPaint = Paint()

    private val clippedArea = Path()
    private val fullArea = Path()
    private val clipRegion = Region()

    private var checkOffset = 0F //0..1
    private val startDrawcheckedFalseBorderOffset = 0.3F

    private var checkedChangeValueAnimator: ValueAnimator? = null

    init {
        setOnClickListener { setChecked(!isChecked) }
    }

    override fun onDraw(canvas: Canvas?) {

        if (checkOffset <= startDrawcheckedFalseBorderOffset) {
            val clipedPathRadius = getUncheckedOffset(checkOffset) * strokeWidth
            clippedArea.addCircle(radus, radus, clipedPathRadius, Path.Direction.CW)
            canvas?.drawCircle(radus, radus, radus, unCheckedPaint)
            canvas?.clipPath(clippedArea, Region.Op.REPLACE)
        } else {
            canvas?.drawCircle(
                    radus,
                    radus,
                    radus * checkOffset,
                    checkedPaint
            )
        }
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
            checkedChangeValueAnimator = ValueAnimator.ofFloat(checkOffset, 0F)
        }

        checkedChangeValueAnimator?.addUpdateListener {
            checkOffset = it.animatedValue as Float
            invalidate()
        }

        checkedChangeValueAnimator?.duration = 3000

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

    private fun getUncheckedOffset(currentOffset: Float): Float {
        if (currentOffset > startDrawcheckedFalseBorderOffset)
            throw IllegalStateException("offset should be lower than $startDrawcheckedFalseBorderOffset")

        return 1 - startDrawcheckedFalseBorderOffset * (1F - currentOffset) / 100
    }

    private fun initPaints() {
        checkedPaint.color = checkedColor
        checkedPaint.isAntiAlias = true

        unCheckedPaint.color = unCheckedColor
        unCheckedPaint.isAntiAlias = true

        fullArea.addCircle(radus, radus, radus, Path.Direction.CW)
    }
}
