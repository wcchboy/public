package com.wcch.android.soft.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MaskView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint: Paint = Paint().apply {
        color = Color.parseColor("#99ffffff")
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val paths = mutableListOf<Path>()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (path in paths) {
            canvas?.drawPath(path, paint)
        }
    }

    fun downPoint(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        paths.add(path)
        invalidate()
    }

    fun addPoint(x: Float, y: Float) {
        if (paths.isNotEmpty()) {
            val path = paths.last()
            path.lineTo(x, y)
            invalidate()
        }
    }

    fun clearPoint() {
        if (paths.isNotEmpty()) {
            val path = paths.last()
            postDelayed({
                paths.remove(path)
                invalidate()
            }, 200)
        }
    }
}