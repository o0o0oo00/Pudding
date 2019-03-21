package com.zcy.pudding.demo

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zcy.fancydialog.askDialog
import com.zcy.pudding.Pudding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    // 默认形式
    fun click1(view: View) {
        Pudding.create(this) {
            setTitle("This is Title")
            setText("this is text")
        }
            .show()
    }

    // 设置背景颜色、字体
    fun click2(view: View) {
        Pudding.create(this) {
            setChocoBackgroundColor(resources.getColor(R.color.colorAccent))
            setTitleTypeface(Typeface.DEFAULT_BOLD)
        }.show()
    }

    // 更改icon
    fun click3(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            setText("this is text")
            setIcon(R.drawable.ic_event_available_black_24dp)
        }.show()
    }

    // 长说明 文字形式
    fun click4(view: View) {
        Pudding.create(this) {
            setText(R.string.verbose_text_text)
            setIcon(R.drawable.ic_event_available_black_24dp)
        }.show()
    }

    // 永久显示
    fun click5(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            enableInfiniteDuration = true
        }.show()
    }

    // loading 形式
    fun click6(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            setEnableProgress(true)
        }.show()
    }

    // loading 形式
    fun click7(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            setText("This is Text , it's very short and I don't like short \n This is Text , it's very short and I don't like short")
            setChocoBackgroundColor(resources.getColor(R.color.color_FFCC00))
            enableInfiniteDuration = true
            addButton("OK", R.style.PuddingButton, View.OnClickListener {
                hide()
                Toast.makeText(this@MainActivity, "click ok", Toast.LENGTH_SHORT).show()
            })
            addButton("NO", R.style.PuddingButton, View.OnClickListener {
                Toast.makeText(this@MainActivity, "click no", Toast.LENGTH_SHORT).show()
            })

        }.show()
    }

    fun showAlertDialog(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setMessage("message")
            .create().show()
    }

    fun showFancyDialog(view: View) {
        askDialog(supportFragmentManager) {
            mTitle = "FancyDialog"
            mMessage = "Use DSL it's so Awesome"
            onlySure = true
            lowerBackground = true
        }
    }

    // 启动一个Activity ,验证是否存在lack window exception
    fun startAnActivity(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}
