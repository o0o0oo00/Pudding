package com.zcy.pudding

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.layout_choco.view.*

/**
 * @author:         https://github.com/o0o0oo00
 * @description:    custom Choco view
 *
 * @date:           2019/3/15
 */
class Choco @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var animEnter: ObjectAnimator
    private val animEnterInterceptor = OvershootInterpolator()
    private var enableIconPulse = true

    var enableInfiniteDuration = false
    private var enableProgress = false
    private var enabledVibration = false
    private var buttons = ArrayList<Button>()

    private var onlyOnce = true


    init {
        inflate(context, R.layout.layout_choco, this)
    }

    /**
     * 初始化配置，如loading 的显示 与 icon的动画 触摸反馈等
     */
    private fun initConfiguration() {

        if (enableIconPulse) {
            icon?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alerter_pulse))
        }

        if (enableProgress) {
            icon.visibility = View.GONE
            progress.visibility = View.VISIBLE
        } else {
            icon.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }

        buttons.forEach {
            buttonContainer.addView(it)
        }

        if (enabledVibration) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e(TAG, "onAttachedToWindow")
        initConfiguration()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e(TAG, "onDetachedFromWindow")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(TAG, "onMeasure")

        if (onlyOnce) {
            onlyOnce = false
            animEnter = ObjectAnimator.ofFloat(this@Choco, "translationY", -this@Choco.measuredHeight.toFloat(), -80F)
            animEnter.interpolator = animEnterInterceptor
            animEnter.duration = ANIMATION_DURATION
            animEnter.start()
        }
    }

    fun hide(removeNow: Boolean = false) {
        if (!this@Choco.isAttachedToWindow) {
            return
        }
        val windowManager = (this.context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager) ?: return
        if (removeNow) {
            if (this@Choco.isAttachedToWindow) {
                windowManager.removeViewImmediate(this@Choco)
            }
            return
        }
        body.isClickable = false
        val anim = ObjectAnimator.ofFloat(this@Choco, "translationY", -80F, -this@Choco.measuredHeight.toFloat())
        anim.interpolator = AnticipateOvershootInterpolator()
        anim.duration = ANIMATION_DURATION
        anim.start()
        Handler().postDelayed({
            if (this@Choco.isAttachedToWindow) {
                windowManager.removeViewImmediate(this@Choco)
            }
        }, ANIMATION_DURATION)
    }


    fun setChocoBackgroundColor(@ColorInt color: Int) {
        body.setBackgroundColor(color)
    }

    /**
     * Sets the Choco Background Drawable Resource
     *
     * @param resource The qualified drawable integer
     */
    fun setChocoBackgroundResource(@DrawableRes resource: Int) {
        body.setBackgroundResource(resource)
    }

    /**
     * Sets the Choco Background Drawable
     *
     * @param drawable The qualified drawable
     */
    fun setChocoBackgroundDrawable(drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            body.background = drawable
        } else {
            body.setBackgroundDrawable(drawable)
        }
    }

    /**
     * Sets the Title of the Choco
     *
     * @param titleId String resource id of the Choco title
     */
    fun setTitle(@StringRes titleId: Int) {
        setTitle(context.getString(titleId))
    }

    /**
     * Sets the Title of the Choco
     *
     * @param title String object to be used as the Choco title
     */
    fun setTitle(title: String) {
        if (!TextUtils.isEmpty(title)) {
            text.visibility = View.VISIBLE
            text.text = title
        }
    }

    /**
     * Set the Title's text appearance of the Title
     *
     * @param textAppearance The style resource id
     */
    fun setTitleAppearance(@StyleRes textAppearance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text.setTextAppearance(textAppearance)
        } else {
            text.setTextAppearance(text.context, textAppearance)
        }
    }

    /**
     * Set the Title's typeface
     *
     * @param typeface The typeface to use
     */
    fun setTitleTypeface(typeface: Typeface) {
        text.typeface = typeface
    }

    /**
     * Sets the Text of the Choco
     *
     * @param textId String resource id of the Choco text
     */
    fun setText(@StringRes textId: Int) {
        setText(context.getString(textId))
    }

    /**
     * Set the Text's typeface
     *
     * @param typeface The typeface to use
     */
    fun setTextTypeface(typeface: Typeface) {
        subText.typeface = typeface
    }

    /**
     * Sets the Text of the Choco
     *
     * @param text String resource id of the Choco text
     */
    fun setText(text: String) {
        if (!TextUtils.isEmpty(text)) {
            this.subText.visibility = View.VISIBLE
            this.subText.text = text
        }
    }

    /**
     * Set the Text's text appearance of the Title
     *
     * @param textAppearance The style resource id
     */
    fun setTextAppearance(@StyleRes textAppearance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            subText.setTextAppearance(textAppearance)
        } else {
            subText.setTextAppearance(subText.context, textAppearance)
        }
    }

    /**
     * Set the inline icon for the Choco
     *
     * @param iconId Drawable resource id of the icon to use in the Choco
     */
    fun setIcon(@DrawableRes iconId: Int) {
        icon.setImageDrawable(AppCompatResources.getDrawable(context, iconId))
    }

    /**
     * Set the icon color for the Choco
     *
     * @param color Color int
     */
    fun setIconColorFilter(@ColorInt color: Int) {
        icon.setColorFilter(color)
    }

    /**
     * Set the icon color for the Choco
     *
     * @param colorFilter ColorFilter
     */
    fun setIconColorFilter(colorFilter: ColorFilter) {
        icon.colorFilter = colorFilter
    }

    /**
     * Set the icon color for the Choco
     *
     * @param color Color int
     * @param mode  PorterDuff.Mode
     */
    fun setIconColorFilter(@ColorInt color: Int, mode: PorterDuff.Mode) {
        icon.setColorFilter(color, mode)
    }

    /**
     * Set the inline icon for the Choco
     *
     * @param bitmap Bitmap image of the icon to use in the Choco.
     */
    fun setIcon(bitmap: Bitmap) {
        icon.setImageBitmap(bitmap)
    }

    /**
     * Set the inline icon for the Choco
     *
     * @param drawable Drawable image of the icon to use in the Choco.
     */
    fun setIcon(drawable: Drawable) {
        icon.setImageDrawable(drawable)
    }

    /**
     * Set whether to show the icon in the Choco or not
     *
     * @param showIcon True to show the icon, false otherwise
     */
    fun showIcon(showIcon: Boolean) {
        icon.visibility = if (showIcon) View.VISIBLE else View.GONE
    }

    /**
     * Set if the Icon should pulse or not
     *
     * @param shouldPulse True if the icon should be animated
     */
    fun pulseIcon(shouldPulse: Boolean) {
        this.enableIconPulse = shouldPulse
    }

    /**
     * Enable or disable progress bar
     *
     * @param enableProgress True to enable, False to disable
     */
    fun setEnableProgress(enableProgress: Boolean) {
        this.enableProgress = enableProgress
    }

    /**
     * Set the Progress bar color from a color resource
     *
     * @param color The color resource
     */
    fun setProgressColorRes(@ColorRes color: Int) {
        progress?.progressDrawable?.colorFilter = LightingColorFilter(MUL, ContextCompat.getColor(context, color))
    }

    /**
     * Set the Progress bar color from a color resource
     *
     * @param color The color resource
     */
    fun setProgressColorInt(@ColorInt color: Int) {
        progress?.progressDrawable?.colorFilter = LightingColorFilter(MUL, color)
    }

    /**
     * Enable or Disable haptic feedback
     *
     * @param vibrationEnabled True to enable, false to disable
     */
    fun setEnabledVibration(enabledVibration: Boolean) {
        this.enabledVibration = enabledVibration
    }


    /**
     * Show a button with the given text, and on click listener
     *
     * @param text The text to display on the button
     * @param onClick The on click listener
     */
    fun addButton(text: String, @StyleRes style: Int, onClick: View.OnClickListener) {
        Button(ContextThemeWrapper(context, style), null, style).apply {
            this.text = text
            this.setOnClickListener(onClick)
            buttons.add(this)
        }

//        // Alter padding
//        body?.apply {
//            this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom / 2)
//        }
    }

    /**
     * Set whether to enable swipe to dismiss or not
     */
    fun enableSwipeToDismiss() {
        body.setOnTouchListener(SwipeDismissTouchListener(body, object : SwipeDismissTouchListener.DismissCallbacks {
            override fun canDismiss(): Boolean {
                return true
            }

            override fun onDismiss(view: View) {
                hide(true)
            }

            override fun onTouch(view: View, touch: Boolean) {
                // Ignore
            }
        }))
    }

    companion object {
        private val TAG = Choco::class.java.simpleName
        const val DISPLAY_TIME: Long = 3000
        const val ANIMATION_DURATION: Long = 500
        private const val MUL = -0x1000000

    }

}