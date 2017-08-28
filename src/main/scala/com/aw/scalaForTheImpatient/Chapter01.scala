package com.aw.scalaForTheImpatient

object Chapter01 {
   def main(args: Array[String]) = {
     println("基础")
   }
/**
*  1.4 算术和操作符重载
*  Scala几乎可以使用任何符号作为方法名
*  方法的调用可以： a 方法 b 是 a.方法(b)的简写
*  1.6 apply方法
*  通常我们会使用类似函数调用的语法，背后的实现原理其实是调用一个apply方法，比如:
*/
   "Hello"(4) //将产出'o'
   "Hello".apply(4) //与上面等价
   BigInt("123456789")
   BigInt.apply("123456789") //与上面等价

}