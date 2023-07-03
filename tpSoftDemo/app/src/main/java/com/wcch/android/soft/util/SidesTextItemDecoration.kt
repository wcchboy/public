package com.igrs.betotablet.soft.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**两边都有文字的分割线
 * author: lmh
 * date: 2021/6/4 18:56
 */
class SidesTextItemDecoration private constructor(private val listener: ItemDecorationListener): RecyclerView.ItemDecoration() {

    //显示左边文字  右边文字  左右都显示
    private var showType = ShowType.ALL
    private var sticky = true //是否悬浮

    //单位PX
    private var groupHeight = 80
    private var dividerHeight = 1
    @ColorInt
    private var groupColor = Color.DKGRAY
    @ColorInt
    private var dividerColor = Color.DKGRAY
    @ColorInt
    private var leftTextColor = Color.WHITE
    @ColorInt
    private var rightTextColor = Color.WHITE
    private var leftTextSize = 35f
    private var rightTextSize = 25f
    private var leftTextPaddingLeft = 0f
    private var rightTextPaddingRight = 0f
    private var textTopOffset = 0f
    private var firstGroupTopPadding = 0//适用于item之间本身有间距，导致第一个group高度不够文字不局中
    private var footerCount = 0 //recyclerview尾部数量

    private val bgPaint = Paint()
    private val textPaint = TextPaint()
    private val rect = Rect()

