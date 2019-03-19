package com.zcy.pudding

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color.alpha
import android.graphics.PixelFormat
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.android.synthetic.main.layout_choco.view.*
import java.lang.ref.WeakReference

/**
 * @author:         zhaochunyu
 * @description:    Choco helper class
 * @date:           2019/3/15
 */
class Pudding : LifecycleObserver {
    private lateinit var choco: Choco
    private var hasPermission = false

    private var windowManager: WindowManager? = null

    // after create
    fun show() {
        windowManager?.also {
            try {
                log("show addView")
                it.addView(choco, initLayoutParameter())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // time over dismiss
        choco.postDelayed({
            log("postDelayed hide")
            if (choco.enableInfiniteDuration) {
                return@postDelayed
            }
            choco.hide(windowManager ?: return@postDelayed)
        }, Choco.DISPLAY_TIME)

        // click dismiss
        choco.body.setOnClickListener {
            log("setOnClickListener hide")
            choco.hide(windowManager ?: return@setOnClickListener)
        }

    }

    // window manager must associate activity's lifecycle
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        log("onDestroy removeViewImmediate")
        choco.hide(windowManager ?: return, true)
        owner.lifecycle.removeObserver(this)
    }

    // todo 暂时先不写
    fun checkPermission() {
        activityWeakReference?.get()?.let { activity ->
            val check = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW)
            if (check != PackageManager.PERMISSION_GRANTED) {
                /**
                 * 判断该权限请求是否已经被 Denied(拒绝)过。  返回：true 说明被拒绝过 ; false 说明没有拒绝过
                 *
                 * 注意：
                 * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
                 * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
                 */
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {
                    log("onViewClicked: 该权限请求已经被 Denied(拒绝)过。");
                    //弹出对话框，告诉用户申请此权限的理由，然后再次请求该权限。
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 1);

                } else {
                    log("onViewClicked: 该权限请未被denied过");

                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), 1);
                }

            } else {
                hasPermission = true
            }

        }
    }

    private fun initLayoutParameter(): WindowManager.LayoutParams {
        // init layout params
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            0, 0,
            PixelFormat.TRANSPARENT
        )
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.TOP

        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or // 不获取焦点，以便于在弹出的时候 下层界面仍然可以进行操作
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR // 确保你的内容不会被装饰物(如状态栏)掩盖.
        // popWindow的层级为 TYPE_APPLICATION_PANEL
        //        TODO("adjust permission to choice type")


        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL

        return layoutParams
    }

    // must invoke first
    private fun setActivity(activity: AppCompatActivity, block: Choco.() -> Unit) {
        activityWeakReference = WeakReference(activity)
        choco = Choco(activity)
        windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        log("setActivity windowManager = $windowManager")
        activity.lifecycle.addObserver(this)


        // 指定使用者的高阶函数named dsl 配置 choco属性
        choco.apply(block)
    }

    companion object {

        private fun log(e: String) {
            Log.e(this::class.java.simpleName, "${this} $e")
        }

        private var activityWeakReference: WeakReference<Activity>? = null

        private val listPudding: MutableList<Pudding> = mutableListOf()

        @JvmStatic
        fun create(activity: AppCompatActivity, block: Choco.() -> Unit): Pudding {
            val pudding = Pudding()
            pudding.setActivity(activity, block)
            clearBeforePudding(activity)
            Handler().postDelayed({ listPudding.add(pudding) }, 60)
            return pudding
        }

        // remove before pudding
        private fun clearBeforePudding(activity: AppCompatActivity) {
            (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).also { windowManager ->
                log("listPudding size = ${listPudding.size}")
                if (listPudding.size <= 1) return@also

                // todo how to remove pudding from listPudding in right time
                for (i in 0 until listPudding.size) {

                    if (!listPudding[i].choco.isAttachedToWindow) continue

                    ViewCompat.animate(listPudding[i].choco).alpha(0F).withEndAction {
                        if (listPudding[i].choco.isAttachedToWindow) {
                            windowManager.removeViewImmediate(listPudding[i].choco)
                        }
                    }
                }
            }
        }


    }
}