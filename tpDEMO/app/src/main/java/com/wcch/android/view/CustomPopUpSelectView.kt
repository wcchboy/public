package com.igrs.betotablet.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.igrs.betotablet.R
import java.util.ArrayList

/**
 *  01BetoTabletApp
 *
 *  @author Created by RyanWang on 2022/12/30
 *  Copyright © 2022年 IGRS. All rights reserved.
 *  Describe:
 */
class CustomPopUpSelectView  @JvmOverloads constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = 0)
    : LinearLayout(context, attrs, defStyle){

    private var mWidth = 240
    private var titleText: TextView? = null
    private var closeIcon: ImageView? = null
    private var confirmBtn: Button? = null
    private var icon: ImageView? = null
    private var popupWindow: PopupWindow? = null
    private val dataList: ArrayList<String>? = ArrayList()
    private var checkIndex = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.pop_up_query_dialog_layout, this, true)
        titleText = findViewById(R.id.pop_query_dialog_title)
        closeIcon = findViewById(R.id.pop_query_dialog_close)

        /*setOnClickListener { v: View? ->
            if (popupWindow == null) {
                showPopWindow()
            } else {
                closePopWindow()
            }
        }*/
    }
    private fun showPopWindow() {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.layout_custom_wrapper, null, false)
        val listView = contentView.findViewById<ListView>(R.id.listView)
        listView.adapter = SpinnerAdapter()
        popupWindow = PopupWindow(contentView, mWidth, LayoutParams.WRAP_CONTENT)
        popupWindow!!.setBackgroundDrawable(resources.getDrawable(R.color.transparent))
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(this, 0, 10)
    }

    private fun closePopWindow() {
        popupWindow!!.dismiss()
        popupWindow = null
    }

    fun setItemsData(list: ArrayList<String>?) {
        if (dataList != null && dataList.size > 0) {
            dataList.clear()
        }
        dataList!!.addAll(list!!)
    }
    fun setTitle(textColor: Int) {
        titleText!!.setTextColor(textColor)
    }
    fun setTitleText(resid: Int) {
        titleText!!.setText(resid)
    }

    fun setCloseBtnSrc(src: Int) {
        closeIcon!!.setImageResource(src)
    }



    internal inner class SpinnerAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return dataList!!.size
        }

        override fun getItem(position: Int): Any? {
            dataList?.let{
                if (it.size>=position+1){
                    return it[position]
                }
            }

            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val holder: Holder
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.new_item_custom_popwin, null)
                holder = Holder()
                holder.tv = convertView.findViewById(R.id.tv)
                holder.iv_checked = convertView.findViewById(R.id.iv_checked)
                holder.layout = convertView.findViewById(R.id.layout_container)
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            holder.tv!!.text = dataList!![position]
            if (position == checkIndex) {
                //                holder.tv.setTextColor(getResources().getColor(R.color.update_button));
                holder.iv_checked!!.visibility = VISIBLE
            } else {
                //                holder.tv.setTextColor(getResources().getColor(R.color.text_unchecked));
                holder.iv_checked!!.visibility = INVISIBLE
            }
            val text = dataList[position]
            holder.layout!!.setOnClickListener { v: View? ->
                if (listener != null) {
                    listener!!.onSelect(position,text)
                    //更新图标显示的索引
                    index = position
                    notifyDataSetChanged()
                }
                //TODO 待确认按钮功能完成 这里不关闭弹窗，等点击确认按钮后在关闭。
                //closePopWindow()
            }
            return convertView
        }
    }

    private inner class Holder {
        var tv: TextView? = null
        var iv_checked: ImageView? = null
        var layout: RelativeLayout? = null
    }

    private var listener: onSelectListener? = null
    interface onSelectListener {
        fun onSelect(index: Int,text:String)
    }

    fun setOnSelectListener(listener: onSelectListener?) {
        this.listener = listener
    }

    var index: Int
        get() = checkIndex
        set(index) {
            if (dataList != null && !dataList.isEmpty() && index <= dataList.size - 1) {
                //控件拆分了。这里只是弹窗，界面显示的值可以拿到索引后去取对应的值。
                //editText!!.text = dataList[index].substring(0, 5)
                checkIndex = index
            } else {
                checkIndex = 0
            }
        }

    init {
        initView()
    }



}