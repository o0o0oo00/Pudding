#  Window & WindowManager

## 从老生常谈的WindowManager到手写一个Alert弹窗控件

#### Window的添加与删除

每一个Window都对应着一个View和一个ViewRootImpl。Window和View通过ViewRootImpl来建立联系。

ViewRootImpl是Window的实际展现形式，因为WindowManager操作的都是View，而ViewRootImpl是继承的ViewParent

通过WindowManager的addView来实现在屏幕上添加一个View，但它只是一个接口，真正的实现是WindowManagerImpl

WindowManagerImpl这种工作模式是典型的桥接模式，将所有的操作全部委托给WindowManagerGlobal来实现，而其内部则是通过ViewRootImpl来更新界面并完成Window的添加过程。

##### 添加过程

WindowManagerGlobal.java/addView

```kotlin
root = new ViewRootImpl(view.getContext(), display);
mViews.add(view);
mRoots.add(root);
root.setView(view, wparams, panelParentView);
```

这里的这个root，是新创建出的`ViewRootImpl(view.getContext(), display);`前面说了，它其实是Window的实际展现形式，暂且理解为window本身，这样我们将view添加到window本身上了。

##### 删除过程

WindowManagerGlobal.java

```kotlin
removeView(View view, boolean immediate)
```

它有一个**immediate**参数表示是否是立即移除，其中**removeView**是异步方法**removeViewImmediate**为同步方法。方法内部拿到了view的index之后去`ArrayList<ViewRootImpl> mRoots`中寻找对应的**Index**。然后调用`removeViewLocked(index, immediate);`此时将View添加到了`ArraySet<View> mDyingViews`中去，并没有立即执行删除，而是发送一个消息

```
boolean die(boolean immediate) {
...
 if (immediate && !mIsInTraversal) {
            doDie();
            return false;
        }
  mHandler.sendEmptyMessage(MSG_DIE);
}
```

如果是同步删除，会直接移除，并刷新**mRoots**等属性列表

此时我们就会收到`onDetachedFromWindow()`这个方法的调用。

```kotlin
 void doDie() {
 ...
 if (mAdded) {
 dispatchDetachedFromWindow();
 }
 
 WindowManagerGlobal.getInstance().doRemoveView(this);
 }
```



#### Window与Activity

Activity的启动过程很复杂，最终会由**ActivityThread**中的`performLaunchActivity()`来完成整个启动过程，在这个方法内部会通过类加载器创建Activity的实例对象，并调用其`attach`方法为其关联运行过程中所依赖的一系列上下文环境变量

在**Activity**`attach()`中，新建一个`Window`实例作为自己的成员变量，它的类型为`PhoneWindow`,这是抽象类`Window`的一个子类。然后设置`mWindow`的`WindowManager`。

##### Activity的布局是如何加载到Window上的

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

PhoneWindow中 包含一个DecorView，

如果为null就新建一个，通过`installDecor()`，内部调用`generateDecor(-1);`创建mDecor。

如果mDecor不为null那么直接关联上phoneWindow`mDecor.setWindow(this);`



拿到了mDecor后去生成布局`mContentParent = generateLayout(mDecor);`

在`generateLayout`中，将Activity的布局addView到decorView上`mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);`

`onResourcesLoaded`内部实现最终的`addView(root, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));`



DecorView添加到WIndowManager上

在ActivityThread的handleResumeActivity方法中，首先会调用Activity的onResume方法，接着会调用Activity的`makeVisible()`

只有在Activity的`makeVisible()`被调用的时候，DecorView才会被addview进WIndowmanager

```java
    void makeVisible() {
        if (!mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(mDecor, getWindow().getAttributes());
            mWindowAdded = true;
        }
        mDecor.setVisibility(View.VISIBLE);
    }

```



##### 不同的Activity对应的WindowManager是否相同

-   **相同**的`WindowManager`，**不同**的`window`
-   他们所依赖的`WindowManager`，是通过`context.getSystemService()`方法获取的，所以不管有多少Activity，Dialog，WindowManager的"实例"只有一个
-   `WindowManager`的实现类是`WindowManagerImpl`，而`WindowManagerImpl`中的addView，removeView等方法，又是直接调用了`WindowManagerGlobal`的相应方法。
-   `WindowManagerImpl`中依赖的`WindowManagerGlobal`也是单例模式创建的，所以app范围内，`WindowManagerGlobal`实例也只有一个
-   于是就解释了，不同的WindowManager为什么可以移除对方的View



#### Window 层级关系

Dialog的弹窗需要Context，而这个Context必须是Activity，其内的Window包含一个token。如果没有这个Token就会报错。

```
“Caused by: android.view.WindowManager$BadToken-Exception: Unable to add window --token null is not for an application”
```

而系统级的Window就不需要token。因此我们可以

```kotlin
 dialog.getWindow().setType(LayoutParams.TYPE_SYSTEM_OVERLAY)
```

来实现一个系统级别的Dialog。But运行时权限了解一下、国产手机了解一下。

我们知道View在window上的显示是有层级顺序的，层级高的覆盖在层级低的View之上。
Activity的层级为 `TYPE_APPLICATION` 

PopupWindow的层级为`TYPE_APPLICATION_PANEL` 

Dialog的层级为`TYPE_SYSTEM_OVERLAY`

而我们只需要添加一个层级为`TYPE_APPLICATION_SUB_PANEL`的View到Window上面即可

##### 为什么不把View的层级设置的高一点呢？

```kotlin
android.view.WindowManager$BadTokenException: Unable to add window android.view.ViewRootImpl$W@308ebe3 -- permission denied for window type 2000
```

我们在这里验证了View的层级为2000以下的时候是不需要申请权限的

那么我们要实现这个WindowManager添加的View显示在Dialog之上，就只剩剩下一个东西了，就是降低**Dialog**的背景等级/改为使用**PopUpWindow**来代替**Dialog**。

改Dialog的背景阴影为Activity的透明度

#### Window Leak

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

