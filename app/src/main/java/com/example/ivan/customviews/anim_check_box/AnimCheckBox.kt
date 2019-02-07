package com.example.ivan.customviews.anim_check_box

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.DashPathEffect
import android.widget.Checkable
import com.example.ivan.customviews.R
import com.example.ivan.customviews.util.UiUtil


class AnimCheckBox @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Checkable {

    private var checked = false

    private var checkedColor = 0
    private var borderColor = 0
    private var checkIconColor = 0

    private var radius: Float = 0F
    private var borderWidth = 0F
    private var checkIconWidth = 0F

    private val checkedPaint = Paint()
    private val borderPaint = Paint()
    private val checkIconPaint = Paint()

    private val fullArea = Path()

    private val checkIconPath = Path()
    private val checkIconPathMeasure = PathMeasure()

    private val centerPoint = PointF()

    private var checkOffset = 0F //0..1

    private var checkIconDrawOffset = 0F

    private var innerDrawIconTranslation = 0

    private var checkIconCornerRadiusInDp = 2F

    private var checkedChangeValueAnimator: ValueAnimator? = null
    private var checkIconDrawOffsetValueAnimator: ValueAnimator? = null

    init {
        attrs?.let { obtainAttributes(it) }
        setOnClickListener { isChecked = !isChecked }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        drawBorder(canvas)
        drawCircleBackground(canvas)
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
        val pathLength = checkIconPathMeasure.length
        canvas.save()
        canvas.translate(-innerDrawIconTranslation.toFloat() / 3, 0F)

        //todo bad solution
        checkIconPaint.pathEffect = ComposePathEffect(
                DashPathEffect(floatArrayOf(pathLength, pathLength),
                        Math.max(checkIconDrawOffset * pathLength, checkIconDrawOffset)),
                CornerPathEffect(UiUtil.dp2px(checkIconCornerRadiusInDp, context))
        )
        canvas.drawPath(checkIconPath, checkIconPaint)
        canvas.restore()
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

        radius = w / 2 - borderWidth / 2

        centerPoint.x = (w / 2).toFloat()
        centerPoint.y = (h / 2).toFloat()

        innerDrawIconTranslation = w / 6

        initCheckIconPath()
    }

    override fun setChecked(checked: Boolean) {
        this.checked = checked
        setChecked(checked, true)
    }

    override fun isChecked(): Boolean {
        return this.checked
    }

    override fun toggle() {
        this.checked = !isChecked
    }

    private fun setChecked(checked: Boolean, animate: Boolean) {
        if (animate) {
            startBackgroundAnimation()
        } else {
            setCheckedWithoutAnimation(checked)
            invalidate()
        }
    }

    private fun setCheckedWithoutAnimation(checked: Boolean) {
        checkIconDrawOffset = if (checked) 1F else 0F
        checkOffset = checkIconDrawOffset
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
        checkedChangeValueAnimator?.duration = 90

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

    private fun obtainAttributes(attributeSet: AttributeSet) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.AnimCheckBox, 0, 0)
        try {
            borderColor = ta.getColor(R.styleable.AnimCheckBox_acb_border_color, Color.GRAY)
            checkedColor = ta.getColor(R.styleable.AnimCheckBox_acb_checked_color, Color.BLUE)
            checkIconColor = ta.getColor(R.styleable.AnimCheckBox_acb_check_icon_color, Color.WHITE)

            borderWidth = ta.getDimensionPixelOffset(R.styleable.AnimCheckBox_acb_border_width, 0).toFloat()
            checkIconWidth = ta.getDimensionPixelOffset(R.styleable.AnimCheckBox_acb_check_icon_width, 0).toFloat()
            isChecked = ta.getBoolean(R.styleable.AnimCheckBox_acb_checked, false)
        } finally {
            ta.recycle()
        }
    }

    private fun initPaints() {
        checkedPaint.color = checkedColor
        checkedPaint.isAntiAlias = true

        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderWidth
        borderPaint.isAntiAlias = true

        checkIconPaint.strokeWidth = checkIconWidth
        checkIconPaint.style = Paint.Style.STROKE
        checkIconPaint.color = checkIconColor
        checkIconPaint.isAntiAlias = true

        fullArea.addCircle(radius, radius, radius, Path.Direction.CW)
    }

    private fun initCheckIconPath() {
        checkIconPath.reset()

        checkIconPath.moveTo(centerPoint.x - innerDrawIconTranslation, centerPoint.y)
        checkIconPath.lineTo(centerPoint.x, centerPoint.y + innerDrawIconTranslation)
        checkIconPath.lineTo(centerPoint.x + innerDrawIconTranslation * 1.5F, centerPoint.y - innerDrawIconTranslation)
        checkIconPathMeasure.setPath(checkIconPath, false)
    }

    override fun isHardwareAccelerated(): Boolean {
        return true
    }
}
