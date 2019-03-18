package com.zcy.pudding.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zcy.pudding.Pudding
import com.zcy.pudding.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun click1 (view: View){
        Pudding.create(this).config {
            setTitle("This is Title")
            setText("this is text")
        }.show()
    }
}
