package com.aw.scalaForTheImpatient
object Chapter07 {
   def main(args: Array[String]) = {
     println("包和引入")
   }
}

// 7.1 包 7.2作用域规则
// 1.包可以像内部类那样嵌套
// 2.源文件的目录和包之间没有关联关系
// 3.包和其他作用域一样支持嵌套，你可以访问上层作用域中的名称
// 4.所有包的超级父包为_root_，若发生引用包冲突可用绝对包名：_root_.x.y.z
// 5.包声明链x.y.z并不自动将中间包x和x.y变成可见
   package com{
     package study{ class Emp1 } // 使用com.study.Emp1访问
     class Emp2
     package st{
       class Emp3{
         var vc = new Emp2 //Emp3可以访问上层的Emp2
       }
     }
     package st3.st4.st5{
       //st3.st4的成员在这里不可见
       package st6{}
     }
   }
// 7.4 文件顶部标记法
// 位于文件顶部不带花括号的包声明在整个文件范围内有效
// package com.a.b
// package c //这个例子中，文件的所有内容属于com.a.b.c但是com.a.b包的内容是可见的，可以被直接引用
// 7.5 包对象
// 包不可以包含函数或变量的定义（虚拟机局限）。把工具函数或常量添加到包而不是对象中，是更加合理做法
// 包对象的出现正是为了解决这个局限.包对象可以持有函数和变量
// 每个包可以有一个包对象，需要在父包中定义，名称与子包一样。
   package com.a.b{
     package object c{
       val defaultName = "j.k.l" //使用com.a.b.c.defaultName访问
     }
     package c{
       class Person{var name = defaultName} //从包对象拿到常量
     }
   }
// 实现：包对象会被编译成带有静态方法和字段的JVM类，叫package.class,位于相应包下   
// 7.6 包可见性
// 在java中，没有被声明为public、private、protected的类成员在包含该类的包中可见。
// 在scala中使用private[包名]修饰符达到java的效果。以下方法在它自己的包中可见
   package com.a.b.c{
     class Class1{ private[c] def sc = "he"}
     class Class2{ val c = (new Class1).sc} //可以访问Class1私有成员
   }
// 你也可以将可见度延展到任何包
// 7.7 引入
// 引入语句可以引入包、类、变量
   import java.awt._ //引入某个包全部成员
// 7.8 任何地方都可以声明引入
// scala中，import语句可以出现在任何位置，import语句的效果一直延伸到包含该语句的块末尾
// 7.9 重命名和隐藏方法
// 引入语句可以重命名和隐藏特定成员
   import java.awt.{Color,Font} //使用选取器（selector），只引用这俩包中成员
   import java.util.{HashMap => JHashMap} //引入HashMap并重命名为JHashMap
   import java.util.{HashMap => _,_} //引入util包所有成员，除了HashMap
// 7.10 隐式引用
// java.lang._、scala._、Predef._总是被引入
