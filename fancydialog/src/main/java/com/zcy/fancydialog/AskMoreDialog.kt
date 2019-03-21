package com.zcy.fancydialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager

/**
 * @author:         zhaochunyu
 * @description:    ${DESP}
 * @date:           2019/1/2
 */

inline fun askMoreDialog(fragmentManager: FragmentManager, block: AskMoreDialog.() -> Unit) {
    AskMoreDialog.instance().apply(block).show(fragmentManager, "dialog")
}

class AskMoreDialog : AskDialog() {

    var mButton3: String? = null
    private var mButton3Color: Int? = null
    private var button3Clicks: (() -> Unit)? = null

    override fun setView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.layout_ask_dialog_vertical, container, false)

        val dialogTitle = view.findViewById<AppCompatTextView>(R.id.title)
        val dialogMessage = view.findViewById<AppCompatTextView>(R.id.message)
        val sureButton = view.findViewById<AppCompatTextView>(R.id.sure)
        val cancelButton = view.findViewById<AppCompatTextView>(R.id.cancel)
        val button3 = view.findViewById<AppCompatTextView>(R.id.button3)

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

        button3.text = mButton3
        button3.setTextColor(mButton3Color ?: mColor)
        button3?.setOnClickListener {
            button3Clicks?.let { onClick ->
                onClick()
            }
            dismiss()
        }

        return view

    }

    fun button3Clicks(key: String, color: Int? = null, onClick: () -> Unit) {
        mButton3 = key
        color?.also {
            mButton3Color = it
        }
        button3Clicks = onClick
    }

    companion object {
        fun instance() = AskMoreDialog()
    }

}