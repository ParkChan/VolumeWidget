package com.chan.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

/**
 * Created by alpaslanbak on 29/09/2017.
 * Modified by Nick Panagopoulos @npanagop on 12/05/2018.
 */
class VerticalSeekBar : LinearLayout {
    /**
     * The min value of progress value.
     */
    private var progressMin = MIN

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private var progressMax = MAX

    /**
     * The increment/decrement value for each movement of progress.
     */
    private var progressStep = 10

    /**
     * The corner radius of the view.
     */
    private var radius2 = 10

    /**
     * Text size in SP.
     */
    private var textSize = 26f

    /**
     * Text bottom padding in pixel.
     */
    private var textPaddingBottom = 20
    private var points = 0
    private var enabled = true

    /**
     * Enable or disable text .
     */
    private var textEnabled = true

    /**
     * Enable or disable image .
     */
    var isImageEnabled = false

    /**
     * mTouchDisabled touches will not move the slider
     * only swipe motion will activate it
     */
    private var touchDisabled = true
    private var progressSweep = 0f
    private var progressPaint: Paint? = null
    private var textPaint: Paint? = null
    private var scrWidth = 0
    private var scrHeight = 0
    var onValuesChangeListener: OnValuesChangeListener? = null
    private var bgColor = 0
    private var mDefaultValue = 0
    private var defaultImage: Bitmap? = null
    private var minImage: Bitmap? = null
    private var maxImage: Bitmap? = null
    private val rect = Rect()
    private var firstRun = true

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        println("INIT")
        val density = resources.displayMetrics.density

