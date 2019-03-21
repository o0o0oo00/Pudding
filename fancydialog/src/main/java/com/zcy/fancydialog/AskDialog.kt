package com.zcy.fancydialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager

/**
 * @author:         zhaochunyu
 * @description:    BaseFragmentDialog 使用范例
 * @date:           2019/1/2
 */

//  DSL style
inline fun askDialog(fragmentManager: FragmentManager, dsl: AskDialog.() -> Unit) {
    val dialog = AskDialog.newInstance().apply(dsl)
    dialog.show(fragmentManager, "dialog")
}

open class AskDialog : BaseFragmentDialog() {

    var mTitle: String? = null
    var mMessage: String? = null
    var msgGravity: Int = Gravity.CENTER
    var onlySure: Boolean = false
    var mColor: Int = MyApp.instance.color(R.color.colorAccent)
    var mCancelText: String = MyApp.instance.string(R.string.cancel)
    var mSureText: String = MyApp.instance.string(R.string.sure)

    protected var cancelClicks: (() -> Unit)? = null
    protected var sureClicks: (() -> Unit)? = null

    override fun setView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.layout_ask_dialog, container, false)

        val dialogTitle = view.findViewById<AppCompatTextView>(R.id.title)
        val dialogMessage = view.findViewById<AppCompatTextView>(R.id.message)
        val sureButton = view.findViewById<AppCompatTextView>(R.id.sure)
        val cancelButton = view.findViewById<AppCompatTextView>(R.id.cancel)
        val line = view.findViewById<View>(R.id.vertical_line)
        cancelButton.isVisibility(!onlySure)
        line.isVisibility(!onlySure)

        dialogTitle.text = mTitle
        mMessage?.also {
            dialogMessage.visibility = View.VISIBLE
            dialogMessage.text = it
            dialogMessage.gravity = msgGravity
        }
        cancelButton.text = mCancelText
        cancelButton.setTextColor(mColor)
        cancelButton.setOnClickListener {
            cancelClicks?.let { onClick ->
                onClick()
            }
            dismiss()
        }

        sureButton.text = mSureText
        sureButton.setTextColor(mColor)
        sureButton.setOnClickListener {
            sureClicks?.let { onClick ->
                onClick()
            }
            dismiss()
        }

        return view
    }

    fun cancelClick(key: String? = null, onClick: () -> Unit) {
        key?.let {
            mCancelText = it
        }
        cancelClicks = onClick
    }

    fun sureClick(key: String? = null, onClick: () -> Unit) {
        key?.let {
            mSureText = it
        }
        sureClicks = onClick
    }

    companion object {
        fun newInstance(): AskDialog = AskDialog()
    }

}