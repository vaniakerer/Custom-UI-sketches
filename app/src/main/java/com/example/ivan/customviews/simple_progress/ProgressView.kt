package com.example.ivan.customviews.simple_progress

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.example.ivan.customviews.R

class ProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val DEFAULT_MAX_PROGRESS_VALUE = 100f
    }

    private var maxProgress: Float = DEFAULT_MAX_PROGRESS_VALUE
    private var progressValue = 0f
    private var progressAnimatedValue = 0f
    private var animateProgressChanges = true

    private var progressColor = 0
    private var trackColor = 0

    private var progressPaint = Paint()
    private var trackPaint = Paint()

    private var progressRect = RectF()
    private var trackRect = RectF()


    private val progressValueAnimator = ValueAnimator()

    private var progressUpdateListener: ProgressUpdateListener? = null

    init {
        if (attrs != null)
            obtainAtributes(attrs)
        initPaints()
        initProgressValueAnimator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val exceptWidth = measureDimention(desiredWidth, widthMeasureSpec)
        val exceptHeight = measureDimention(desiredHeight, heightMeasureSpec)


        setMeasuredDimension(exceptWidth, exceptHeight)

        initRects(exceptWidth.toFloat(), exceptHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(trackRect, trackPaint)
        canvas?.drawRect(progressRect, progressPaint)
    }

    fun setProgress(progress: Float) {

        var newProgress = progress
        if (newProgress > maxProgress) newProgress = maxProgress
        val oldProgressValue = progressValue

        this.progressValue = newProgress
        if (animateProgressChanges) {
            animateProgress(oldProgressValue)
        } else {
            this.progressRect.right = getProgressRight(newProgress)
            invalidate()
            progressUpdateListener?.onUpdate(newProgress)
        }
    }

    fun setProgressUpdateListener(progressUpdateListener: ProgressUpdateListener) {
        this.progressUpdateListener = progressUpdateListener
    }

    private fun animateProgress(progressFrom: Float) {
        progressValueAnimator.cancel()
        progressValueAnimator.setFloatValues(
                progressAnimatedValue,
                getProgressRight(progressValue)
        )
        progressValueAnimator.start()
    }

    private fun getProgressRight(progress: Float): Float {
        if (progress > maxProgress) throw IllegalArgumentException("New progress value ($progress)> maxProgress ($maxProgress)")
        return trackRect.right / maxProgress * progress
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

    private fun initPaints() {
        progressPaint.isAntiAlias = true
        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.FILL

        trackPaint.isAntiAlias = true
        trackPaint.color = trackColor
        trackPaint.style = Paint.Style.FILL
    }

    private fun initRects(width: Float, height: Float) {
        trackRect.left = 0f
        trackRect.top = 0f
        trackRect.right = width
        trackRect.bottom = height

        progressRect.left = 0f
        progressRect.top = 0f
        progressRect.right = getProgressRight(progressValue)
        progressRect.bottom = height
    }

    private fun obtainAtributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0)

        progressColor = typedArray.getColor(R.styleable.ProgressView_pv_progress_color, getColor(context, R.color.colorAccent))
        trackColor = typedArray.getColor(R.styleable.ProgressView_pv_track_color, getColor(context, R.color.colorPrimary))
        animateProgressChanges = typedArray.getBoolean(R.styleable.ProgressView_pv_animate_progress_changes, true)
        maxProgress = typedArray.getFloat(R.styleable.ProgressView_pv_max_progress, DEFAULT_MAX_PROGRESS_VALUE)

        typedArray.recycle()
    }

    private fun initProgressValueAnimator() {
        progressValueAnimator.addUpdateListener {
            progressAnimatedValue = (it.animatedValue as Float)
            progressRect.right = progressAnimatedValue
            invalidate()
            progressUpdateListener?.onUpdate(progressAnimatedValue)
        }
        progressValueAnimator.duration = 300
    }

    private fun getColor(context: Context, @ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

}

interface ProgressUpdateListener {
    fun onUpdate(progress: Float)
}