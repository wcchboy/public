package com.igrs.betotablet.soft.util

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MyToast(val c: Application) {

    private val sysToast: Toast = Toast(c)
    private lateinit var toastView: View

    fun setContentView(id: Int) {
        toastView = LayoutInflater.from(c).inflate(id, null, false)
        sysToast.view = toastView
    }

    fun <V: View>findViewById(id: Int): V {
        return toastView.findViewById(id)
    }

    fun setDuration(duration: Int) {
        sysToast.duration = duration
    }

    @JvmOverloads
    fun setGravity(gravity: Int, xOffset: Int = 0, yOffset: Int = 0) {
        sysToast.setGravity(gravity, xOffset, yOffset)
    }

    fun setText(text: CharSequence) {
        toastView.findViewById<TextView>(android.R.id.message).text = text
    }

    fun setImageDrawable(resId: Int) {
        toastView.findViewById<ImageView>(android.R.id.icon).setImageResource(resId)
    }

    fun cancel() {
        sysToast.cancel()
    }

    fun show() {
        sysToast.show()
    }

}