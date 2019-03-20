###  Window & WindowManager

#### Window与Activity

ActivityThread:

在`attach()`中，新建一个`Window`实例作为自己的成员变量，它的类型为`PhoneWindow`,这是抽象类`Window`的一个子类。然后设置`mWindow`的`WindowManager`。

##### Activity的布局是如何加载到WIndow上的

Activity中的window就是phoneWindow，而Activity中的setContentView就是调用的phoneWindow的setContentView。

````kotlin
public void setContentView(@LayoutRes int layoutResID) {
  getWindow().setContentView(layoutResID);
  initWindowDecorActionBar();
}
````

PhoneWindow.java/setContentView

```
if (mContentParent == null) {
installDecor();
} else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
mContentParent.removeAllViews();
}
```

PhoneWindow中 包含一个DecorView，如果没有就新建一个，通过`installDecor()`，内部调用`generateDecor(-1);`创建mDecor。

拿到了mDecor后去生成布局`mContentParent = generateLayout(mDecor);`

在`generateLayout`中，将Activity的布局addView到decorView上`mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);`

`onResourcesLoaded`内部实现最终的`addView(root, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));`

ActivityThread.java

DecorView添加到WIndowManager上

只有再Activity的`makeVisible()`被调用的时候，DecorView才会被addview进WIndowmanager



##### 不同的Activity对应的WindowManager是否相同

-   **相同**的`WindowManager`，**不同**的`window`

-   他们所依赖的`WindowManager`，是通过`context.getSystemService()`方法获取的，所以不管有多少Activity，Dialog，WindowManager的实例只有一个
-   `WindowManager`的实现类是`WindowManagerImpl`，而`WindowManagerImpl`中的addView，removeView等方法，又是直接调用了`WindowManagerGlobal`的相应方法。
-   `WindowManagerImpl`中依赖的`WindowManagerGlobal`也是单例模式创建的，所以app范围内，`WindowManagerGlobal`实例也只有一个
-   于是就解释了，不同的WindowManager为什么可以移除对方的View

#### Window 层级关系

我们知道View在window上的显示是有层级顺序的，层级高的覆盖在层级低的View之上。
Activity的层级为 `TYPE_APPLICATION` 

PopupWindow的层级为`TYPE_APPLICATION_PANEL` 

Dialog的层级为`TYPE_SYSTEM_DIALOG`

而我们只需要添加一个层级为`TYPE_APPLICATION_SUB_PANEL`的View到Window上面即可

##### 为什么不把View的层级设置的高一点呢？

```kotlin
android.view.WindowManager$BadTokenException: Unable to add window android.view.ViewRootImpl$W@308ebe3 -- permission denied for window type 2000
```

我们在这里验证了View的层级为2000以下的时候是不需要申请权限的

#### Window Lack

这里的处理方式是将传入的Activity绑定lifecycle，然后在onDestroy的时候，将**Pudding**`dismiss`掉

**Pudding**类继承`LifecycleObserver`

```kotlin
class Pudding : LifecycleObserver
```

绑定生命周期，并且Activity的每个生命周期都可以通知到**Pudding**

```kotlin
activity.lifecycle.addObserver(this)
```

监听状态

```
// window manager must associate activity's lifecycle
@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
fun onDestroy(owner: LifecycleOwner) {
choco.hide(windowManager ?: return, true)
owner.lifecycle.removeObserver(this) // 移除监听事件
}
```

