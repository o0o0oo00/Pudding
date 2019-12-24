package com.zcy.pudding.demo

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zcy.fancydialog.askDialog
import com.zcy.pudding.Pudding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Pudding.create(this@MainActivity) {
                    setTitle("This is Title")
                }.show()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
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
    fun click8(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            setText("This is Text , it's very short and I don't like short \n This is Text , it's very short and I don't like short")
            enableInfiniteDuration = true
            enableSwipeToDismiss()
        }.show()
    }

    // loading 形式
    fun click9(view: View) {
        Pudding.create(this) {
            setTitle("Choco Title")
            setText("This is Text , it's very short and I don't like short \n This is Text , it's very short and I don't like short")
            onShow {
                Toast.makeText(this@MainActivity, "onShowListener", Toast.LENGTH_SHORT).show()
            }
            onDismiss {
                Toast.makeText(this@MainActivity, "onDismissListener", Toast.LENGTH_SHORT).show()
            }
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
            .setTitle("AlertDialog")
            .setMessage("normal AlertDialog will cover Pudding")
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
