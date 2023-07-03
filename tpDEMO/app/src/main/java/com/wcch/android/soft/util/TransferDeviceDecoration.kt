package com.igrs.betotablet.soft.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView

class TransferDeviceDecoration(
    private val dividerSize: Int,
    private val leftMargin: Float,
    private val rightMargin: Float,
    @ColorInt dividerColor: Int
) : ItemDecoration() {
    private val dividerPaint: Paint = Paint()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position < (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.bottom = dividerSize
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val size = parent.childCount
        for (i in 0 until size - 1) {
            val v = parent.getChildAt(i)
            v?.let {
                c.drawRect(
                    v.left + leftMargin,
                    v.bottom.toFloat(),
                    v.right - rightMargin,
                    (v.bottom + dividerSize).toFloat(),
                    dividerPaint
                )
            }
        }
    }

    init {
        dividerPaint.color = dividerColor
    }
}