package com.zcy.fancydialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zcy.fancydialog.list.ListDialogAdapter

/**
 * @author:         zhaochunyu
 * @description:    ${DESP}
 * @date:           2019/1/8
 */

// list点击事件一定是要暴露给外部的，所以，将实例返回出去，以便于onclick时候调用dismiss()
inline fun listDialog(block: ListDialog.() -> Unit): ListDialog {
    val dialog = ListDialog.newInstance()
    dialog.apply(block)
    return dialog
}

class ListDialog : BaseFragmentDialog() {
    var mLayoutManager: RecyclerView.LayoutManager? = null
    var mParentPadding: Int = 0
    var mPaddingTop: Int = MyApp.instance.dp2px(10F)
    var mPaddingLeft: Int = 0
    var mPaddingRight: Int = 0
    var mPaddingBottom: Int = 0
    var mBackground: Int? = null
    var mAdapter: ListDialogAdapter = ListDialogAdapter()
    var mItemDecoration: RecyclerView.ItemDecoration? = null

    override fun setView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.layout_list_dialog, container, false)
        view.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom)
        mBackground?.let { view.background = resources.getDrawable(it) }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = mLayoutManager ?: LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mAdapter
        mItemDecoration?.also { recyclerView.addItemDecoration(it) }
        return view
    }

    inline fun listSetting(
        noinline onClick: (View, Int) -> Unit,
        noinline onLoneClick: (View, Int) -> Unit,
        block: MutableList<Any>.() -> Unit
    ) {
        mAdapter.list.apply(block)
        mAdapter.setOnClickListener(onClick)
        mAdapter.setOnLongClickListener(onLoneClick)
    }

    companion object {
        fun newInstance() = ListDialog()
    }

}