package com.igrs.betotablet.view

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet

class MarqueeText : AppCompatTextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        //设置文字内容超过textView宽度时候以跑马灯的形式
        ellipsize = TextUtils.TruncateAt.MARQUEE
        //设置焦点
        isFocusable = true
        //设置触摸模式下是否可以获得焦点
        isFocusableInTouchMode = true
        //设置跑马灯循环次数 -1为always 无限循环  3就是3次 4就是4次  0没试过
        marqueeRepeatLimit = 3
    }

    /**
     * 重写isFocused方法始终获得焦点来播放跑马灯
     *
     * @return
     */
    override fun isFocused(): Boolean {
        return true
    }
}