package com.zcy.fancydialog

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * @author:         zhaochunyu
 * @description:    工具类
 * @date:           2019/1/2
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}
fun Context.color(id: Int): Int = resources.getColor(id)
fun Context.string(id: Int): String = resources.getString(id)

// 黑暗 0.0F ~ 1.0F 透明
fun Context.setBackgroundAlpha(alpha: Float) {
    val act = this as? Activity ?: return
    val attributes = act.window.attributes
    attributes.alpha = alpha
    act.window.attributes = attributes
}

// 快速双击
private var lastClickTime: Long = 0
private val SPACE_TIME = 500
fun isDoubleClick(): Boolean {
    val currentTime = System.currentTimeMillis()
    val isDoubleClick: Boolean // in range
    isDoubleClick = currentTime - lastClickTime <= SPACE_TIME
    if (!isDoubleClick) {
        lastClickTime = currentTime
    }
    return isDoubleClick
}

fun View.isVisibility(visibility: Boolean) {
    if (visibility) {
        if (this.visibility != View.VISIBLE) {
            this.visibility = View.VISIBLE
        }
    } else {
        if (this.visibility == View.VISIBLE) {
            this.visibility = View.GONE
        }
    }
}

fun View.showSoft() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = rootView?.findFocus()
    view?.let { imm.showSoftInput(view, InputMethodManager.SHOW_FORCED) }
}
