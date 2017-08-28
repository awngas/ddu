package com.aw.scalaForTheImpatient

object Chapter12 {
   def main(args: Array[String]) = {
     println("高阶函数")
   }
// 12.1 作为值得函数
// 在scala中，函数是“头等公民”，就和数字一样，你可以在变量中存放函数
   import scala.math._
   val fun = ceil _ //fun的类型为 (Double)=>Double
// 从技术上讲，_将ceil方法转成了函数，在scala中，你无法直接操纵方法，而只能直接操纵函数
// 你可以对函数进行：1.调用 2.传递它，存放在变量或作为参数传递给另一个参数
   fun(3.14);
   Array(3.14,1.42,2.0).map(fun)
// 12.2 匿名函数
// scala中，不需要给每一个函数命名
   val triple = (x:Double) => 3 * x
   Array(3.14,1.42,2.0).map( (x:Double) => 3 * x ) //结果：Array(9.42,4.26,6.0)
// 也可以将函数参数包在花括号中
   Array(3.14,1.42,2.0).map{ (x:Double) => 3 * x }
   Array(3.14,1.42,2.0) map { (x:Double) => 3 * x } //常用在使用中置表示法时
// 12.3 带函数参数的函数
   def valueAtOneQuarter(f:(Double) => Double) = f(0.25) //函数类型：((Double) => Double) => Double
   valueAtOneQuarter(ceil _) //1.0
   valueAtOneQuarter(sqrt _) //0.5
// 函数的类型写作： (参数类型) => 结果类型
// 接受函数参数的函数，被称作高阶函数（higher-order function）
// 高阶函数也可以产出另一个函数
   def mulBy(factor:Double) = (x:Double) => factor * x //类型：(Double) => ((Double) => Double)
// 12.4 参数(类型)推断
// 将匿名函数传递给另一个函数或方法时，scala会进行类型推断，所以可以进行一些简写
   valueAtOneQuarter((x:Double) => 3 * x)
   valueAtOneQuarter( (x) => 3*x) //省略类型
   valueAtOneQuarter( x => 3*x) //只有一个参数的函数，可以省略()
   valueAtOneQuarter(3 * _) //如果参数在=>右侧只出现一次，可以用_替换掉它
// 这些简写方式仅在参数类型已知的情况下有效
// val fun1 = 3 * _ //错误，无法推断出类型
   val fun2 =3 * (_:Double)
   val fun3:(Double)=>Double = 3*_
// 12.5 一些有用的高阶函数
   (1 to 9).map(0.1 * _) //快速产出0.1,0.2...0.9集合
   (1 to 9).map("*" * _).foreach(println _) //多行递增打印“*”
   (1 to 9).filter(_ % 2 ==0) //2,4,6,8 输出匹配的元素
   (1 to 9).reduceLeft(_ * _) //将他们用到序列中的所有元素，从左到右。 等同于:1*2*3*4*5*6*7*8*9
// 12.6 闭包
// 在scala中，可以在任何作用域定义函数：包、类甚至是另一个函数或方法。在函数体内，你可以访问到相应作用域内的任何变量。
// 这里有个问题：函数可以在变量不再处于作用域内时被调用
   def mulBy2(factor:Double) = (x:Double) => factor * x
   val t1 = mulBy2(3);val t2 = mulBy2(0.5);println(t1(14) + " "+t2(14)) //打印42 7
// t1,t2中函数变量factor分别设为3和0.5，虽然factor在函数返回后，会在栈上弹出来，但是每一个返回的函数都有自己的factor设置
// 这样的函数被称作闭包(closure)。这些函数实际上是以类的对象方式实现的，该类由一个实例变量factor和一个包含了函数体的apply方法
// 12.7 SAM转换
// scala中支持带函数参数的函数
// java中通常的做法是将动作放在一个实现某接口的类中，然后将该类的一个实例传递给另一个方法。很多时候，这些接口都只有单个抽象方法，在java中他们被称作SAM类型。
// 例如：在按钮被点击时递增一个计数器
   import javax.swing._
   import java.awt.event.ActionListener
   import java.awt.event.ActionEvent
   var counter =0
   val button = new JButton("按钮")
   button.addActionListener(
       new ActionListener{override def actionPerformed(event:ActionEvent){counter += 1}})
// 我们可以使用隐式转换，来使用更简便的调用方法
   implicit def makeAction(action:(ActionEvent) => Unit) = 
     new ActionListener{override def actionPerformed(event:ActionEvent){action(event)}}
// 使用
   button.addActionListener((event:ActionEvent) => counter +=1) //简单调用
// 12.8 柯里化（currying）
// 柯里化指的是将原来接受两个参数的函数变成新的接受一个参数的函数的过程，新的函数返回一个以原有第二个参数作为参数的函数
   def mulOneAtTime(x:Int) = (y:Int) => x*y //柯里化函数
   mulOneAtTime(6)(7) //调用
// scala支持如下简写来定义柯里化函数
   def mulOneAtTime2(x:Int)(y:Int) = x*y
// 有时，你想要用柯里化把某个函数参数单拎出来，以提供更多用于类型推断的信息
// 例子：比较两个序列在摸个对比条件下相同
   val a=Array("abc","ABC");val b=Array("Abc","Abc");
   a.corresponds(b)(_.equalsIgnoreCase(_)) //_.equalsIgnoreCase(_)为(a:String,b:String)=>a.equalsIgnoreCase(b)简写
// def corresponds[B](that: GenSeq[B])(p: (A,B) => Boolean): Boolean 该方法的声明
// 12.9 控制抽象
// 无参且无返回值函数的调用
   def runInThread(block:()=>Unit){new Thread{override def run(){block()}}.start()}
// 调用
   runInThread {()=>println("Hi");Thread.sleep(1000);println("Bye") }
// 但这么做不美观，可以如下定义
   def runInThread2(block: =>Unit){new Thread{override def run(){block}}.start()}
   runInThread2 { println("Hi");Thread.sleep(1000);println("Bye") }
// 我们可以构建控制抽象：看上去像是编程语言关键字的函数。比如until语句，工作原理类似while
   def until(condition: =>Boolean)(block: =>Unit){
     if(!condition){block;until(condition)(block)}
   }
// 使用:
   var x=10
   until(x==10){
     x -= 1;println(x)
   }
// 这样的函数参数叫做换名调用参数，和常规参数不同，函数在被调用时，参数表达式不会被求值，x==0不会被求值
// 12.10 return表达式
// scala不需要使用return语句返回函数值，不过，你可以用return来从一个匿名函数中返回值给包含这个匿名函数的带名函数
   def indexOf(str:String,ch:Char):Int ={
     var i=0
     until(i==str.length){
       if(str(i)==ch) return i;i+=1 //return执行时，indexOf终止并返回值
     }
     return -1
   }
// 这个控制流程的实现依赖一个在匿名函数的return表达式中抛出的特殊异常，该异常从until函数传出，并被indexOf函数捕获
// 注意:如果异常在被送往带名函数前，在try代码块中被捕获了，那么相应的值就不会被返回
}