        // Defaults, may need to link this into theme settings
        var progressColor = ContextCompat.getColor(context, R.color.color_progress)
        bgColor = ContextCompat.getColor(context, R.color.color_background)
        bgColor = ContextCompat.getColor(context, R.color.color_background)
        var textColor = ContextCompat.getColor(context, R.color.color_text)
        textSize = (textSize * density)
        mDefaultValue = progressMax / 2
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.BoxedVertical, 0, 0
            )
            points = a.getInteger(R.styleable.BoxedVertical_points, points)
            progressMax = a.getInteger(R.styleable.BoxedVertical_max, progressMax)
            progressMin = a.getInteger(R.styleable.BoxedVertical_min, progressMin)
            progressStep = a.getInteger(R.styleable.BoxedVertical_step, progressStep)
            mDefaultValue = a.getInteger(R.styleable.BoxedVertical_defaultValue, mDefaultValue)
            radius2 = a.getInteger(R.styleable.BoxedVertical_libCornerRadius, radius2)
            textPaddingBottom =
                a.getInteger(R.styleable.BoxedVertical_textBottomPadding, textPaddingBottom)
            //Images
            isImageEnabled = a.getBoolean(R.styleable.BoxedVertical_imageEnabled, isImageEnabled)
            if (isImageEnabled) {
                defaultImage =
                    (a.getDrawable(R.styleable.BoxedVertical_defaultImage) as BitmapDrawable?)!!.bitmap
                minImage =
                    (a.getDrawable(R.styleable.BoxedVertical_minImage) as BitmapDrawable?)!!.bitmap
                maxImage =
                    (a.getDrawable(R.styleable.BoxedVertical_maxImage) as BitmapDrawable?)!!.bitmap
            }
            progressColor = a.getColor(R.styleable.BoxedVertical_progressColor, progressColor)
            bgColor = a.getColor(R.styleable.BoxedVertical_backgroundColor, bgColor)
            textSize = a.getDimension(R.styleable.BoxedVertical_textSize, textSize)
            textColor = a.getColor(R.styleable.BoxedVertical_textColor, textColor)
            enabled = a.getBoolean(R.styleable.BoxedVertical_enabled, enabled)
            touchDisabled = a.getBoolean(R.styleable.BoxedVertical_touchDisabled, touchDisabled)
            textEnabled = a.getBoolean(R.styleable.BoxedVertical_textEnabled, textEnabled)
            points = mDefaultValue
            a.recycle()
        }

        // range check
        points = if (points > progressMax) progressMax else points
        points = if (points < progressMin) progressMin else points
        progressPaint = Paint()
        progressPaint!!.color = progressColor
        progressPaint!!.isAntiAlias = true
        progressPaint!!.style = Paint.Style.STROKE
        textPaint = Paint()
        textPaint!!.color = textColor
        textPaint!!.isAntiAlias = true
        textPaint!!.style = Paint.Style.FILL
        textPaint!!.textSize = textSize
        scrHeight = context.resources.displayMetrics.heightPixels
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        scrWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        scrHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        progressPaint!!.strokeWidth = scrWidth.toFloat()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        paint.alpha = 255
        canvas.translate(0f, 0f)
        val mPath = Path()
        mPath.addRoundRect(
            RectF(0f, 0f, scrWidth.toFloat(), scrHeight.toFloat()),
            radius2.toFloat(),
            radius2.toFloat(),
            Path.Direction.CCW
        )
        canvas.clipPath(mPath, Region.Op.INTERSECT)
        paint.color = bgColor
        paint.isAntiAlias = true
        canvas.drawRect(0f, 0f, scrWidth.toFloat(), scrHeight.toFloat(), paint)
        canvas.drawLine(
            (canvas.width / 2).toFloat(),
            canvas.height.toFloat(),
            (canvas.width / 2).toFloat(),
            progressSweep,
            progressPaint!!
        )
        if (isImageEnabled && defaultImage != null && minImage != null && maxImage != null) {
            //If image is enabled, text will not be shown
            if (points == progressMax) {
                drawIcon(maxImage!!, canvas)
            } else if (points == progressMin) {
                drawIcon(minImage!!, canvas)
            } else {
                drawIcon(defaultImage!!, canvas)
            }
        } else {
            //If image is disabled and text is enabled show text
            if (textEnabled) {
                val strPoint = points.toString()
                drawText(canvas, textPaint, strPoint)
            }
        }
        if (firstRun) {
            firstRun = false
            value = points
        }
    }

    private fun drawText(canvas: Canvas, paint: Paint?, text: String) {
        canvas.getClipBounds(rect)
        val cWidth = rect.width()
        paint!!.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, rect)
        val x = cWidth / 2f - rect.width() / 2f - rect.left
        canvas.drawText(text, x, (canvas.height - textPaddingBottom).toFloat(), paint)
    }

    private fun drawIcon(bitmap: Bitmap, canvas: Canvas) {
        var bitmap = bitmap
        bitmap = getResizedBitmap(bitmap, canvas.width / 2, canvas.width / 2)
        canvas.drawBitmap(
            bitmap, null, RectF(
                (canvas.width / 2 - bitmap.width / 2).toFloat(),
                (canvas.height - bitmap.height).toFloat(),
                (canvas.width / 3 + bitmap.width).toFloat(),
                canvas.height
                    .toFloat()
            ), null
        )
    }

    private fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        //Thanks Piyush
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (enabled) {
            this.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (onValuesChangeListener != null) onValuesChangeListener!!.onStartTrackingTouch(
                        this
                    )
                    if (!touchDisabled) updateOnTouch(event)
                }
                MotionEvent.ACTION_MOVE -> updateOnTouch(event)
                MotionEvent.ACTION_UP -> {
                    if (onValuesChangeListener != null) onValuesChangeListener!!.onStopTrackingTouch(
                        this
                    )
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (onValuesChangeListener != null) onValuesChangeListener!!.onStopTrackingTouch(
                        this
                    )
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        }
        return false
    }

    /**
     * Update the UI components on touch events.
     *
     * @param event MotionEvent
     */
    private fun updateOnTouch(event: MotionEvent) {
        isPressed = true
        val mTouch = convertTouchEventPoint(event.y)
        val progress = Math.round(mTouch).toInt()
        updateProgress(progress)
    }

    private fun convertTouchEventPoint(yPos: Float): Double {
        val wReturn: Float
        if (yPos > scrHeight * 2) {
            wReturn = (scrHeight * 2).toFloat()
            return wReturn.toDouble()
        } else if (yPos < 0) {
            wReturn = 0f
        } else {
            wReturn = yPos
        }
        return wReturn.toDouble()
    }

    private fun updateProgress(progress: Int) {
        var progress = progress
        progressSweep = progress.toFloat()
        progress = if (progress > scrHeight) scrHeight else progress
        progress = if (progress < 0) 0 else progress

        //convert progress to min-max range
        points = progress * (progressMax - progressMin) / scrHeight + progressMin
        //reverse value because progress is descending
        points = progressMax + progressMin - points
        //if value is not max or min, apply step
        if (points != progressMax && points != progressMin) {
            points = points - points % progressStep + progressMin % progressStep
        }
        if (onValuesChangeListener != null) {
            onValuesChangeListener!!
                .onPointsChanged(this, points)
        }
        invalidate()
    }

    /**
     * Gets a value, converts it to progress for the seekBar and updates it.
     *
     * @param value The value given
     */
    private fun updateProgressByValue(value: Int) {
        points = value
        points = if (points > progressMax) progressMax else points
        points = if (points < progressMin) progressMin else points

        //convert min-max range to progress
        progressSweep =
            ((points - progressMin) * scrHeight / (progressMax - progressMin)).toFloat()
        //reverse value because progress is descending
        progressSweep = scrHeight - progressSweep
        if (onValuesChangeListener != null) {
            onValuesChangeListener!!
                .onPointsChanged(this, points)
        }
        invalidate()
    }

    interface OnValuesChangeListener {
        /**
         * Notification that the point value has changed.
         *
         * @param boxedPoints The SwagPoints view whose value has changed
         * @param points      The current point value.
         */
        fun onPointsChanged(boxedPoints: VerticalSeekBar?, points: Int)
        fun onStartTrackingTouch(boxedPoints: VerticalSeekBar?)
        fun onStopTrackingTouch(boxedPoints: VerticalSeekBar?)
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    var value: Int
        get() = points
        set(points) {
            var points = points
            points = if (points > progressMax) progressMax else points
            points = if (points < progressMin) progressMin else points
            updateProgressByValue(points)
        }

    var max: Int
        get() = progressMax
        set(mMax) {
            require(mMax > progressMin) { "Max should not be less than zero" }
            this.progressMax = mMax
        }

    var cornerRadius: Int
        get() = radius2
        set(mRadius) {
            radius2 = mRadius
            invalidate()
        }

    var defaultValue: Int
        get() = mDefaultValue
        set(mDefaultValue) {
            require(mDefaultValue <= progressMax) { "Default value should not be bigger than max value." }
            this.mDefaultValue = mDefaultValue
        }

    companion object {
        private val TAG = VerticalSeekBar::class.java.simpleName
        private const val MAX = 100
        private const val MIN = 0
    }
}