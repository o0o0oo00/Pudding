package com.zcy.fancydialog

import android.app.Application

/**
 * @author:         zhaochunyu
 * @description:    application context
 * @date:           2019/1/2
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Application
    }
}