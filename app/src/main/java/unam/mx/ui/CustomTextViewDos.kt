package unam.mx.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView



class CustomTextViewDos(context: Context, attrs: AttributeSet? = null) :  AppCompatTextView(context, attrs) {
    init {
        includeFontPadding = true
        setShadowLayer(4f, -2f, 2f, Color.BLACK)
        setShadowLayer(4f, 2f, 2f, Color.BLACK)
        setShadowLayer(4f, -2f, -2f, Color.BLACK)
        setShadowLayer(4f, 2f, -2f, Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        val textColor = currentTextColor

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        setTextColor(Color.BLACK)
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL
        setTextColor(Color.WHITE)
        super.onDraw(canvas)
        setTextColor(textColor)
    }
}