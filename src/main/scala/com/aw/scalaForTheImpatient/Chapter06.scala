package com.aw.scalaForTheImpatient

object Chapter06 {
   def main(args: Array[String]) = {
     println("第6章 对象")
   }
// 第6章 对象
// 6.1 单例对象
   object Accounts{ private var ls =0 }
// 1.单例对象拥有类的所有特性，可以扩展其他类或特质
// 2.单例对象不能提供构造器参数
// 3.单利对象的构造器在该对象第一次被使用时调用
// 6.2 伴生对象
// 与类同名的单例对象为该类的伴生对象
   class Account{ val id = Account.nln() }
   object Account{ private def nln() = 1 } // 伴生对象
// 类和伴生对象可以互相访问私有特性。他们必须存在同一个源文件中
// 6.4 apply方法
// 当使用 Object(参数1,..,参数N) 时，apply方法会调用
// 我们可以在类的伴生对象定义apply返回一个新建的类，免去使用new Object创建对象
// 6.5 应用程序对象
// 每个Scala程序都必须从一个对象的main方法开始，方法类型为Array[String]=>Unit
// 也可以拓展App特质，将程序代码放入构造器方法体内,args属性为命令行参数
   object Hello extends App{ println("hello")}
// 原理与打印消耗时间和DelayedInit见P73
// 6.6 枚举
// scala没有枚举类型，使用Enumeration类产生枚举
   object Enume extends Enumeration{
     val Aa,Ba = Value;val Ca = Value(0,"St")
   }
// 使用：Enume.Aa;Enume.values;Enume(0);Enume.withName("Aa")
}