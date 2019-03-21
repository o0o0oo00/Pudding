package com.zcy.fancydialog.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zcy.fancydialog.R
import kotlinx.android.synthetic.main.item_list_dialog.view.*

/**
 * @author:         zhaochunyu
 * @description:    ${DESP}
 * @date:           2019/1/8
 */
class ListDialogAdapter : RecyclerView.Adapter<ListDialogViewHolder>() {
    val list: MutableList<Any> = mutableListOf()
    private var onClickListener: ((View, Int) -> Unit)? = null
    private var onLongClickListener: ((View, Int) -> Unit)? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: ListDialogViewHolder, p1: Int) {
        p0.itemView.text.text = list[p1].toString()
        p0.itemView.tag = list[p1]
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ListDialogViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_list_dialog, p0, false)
        view.setOnClickListener {
            onClickListener?.invoke(view, p1)
        }

        view.setOnLongClickListener {
            onLongClickListener?.invoke(view, p1)
            return@setOnLongClickListener true
        }

        return ListDialogViewHolder(view)
    }

    fun setOnClickListener(listener: (View, Int) -> Unit) {
        onClickListener = listener
    }

    fun setOnLongClickListener(listener: (View, Int) -> Unit) {
        onLongClickListener = listener
    }

}

class ListDialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
