package ru.netology.customviewapplication.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.customviewapplication.R
import ru.netology.customviewapplication.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {

    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()
    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var percentageData = 0F
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor())
            )
        }
    }

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        } else {
            var startAngle = -90F

            data.forEachIndexed { index, datum ->
                val percentageDatum = datum / data.sum()
                percentageData += percentageDatum
                val angle = percentageDatum * 360F
                paint.color = colors.getOrElse(index) { generateRandomColor() }
                canvas.drawArc(oval, startAngle, angle * progress, false, paint)
                startAngle += angle
            }
        }

        canvas.drawText(
            "%.2f%%".format(percentageData * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 500
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }
}