package com.zcy.fancydialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager


/**
 * @author:         zhaochunyu
 * @description:    封装基本属性
 *                  赋值mContext
 *                  降级Dialog背景阴影 为 Activity 透明度
 * @date:           2018/12/25
 */
abstract class BaseFragmentDialog : DialogFragment() {

    var mWidth = WRAP_CONTENT
    var mHeight = WRAP_CONTENT
    var mGravity = Gravity.CENTER
    var mOffsetX = 0
    var mOffsetY = 0
    var mAnimation: Int? = null
    var touchOutside: Boolean = true
    var lowerBackground = false // 是否降级背景，例如图片预览的时候不可以降级（设置Activity的透明度）
    lateinit var mContext: Context

    /****** listener ******/
    private var viewLoadedListener: ((View) -> Unit)? = null
    private var showListener: (() -> Unit)? = null
    private var disListener: (() -> Unit)? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    protected abstract fun setView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setStyle()
        val view = setView(inflater, container, savedInstanceState)
        viewLoadedListener?.invoke(view)
        return view
    }

    /**** 降低背景的Window等级 ****/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (lowerBackground) mContext.setBackgroundAlpha(0.35F)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        if (lowerBackground) mContext.setBackgroundAlpha(1F)
        super.onDestroyView()
    }

    /**
     * 防止同时弹出两个dialog
     */
    override fun show(manager: FragmentManager, tag: String?) {
        if (isDoubleClick() || activity?.isFinishing == true) {
            return
        }
        showListener?.invoke()
//        super.show(manager, tag)
        setBooleanField("mDismissed", false)
        setBooleanField("mShownByMe", true)
        val ft = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    private fun setBooleanField(fieldName: String, value: Boolean) {
        try {
            val field = DialogFragment::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            field.set(this, value)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        disListener?.invoke()
        super.dismiss()
    }

    fun onShow(listener: () -> Unit) {
        showListener = listener
    }

    fun onDismiss(listener: () -> Unit) {
        disListener = listener
    }

    /**
     * 布局加载完成监听事件
     * 用于 获取布局中的 view
     */
    fun onViewLoaded(listener: (View) -> Unit) {
        viewLoadedListener = listener
    }

    /**
     * 设置统一样式
     */
    private fun setStyle() {
        //获取Window
        val window = dialog?.window
        //无标题
        dialog?.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
        // 透明背景
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (lowerBackground) window?.setDimAmount(0F) // 去除 dialog 弹出的阴影
        dialog?.setCanceledOnTouchOutside(touchOutside)
        //设置宽高
        window!!.decorView.setPadding(0, 0, 0, 0)
        val wlp = window.attributes
        wlp.width = mWidth
        wlp.height = mHeight
        //设置对齐方式
        wlp.gravity = mGravity
        //设置偏移量
        wlp.x = dialog?.context?.dp2px(mOffsetX.toFloat()) ?: 0
        wlp.y = dialog?.context?.dp2px(mOffsetY.toFloat()) ?: 0
        //设置动画
        mAnimation?.also { window.setWindowAnimations(it) }
        window.attributes = wlp
    }

}