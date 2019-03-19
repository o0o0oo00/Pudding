# Pudding

Imitate [Alerter](https://github.com/Tapadoo/Alerter) and ☆⌒(*＾-゜)v THX!! a lot 

and what's diffente between us 

Use activity decorView we can show a view on the top of Activity。but when dialog is showing , its black background will cover the view , its not cool. so i create this repository to solve this problem , and  i hope you will like it.

TODO

1. 两种addView方式
   - [x] activity decorView
   - [ ] windowManager 
     - [x] 动画显示
     - [x] 生命周期控制
     - [ ] 有权限/无权限情况
       - [ ] 各个版本的适配
2. 内部显示队列(考虑可以使用kotlin通道来实现)
   - [ ] 上一条未消失时，后来一条等待
   - [ ] 直接顶掉上一条，上一条渐变消失
3. 左右/上下滑动消失