# Pudding

**Imitate [Alerter](https://github.com/Tapadoo/Alerter) and ☆⌒(*＾-゜)v THX!! a lot** 

### what's different

Use activity decorView we can show a view on top of Activity。but when dialog is showing , its black background will cover the view , that is not cool. so i create this repository to solve this problem , and  I hope it will work for you.

### Something new

* Use DSL style to config `Choco`

* Show Queue

* Cover Dialog/PopWindow

### WIKI

[WIKI中文说明](https://github.com/o0o0oo00/Pudding/blob/master/WIKIPAGE.md)

#### TODO

1.  两种addView方式
    -   [x] activity decorView
    -   [ ] windowManager 
        -   [x] 动画显示
        -   [x] 生命周期控制
        -   [ ] 有权限/无权限情况
        -   [ ] 各个版本的适配
2.  内部显示队列(考虑可以使用kotlin通道来实现)
    -   [ ] 上一条未消失时，后来一条等待
    -   [x] 直接顶掉上一条，上一条渐变消失
3.  左右/上下滑动消失动画
4.  配合[FancyDialog](https://github.com/o0o0oo00/FancyDialog)实现 Pudding cover dialog



存在 的问题：

- [x] 不同的Activity对应的WindowManager是否相同

    - [x] 相同的WindowManager，不同的window

        他们所依赖的WindowManager，是通过context.getSystemService()方法获取的，所以不管有多少Activity，Dialog，WindowManager的实例只有一个

- [x] 不同的WindowManager为什么可以移除对方的View

    - [x] 因为所有的Activity共同只有一个WindowManager

- [ ] 如何去维护一个Pudding队列

    - [x] 用 一个`Map<activity,list<pudding>>`
    - [ ] 什么时间 去clear这个map