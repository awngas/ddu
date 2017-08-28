package com.aw.scalaForTheImpatient

object Chapter05 {
   def main(args: Array[String]) = {
     val aa = new Counter();
     aa.age1 = 10
     aa.current1
     
     println("类")
     
     class Test(n:String){
       import scala.beans.BeanProperty
       @BeanProperty var name1:String = _
     }
     val a = new Test("_")
     println(a.name1)
   }


// 5.1 简单类和无参方法
   class Counter{ //类是公有可见性
     private var value = 0 //字段必须初始化
     def increment() {value += 1} //方法默认是公有的
     def current() = value
// 调用无参方法是,可以写括号也可以不写
// 若定义方法时不带(),则调用函数时强制不带圆括号
   def current1 = value //myCounter.current1
// 5.2 带getter和setter的属性
// scala对每个字段都提供getter和setter方法
// 例子:下句会生成私有age字段和getter和setter方法
   var age = 0
// scala中的get/set分别叫做age和age_=
// 你可以显式的重新定义自己的getter和setter方法
   private var privateAge1 = 0
   def age1 = privateAge1
   def age1_=(newValue:Int){if(newValue > privateAge1) privateAge1 = newValue}
// scala为每个字段生成getter和setter规则
// 如果字段是私有的,则getter和setter方法也是私有的
// 如果字段是val,则只生成getter方法
// 如果不需要任何getter或setter,可以将字段声明为private[this]
// 5.4 对象私有字段
// 与java相同,方法可以访问该类的所有对象的私有字段.如:this.value = other.value
// scala允许定义更严格访问限制,通过private[this]修饰符修饰(该对象称为:对象私有的)
   private[this] var pvale = 0 //在该类方法中, 该类其他实例对象.pvale是不允许的
// scala允许将访问权限赋予指定的类,private[类名]修饰符可以定义仅有指定类的方法可以访问给定的字段
// 类名必须是当前定义的类,或者是包含该类的外部类,这种情况下,会生成get/set方法,允许外部类访问该字段   
   private[Counter] var pcale = 0
// 5.5 Bean属性
// javaBeans规范规定java的get/set方法命名为getXxx/setXxx,与scala自动生成的不同
// 可以通过为字段标注@BeanProperty,这样符合javabean规范的方法会自动生成
   import scala.beans.BeanProperty
   @BeanProperty var name1:String = _
// 表5.1 针对字段生成的方法
// scala字段                                           #生成的方法                                                      #何时使用
// val/var name               #公有的name;name_=(仅限var)       #实现一个可以被公开访问并且背后是以字段形式保存的属性
// @BeanProperty val/var name #公有的name;getName();name_=(仅限var);setName(..)(仅限var)     #与javaBeans互操作
// private val/var name       #私有的name;name_=(仅限var)       #用于将字段访问限制在本类的方法，就像和java一样。
// private[this] val/var name #无                                                                      #用于将字段访问限制在同一个对象上调用的方法，并不经常用到
// private[类名] val/var name  #依赖于具体实现                                             #将访问权限赋予外部类，并不经常用到
// ---------------------------------------------------
// 5.6 辅助构造器
// class有一个主构造器和若干辅助构造器 
// 辅助构造器1.名称为this 2.辅助构造器必须先调用其他辅助构造器或者主构造器
   def this(name:String){ //辅助构造器
     this() //调用主构造器
     this.name1 = name
   }
// 5.7 主构造器
// 类若没有显示定义主构造器，会有一个无参的主构造器
   class Preson(val name:String = "",val age:Int = 0){ //接受默认参数
     println("主构造器") //主构造器会执行语句
   }
// 1.主构造器与类名合并 2.主构造器会执行类定义中的所有语句
// 主构造器的参数可以有以下形式
// 主构造器参数                                                       #生成的字段/方法
// name:String                       #对象私有字段，若没有方法使用name,则没有该字段
// private val/var name:String       #私有字段，私有getter/setter方法
// val/var name:String               #私有字段，公有getter/setter方法
// @BeanProperty val/var name:String #私有字段，公有scala版和javaBeans版的getter/setter方法
// ---------------------------------------------------
// 若想将主构造器变成私有的，可如下定义
   class Preson2 private(val id:Int){}
// 5.8 嵌套类
// 在scala中，你可以在函数中定义函数，在类中定义类
   import scala.collection.mutable.ArrayBuffer
   class Network{
     class Member(val name:String) {val contacts = new ArrayBuffer[Member]}
     private val members = new ArrayBuffer[Member]
     def join(name:String) = {
       val m = new Member(name);members+=m;m
     }
   }
   
   val a = new Network
   val b = new Network
// 与java不同scala中。每个实例都有它自己的Member类，所以a.Member和b.Member是不同的两个类
   new a.Member("1") //在java中要写成a.new Member()
// 如果不希望这么做，可以由以下几种方法处理
// 将Member类移到别处，比如：Network的伴生对象中
// 或者使用类型投影Network#Member，其含义是"任何Network的Member"
   class Network1{
     class Member(val name:String){val c = new ArrayBuffer[Network1#Member]}
   }
// 在内嵌类中，可以使用外部类.this的方式访问外部类的引用，也可以用如下语法
// 建立一个指向该引用的别名
   class Network2(val n:String){ outer =>
     class Member(val name:String) {
       def d = name + outer.n //使用
     }
   }
   
   }

}
   