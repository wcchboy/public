package com.wcch.android.view

import android.content.Context
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.view.LayoutInflater
import android.view.View
import com.igrs.betotablet.R
import android.view.ViewGroup
import android.widget.*
import com.igrs.betotablet.view.CustomSpinner.SpinnerAdapter
import java.util.ArrayList

class CustomSpinner @JvmOverloads constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = 0)
    : LinearLayout(context, attrs, defStyle) {
    private var mWidth = 240
    private var editText: TextView? = null
    private var imageView: ImageView? = null
    private var icon: ImageView? = null
    private var popupWindow: PopupWindow? = null
    private val dataList: ArrayList<String>? = ArrayList()
    private var checkIndex = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
    }

    fun initView() {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_spinner, this, true)
        editText = findViewById(R.id.text)
        imageView = findViewById(R.id.btn)
        icon = findViewById(R.id.iv_internet_icon)
        if (!iconVisible) icon!!.visibility = GONE
        setOnClickListener { v: View? ->
            if (popupWindow == null) {
                showPopWindow()
            } else {
                closePopWindow()
            }
        }
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



    fun setTextColor(textColor: Int) {
        editText!!.setTextColor(textColor)
    }

    fun setArrowSrc(src: Int) {
        imageView!!.setImageResource(src)
    }

    internal inner class SpinnerAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return dataList!!.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val holder: Holder
            if (convertView == null) {
                 convertView = View.inflate(context, R.layout.item_custom_popwin, null)
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
                editText!!.text = text
                if (listener != null) {
                    listener!!.onSelect(position)
                }
                closePopWindow()
            }
            return convertView
        }
    }

    private inner class Holder {
        var tv: TextView? = null
        var iv_checked: ImageView? = null
        var layout: RelativeLayout? = null
    }

    private var iconVisible = true
    fun setIconVisible(iconVisible: Boolean) {
        this.iconVisible = iconVisible
        if (icon != null) {
            if (iconVisible) {
                icon!!.visibility = VISIBLE
            } else {
                icon!!.visibility = GONE
            }
        }
    }

    //=========================================================
    private var listener: onSelectListener? = null
    interface onSelectListener {
        fun onSelect(index: Int)
    }

    fun setOnSelectListener(listener: onSelectListener?) {
        this.listener = listener
    }

    var index: Int
        get() = checkIndex
        set(index) {
            if (dataList != null && !dataList.isEmpty() && index <= dataList.size - 1) {
                    editText!!.text = dataList[index].substring(0, 5)
                  checkIndex = index
            } else {
                checkIndex = 0
            }
        }

    init {
        initView()
    }
}