    init {
        bgPaint.isAntiAlias = true
        bgPaint.isDither = true
        bgPaint.style = Paint.Style.FILL_AND_STROKE

        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.letterSpacing = 0.05f
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        parent.adapter?.let { adapter ->
            if (position >= adapter.itemCount.minus(footerCount))
                return
        }

        if (isGroupFirstItem(position)) {
            if (position == 0)
                outRect.top = groupHeight + firstGroupTopPadding
            else
                outRect.top = groupHeight
        } else {
            outRect.top = dividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val itemCount = parent.childCount
        for (index in 0 until itemCount) {
            val view = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(view)
            parent.adapter?.let { adapter ->
                if (position >= adapter.itemCount.minus(footerCount))
                    return
            }
            var top: Int

            if (isGroupFirstItem(position)) {
                top = view.top - groupHeight
                if (position == 0) {
                    top -= firstGroupTopPadding
                }
                rect.left = view.left
                rect.top = top
                rect.right = view.right
                rect.bottom = view.top
                drawGroup(c, rect, position)
            } else {
                if (dividerHeight <= 0) {
                    continue
                }
                top = view.top - dividerHeight

                rect.left = view.left
                rect.top = top
                rect.right = view.right
                rect.bottom = view.top
                drawDivider(c, rect)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (!sticky) return
        val firstView = parent.getChildAt(0)
        val secondView = parent.getChildAt(1)
        if (firstView != null && secondView != null) {
            val firstPosition = parent.getChildAdapterPosition(firstView)
            val secondPosition = parent.getChildAdapterPosition(secondView)
            var secondViewIsGroupFirst = false

            parent.adapter?.let { adapter ->
                if (adapter.itemCount == footerCount)
                    return
                secondViewIsGroupFirst = if (secondPosition >= adapter.itemCount.minus(footerCount))
                    false
                else
                    isGroupFirstItem(secondPosition)
            }

            val height = groupHeight + (firstGroupTopPadding * 2)
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            rect.left = left
            rect.top = 0
            rect.right = right
            rect.bottom = height

            var position = if (firstView.bottom <= firstGroupTopPadding) secondPosition else firstPosition

            parent.adapter?.let {
                if (position >= it.itemCount.minus(footerCount))
                    position = it.itemCount.minus(footerCount + 1)
            }

            drawGroup(c, rect, position, true)
        }
    }

    //当前item是否是本组第一个
    private fun isGroupFirstItem(position: Int): Boolean {
        if (position <= 0)
            return true

        val groupName = listener.getGroupName(position)
        val lastGroupName = listener.getGroupName(position - 1)
        return groupName != lastGroupName
    }

    /**
     *     画分组对应view
     */
    private fun drawGroup(c: Canvas, r: Rect, position: Int, isOver: Boolean = false) {
        bgPaint.color = groupColor
//        bgPaint.alpha = if (isOver) 242 else 255

        c.drawRect(r, bgPaint)

        val centerY = if (position == 0 && !isOver) (r.top + firstGroupTopPadding + r.bottom) / 2 + textTopOffset else (r.top + r.bottom) / 2 + textTopOffset

        when(showType) {

            ShowType.ALL -> {
                drawLeftText(c, centerY, position)
                drawRightText(c, centerY, r.right, position, isOver)
            }

            ShowType.LEFT -> {
                drawLeftText(c, centerY, position)
            }

            ShowType.RIGHT -> {
                drawRightText(c, centerY, r.right, position, isOver)
            }
        }

    }

    private fun drawDivider(c: Canvas, r: Rect) {
        bgPaint.color = dividerColor

        c.drawRect(r, bgPaint)
    }

    private fun drawLeftText(c: Canvas, centerY: Float, position: Int) {
//        textPaint.isFakeBoldText = true
        textPaint.textSize = leftTextSize
        textPaint.color = leftTextColor
        textPaint.textAlign = Paint.Align.LEFT
        val fm = textPaint.fontMetrics
        val leftBaseline = (fm.descent - fm.ascent) / 2 - fm.descent + centerY
        c.drawText(listener.getLeftText(position), leftTextPaddingLeft, leftBaseline, textPaint)
    }

    private fun drawRightText(c: Canvas, centerY: Float, right: Int, position: Int, isOver: Boolean) {
        textPaint.isFakeBoldText = false
        textPaint.textSize = rightTextSize
        textPaint.color = rightTextColor
        textPaint.textAlign = Paint.Align.RIGHT
        val fm = textPaint.fontMetrics
        val rightBaseline = (fm.descent - fm.ascent) / 2 - fm.descent + centerY

        val currentText = listener.getRightText(position)
        if (isOver) {
            c.drawText(currentText, right - rightTextPaddingRight, rightBaseline, textPaint)
        } else {
            val lastText = listener.getRightText(position - 1)
            if (lastText != currentText) {
                c.drawText(currentText, right - rightTextPaddingRight, rightBaseline, textPaint)
            }
        }
    }

    class Builder(lister: ItemDecorationListener) {

        val itemDecoration = SidesTextItemDecoration(lister)

        //显示左边文字  右边文字  左右都显示
        fun setShowType(type: ShowType): Builder {
            itemDecoration.showType = type
            return this
        }
        fun setSticky(b: Boolean): Builder {
            itemDecoration.sticky = b
            return this
        }
        //单位PX
        fun setGroupHeight(height: Int): Builder {
            itemDecoration.groupHeight = height
            return this
        }

        fun setDividerHeight(height: Int): Builder {
            itemDecoration.dividerHeight = height
            return this
        }

        fun setGroupColor(@ColorInt color: Int): Builder {
            itemDecoration.groupColor = color
            return this
        }

        fun setDividerColor(@ColorInt color: Int): Builder {
            itemDecoration.dividerColor = color
            return this
        }

        fun setLeftTextColor(@ColorInt color: Int): Builder {
            itemDecoration.leftTextColor = color
            return this
        }

        fun setRightTextColor(@ColorInt color: Int): Builder {
            itemDecoration.rightTextColor = color
            return this
        }

        fun setLeftTextSize(size: Float): Builder {
            itemDecoration.leftTextSize = size
            return this
        }

        fun setRightTextSize(size: Float): Builder {
            itemDecoration.rightTextSize = size
            return this
        }

        fun setLeftTextPadding(padding: Float): Builder {
            itemDecoration.leftTextPaddingLeft = padding
            return this
        }

        fun setRightTextPadding(padding: Float): Builder {
            itemDecoration.rightTextPaddingRight = padding
            return this
        }

        fun setTextTopOffset(topOffset: Float): Builder {
            itemDecoration.textTopOffset = topOffset
            return this
        }

        fun setFirstGroupTopPadding(padding: Int): Builder {
            itemDecoration.firstGroupTopPadding = padding
            return this
        }
        fun setFooterCount(count: Int): Builder {
            itemDecoration.footerCount = count
            return this
        }
    }

    enum class ShowType {
        LEFT, RIGHT, ALL
    }

    interface ItemDecorationListener {
        //函数参数，获取当前item对应的分组name
        fun getGroupName(position: Int): String

        fun getLeftText(position: Int): String
        fun getRightText(position: Int): String
    }

    abstract class BaseItemDecorationListener: ItemDecorationListener {
        override fun getLeftText(position: Int): String {
            return ""
        }

        override fun getRightText(position: Int): String {
            return ""
        }
    }


}