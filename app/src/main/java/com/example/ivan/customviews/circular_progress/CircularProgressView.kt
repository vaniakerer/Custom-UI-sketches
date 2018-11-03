package com.example.ivan.customviews.circular_progress

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Handler
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.example.ivan.customviews.R

class CircularProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_ITEM_SIZE = 100
        private val DEFAULT_ITEM_OFFSET = 10
        private val DEFAULT_ITEM_SIDE_SIZE = 50
    }

    private var itemColor = 0
    private var itemOffset = 0f
    private var itemSideSize = 0f

    private var progressItemPaint = Paint()

    private var centerPoint = PointF()

    private var firstItemPoint = PointF()
    private var secondItemPoint = PointF()
    private var thirdtemPoint = PointF()
    private var fourthItemPoint = PointF()

    private var firstItemAnimatedInPoint = PointF()
    private var secondItemAnimatedInPoint = PointF()
    private var thirdItemAnimatedInPoint = PointF()
    private var fourthItemAnimatedInPoint = PointF()

    private var firstItemAnimatedOutPoint = PointF()
    private var secondItemAnimatedOutPoint = PointF()
    private var thirdItemAnimatedOutPoint = PointF()
    private var fourthItemAnimatedOutPoint = PointF()


    private var rotateValue = 0f
    private var inAnimatedFraction = 0f
    private var cornerRound = 0f

    private val translateValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private val rotationValueAnimator = ValueAnimator.ofFloat(0f, 360f)
    private val cornerRoundValueAnimator = ValueAnimator.ofFloat(0f, 90f)

    private val animatorSet = AnimatorSet()

    init {
        attrs?.let { obtainAttributes(it) }
        initPaints()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val itemHypotenuseSize = calculateHypotenuseIsoscelesTriangle(itemSideSize)
        val itemOffsetTriangleHypotenuse = calculateHypotenuseIsoscelesTriangle(itemOffset)

        val desiredWidth = itemHypotenuseSize * 2 + itemOffsetTriangleHypotenuse + paddingRight + paddingLeft
        val desiredHeight = itemHypotenuseSize * 2 + itemOffsetTriangleHypotenuse + paddingTop + paddingBottom

        val minSizeValue = Math.min(measuredWidth, measuredHeight)

        val itemOffsetHalf = itemOffset / 2

        centerPoint.x = minSizeValue / 2f
        centerPoint.y = centerPoint.x

        //animated out
        firstItemAnimatedOutPoint.x = 0f
        firstItemAnimatedOutPoint.y = 0f

        secondItemAnimatedOutPoint.x = desiredWidth.toFloat() - itemSideSize
        secondItemAnimatedOutPoint.y = 0f

        thirdItemAnimatedOutPoint.x = desiredWidth.toFloat() - itemSideSize
        thirdItemAnimatedOutPoint.y = desiredHeight.toFloat() - itemSideSize

        fourthItemAnimatedOutPoint.x = 0f
        fourthItemAnimatedOutPoint.y = desiredHeight.toFloat() - itemSideSize


        //animated in
        firstItemAnimatedInPoint.x = centerPoint.x - itemSideSize - itemOffsetHalf
        firstItemAnimatedInPoint.y = centerPoint.y - itemSideSize - itemOffsetHalf

        secondItemAnimatedInPoint.x = centerPoint.x + itemOffsetHalf
        secondItemAnimatedInPoint.y = centerPoint.y - itemSideSize - itemOffsetHalf

        thirdItemAnimatedInPoint.x = centerPoint.x + itemOffsetHalf
        thirdItemAnimatedInPoint.y = centerPoint.y + itemOffsetHalf

        fourthItemAnimatedInPoint.x = centerPoint.x - itemSideSize - itemOffsetHalf
        fourthItemAnimatedInPoint.y = centerPoint.y + itemOffsetHalf

        //current
        firstItemPoint.x = firstItemAnimatedOutPoint.x
        firstItemPoint.y = firstItemAnimatedOutPoint.y

        secondItemPoint.x = secondItemAnimatedOutPoint.x
        secondItemPoint.y = secondItemAnimatedOutPoint.y

        thirdtemPoint.x = thirdItemAnimatedOutPoint.x
        thirdtemPoint.y = thirdItemAnimatedOutPoint.y

        fourthItemPoint.x = fourthItemAnimatedOutPoint.x
        fourthItemPoint.y = fourthItemAnimatedOutPoint.y

        setMeasuredDimension(desiredWidth, desiredHeight)

    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        canvas.rotate(rotateValue, (canvas.getWidth() / 2).toFloat(), (canvas.getHeight() / 2).toFloat());

        canvas.save()
        canvas.translate(firstItemPoint.x, firstItemPoint.y)
        canvas.drawRect(0f, 0f, itemSideSize, itemSideSize, progressItemPaint)
        canvas.restore()

        canvas.save()
        canvas.translate(secondItemPoint.x, secondItemPoint.y)
        canvas.drawRect(0f, 0f, itemSideSize, itemSideSize, progressItemPaint)
        canvas.restore()

        canvas.save()
        canvas.translate(thirdtemPoint.x, thirdtemPoint.y)
        canvas.drawRoundRect(0f, 0f, itemSideSize, itemSideSize, 20f, 20f, progressItemPaint)
        canvas.restore()

        canvas.save()
        canvas.translate(fourthItemPoint.x, fourthItemPoint.y)
        canvas.drawRect(0f, 0f, itemSideSize, itemSideSize, progressItemPaint)
        canvas.restore()
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

    public fun startAnimation() {
        initTranslateInAnimation()
        initRotationAnimation()

        translateValueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                rotationValueAnimator.start()
            }
        })

        translateValueAnimator.start()
    }

    private fun initTranslateInAnimation() {
        translateValueAnimator.duration = 300
        translateValueAnimator.addUpdateListener {
            val fraction = it.animatedValue as Float

            firstItemPoint.x = (firstItemAnimatedInPoint.x - firstItemAnimatedOutPoint.x) * fraction
            firstItemPoint.y = (firstItemAnimatedInPoint.y - firstItemAnimatedOutPoint.y) * fraction

            secondItemPoint.x = secondItemAnimatedOutPoint.x - (secondItemAnimatedOutPoint.x - secondItemAnimatedInPoint.x) * fraction
            secondItemPoint.y = secondItemAnimatedInPoint.y * fraction

            thirdtemPoint.x = thirdItemAnimatedOutPoint.x - (thirdItemAnimatedOutPoint.x - thirdItemAnimatedInPoint.x) * fraction
            thirdtemPoint.y = thirdItemAnimatedOutPoint.y - (thirdItemAnimatedOutPoint.y - thirdItemAnimatedInPoint.y) * fraction

            fourthItemPoint.x = (fourthItemAnimatedInPoint.x - fourthItemAnimatedOutPoint.x) * fraction
            fourthItemPoint.y = fourthItemAnimatedOutPoint.y - (fourthItemAnimatedOutPoint.y - fourthItemAnimatedInPoint.y) * fraction


            invalidate()
        }

        translateValueAnimator.interpolator = AccelerateInterpolator()
    }

    private fun initRoundAnimation() {

    }

    private fun initRotationAnimation() {
        rotationValueAnimator.duration = 1000
        rotationValueAnimator.addUpdateListener {
            rotateValue = it.animatedValue as Float
            invalidate()
        }
    }

    private fun obtainAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView, 0, 0)

        try {
            itemColor = ta.getColor(R.styleable.CircularProgressView_cpv_item_color, getColor(R.color.colorAccent))
            itemOffset = ta.getDimensionPixelOffset(R.styleable.CircularProgressView_cpv_item_offset, DEFAULT_ITEM_OFFSET).toFloat()
            itemSideSize = ta.getDimensionPixelOffset(R.styleable.CircularProgressView_item_side_size, DEFAULT_ITEM_SIDE_SIZE).toFloat()
        } finally {
            ta.recycle()
        }
    }

    private fun initPaints() {
        progressItemPaint.isAntiAlias = true
        progressItemPaint.color = itemColor
        progressItemPaint.style = Paint.Style.FILL
    }

    private fun calculateHypotenuseIsoscelesTriangle(katte: Float) = Math.sqrt((katte * katte + katte * katte).toDouble()).toInt()

    private fun calculateCattetIsoscelesTriangle(hypotenuse: Int) = Math.sqrt(((hypotenuse * hypotenuse) / 2).toDouble()).toFloat()

    private fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

}