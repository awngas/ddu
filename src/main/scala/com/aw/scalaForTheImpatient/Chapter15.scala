package com.aw.scalaForTheImpatient

import scala.beans.BeanProperty
import scala.annotation.meta.beanGetter
import java.io.IOException
import scala.annotation.unchecked.uncheckedVariance


object Chapter15 {
   def main(args: Array[String]) = {
     println("11")
   }
// 注解
// 15.1 什么是注解
// 注解的语法与java一样，可以对scala类使用java注解。
// scala有一些特有注解。java注解并不影响编译器如何将源码翻译成字节码，
// 他们仅仅往字节码添加数据，以便外部工具可以利用他们
// scala中注解可以影响编译过程，比如第5章@BeanProperty
// 15.2 什么可以被注解
// 可以为类、方法、字段、局部变量和参数添加注解
// 可以同时添加多个注解。先后次序没有影响
// @BeanProperty @Id var username = _
// 给主构造器添加注解，需要放置在构造器之前，并加上一对圆括号(注解不带参数)
// class Credentials @Inject() (var u:String)
// (myMap.get(key):@unchecked ) match {..} //为表达式添加注解
// class Mycls[@specialized T]  //为类型参数添加注解
// String @cps[Unit] //针对实际类型的注解应放置在类型名称之后，@cps带一个类型参数
// 15.3 注解参数
// 注解可以有带名参数，如果参数名只有value，名称可以略去、如果注解不带参数，圆括号可以略去
// @Test(timeout = 100,expected = classOf[IOException])
// 不同于java，scala注解的参数可以是任何类型
// 15.4 注解实现
// 注解必须扩展Annotation
// 注解类可以选择扩展StaticAnnotation或ClassfileAnnotation
// 默认情况，注解都有应用范围。
// 元注解@param @field @getter @setter @beanGetter @beanSetter将使得注解被附在别处
// 例：@Id注解将被应用到java的getId方法。
// @Entity class Credentials{
//   @(Id @beanGetter) @BeanProperty var id =0
// }
// 15.5 针对java特性的注解
// 15.5.1 java修饰符
   @volatile var d = false //替换java volatile
   @transient var s = false; //替换transient，瞬态字段不会被序列化
// @strictfp def ca(x:Double) = .. //对应strictfp修饰符
// @native 替换java native
// 15.5.2 标记接口
// @cloneable 替换java Cloncable 可被克隆
// @remote 替换java.rmi.Remote 远程对象
// @SerialVersionUID 指定序列化版本
// 15.5.3 受检异常
// java编译器会跟踪受检异常，如果你从java代码中调用scala方法，其签名应包含那些可能被抛出的受检异常
// 用@throws注解来生成正确的签名
   class Book{
     @throws(classOf[IOException]) def read(filename:String){}
   }
// java版签名为：void read(String filename) throws IOException
// 没有@throws注解，java代码将不能捕获该异常
// 15.5.4 变长参数
// @varargs注解从java调用scala的带有变长参数的方法
// 15.5.5 JavaBeans
// @scala.reflect.BeanProperty 注解生成javabeans风格的getter和setter方法
// 15.6 用于优化的注解
// 15.6.1 尾递归
// 递归调用有时能被转换成循环，节约栈空间。
   def sum(xs:Seq[Int]):BigInt = if(xs.isEmpty) 0 else xs.head + sum(xs.tail)
// 这样的无法被优化，最后一部是加法，不是递归调用
   def sum2(xs:Seq[Int],partial:BigInt):BigInt =
     if(xs.isEmpty) partial else sum2(xs.tail,xs.head+partial)
// 这样是可以被优化的。最后一部调用方法，变换成调回方法顶部的循环。不会栈溢出
// 可以显式明确方法可以使用尾递归优化，@tailrec注解，如果方法不能被优化会报错。例如：sum2位于某个类而不是某个对象中，这种情况，可以将方法挪到对象中，或者将它声明为private或final
// 消除递归更加通用的机制是“蹦床”，它会执行一个循环，不停地调用函数，每个函数返回下一个将被调用的函数。尾递归是一个特例，每个函数都返回它自己
// scala有一个名为TailCalls的工具对象，可以轻松实现蹦床。
// 相互递归的函数返回类型为TailRec[A],要么返回done(result),要么返回tailcall(fun)，fun是下一个被调用函数，这必须是一个不带额外参数且同样返回TailRec[A]的函数
// 例子：
   import scala.util.control.TailCalls._
   def evenLength(xs:Seq[Int]):TailRec[Boolean] =
     if(xs.isEmpty) done(true) else tailcall(oddLength(xs.tail))
   def oddLength(xs:Seq[Int]):TailRec[Boolean] =
     if(xs.isEmpty) done(false) else tailcall(evenLength(xs.tail))
   evenLength(1 to 100000).result //获取最终结果
// 15.6.2 跳转表生成与内联
// 在c++或java中，switch语句通常可以被编译成跳转表，比一系列if/else更搞笑。scala也有此功能
// @switch注解可以检测scala的match语句是不是真的被编译成了跳转表
// (str: @switch) match{....}
// 内联
// @inline建议编译器做内联
// @noinline告诉编译器不要内联
// 15.6.3 可省略方法
// @elidable注解给那些可以在生产代码中移除的方法打上标记
// @elidable(500) 有一个参数，参数指定抛弃的级别，内置了一些常量， 比如禁用断言:scala -Xelid-below 2001 x.scala 。断言默认启用
// 15.6.4 基本类型的特殊化
// 打包和解包基本类型的值是不高效的-但在泛型代码中常见
// def allDifferent[T](x:T,y:T)=x!=y
// 调用allDifferent(3,4),方法被调用前，每个整数值都会被包装成java.lang.Integer.
// 当然我们可以给这八个基本类型写重载的版本如def allDifferent[T](x:Int,y:Int)=x!=y
// 使用@specialized可以自动生成这些方法
   def allDifferent[@specialized(Long,Double) T](x:T,y:T) = x!=y
// 括号可选，可以指定：Unit,Boolean,Byte,Short,Char,Int,Long,Float,Double类型
// 15.7 用于错误和警告的注解
// 已过时警告
// @deprecated注解，该注解有两个选填参数message和since
// @deprecatedName可以被应用到参数上，并给出一个该参数之前使用过的名称
   def draw(@deprecatedName('sz) size:Int,style:Int=0) {}
// 这里的'sz是一个 符号 以单引号开头的名称，名称相同的符号一定是唯一的，它们==方法用引用判断相等性，字符串而是比对内容，符号表示的是程序中某个项目的名称
// @implicitNotFound注解用于在某个隐式参数不存在时生成有意义的错误提示。参见21章
// @unchecked注解用于在匹配不完整时取消警告信息。比如：知道某个列表不可能是空，编译器不会报告说我没给Nil选项
// (lst: @unchecked) match { case head :: tail => ...}
// @uncheckedVariance注解会取消与型变相关的错误提示。举例：java.until.Comparator按理应该是逆变的。如果Stduends是Person子类
// 那么Comparator[Student]时，我们也可以用Comparator[Person]。但java泛型不支持型变，通过该注解解决问题
   trait Comparator[-T] extends java.lang.Comparable[T @uncheckedVariance]
}