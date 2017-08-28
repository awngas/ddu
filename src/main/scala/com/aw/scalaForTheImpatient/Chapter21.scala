package com.aw.scalaForTheImpatient

object Chapter21 {
   def main(args: Array[String]) = {
     println("第21章 隐式转换和隐式参数")
     
/**
21
要点:
隐式转换用于在类型之间做转换
你必须引入隐式转换,并确保它们可以以单个标识符的形式出现在当前作用域
隐式参数列表会要求指定类型的对象,它们可以从当前作用域中以单个标识符定义的隐式对象获取,或者从目标类型的伴生对象获取
如果隐式参数是一个单参数的函数,那么它同时也会被作为隐式转换使用
类型参数的上下文界定要求存在一个指定类型的隐式对象
如果有可能定位到一个隐式对象,这一点可以作为证据证明某个类型转换时合法的,
21.1 隐式转换 Implicit Conversions
所谓隐式转换函数(implicit conversion function)指的是那种以implicit关键字
声明的带有单个参数的函数。这样的函数被自动应用，将值从一种类型转换为另一种类型。
例子：implicit def int2Fraction (n:Int)=Fraction(n,1)
这样我们就可以做如下表达式求值：val rst = 3 * Fraction(4,5)
约定俗称的函数命名source2Target
21.3 引入隐式转换
scala会考虑如下的隐式转换函数：
1.位于源或目标类型的伴生对象中的隐式函数。
2.位于当前作用域可以以单个标识符指代的隐式函数。
比如int2Fraction函数，可以放到它的伴生对象中，这样它就能够被使用了
或者，比如我们放在了FractionCover对象中，而这个对象位于com.a.b包，
如果想使用这个转换，就需要引入FractionCover对象。
像这样：import com.a.b.FractionCover._ 或者com.a.b.FractionCover.int2Fraction 
单凭如下这样引入语句是不行的import com.a.b.FractionCover
21.4 隐式转换规则
在以下情况下会考虑：
当表达式的类型与预期的类型不同时
sqrt(Fraction(1,4)) sqrt预期的是一个Double,所以会转换
当对象访问一个不存在的成员时：
new File("a").read
当对象调用某个方法，而该方法的参数声明与传入参数不匹配时：
3 * Fraction(4,5)
有三种情况编译器不会尝试使用隐式转换：
如果代码能够在不使用隐式转换的前提下通过编译，则不会使用隐式转换。
编译器不会尝试执行多个转换，比如convert1(convert2(a))*b
存在二义性的转换编译器会报错，比如convert1(a)*b与a*convert2(b)合法
命令行参数 scalac -Xprint:type class.scala 将会看到隐式转换后的源码
21.5 隐式参数
函数或方法可以带有一个标记为implicit的参数列表，这种情况下，编译器将会查找缺省值。
提供给该函数或方法。
示例：*/
  case class Delimiters(left: String, right: String)
  def quote(what: String)(implicit delims: Delimiters) = delims.left + what + delims.right
/**
这样你可以省略隐式参数列表：quote("aa")
这种情况下，编译器将会查找一个类型为Delimiters的隐式值，这必须是一个被声明为implicit的值。
编译器将会在如下两个地方查找这样一个对象：
在当前作用域所有可以用单个标识符指代的满足类型要求的val和def.
与所要求类型相关联的类型的伴生对象。相关联的类型包括所要求类型本身，以及它的类型参数(如果它是一个参数化的类型的话)。
例如：object FP{implicit val q = Deli("a","b")...}这样我们就可以引入import FP._或import FP.q 这样隐式的提供给了q函数
21.6 利用隐式参数进行隐式转换
比如：def s[T](a:T,b:T)=if(a< b) a else b 编译不通过，不知道a和b有没有< 操作符 */
  def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (order(a) < b) a else b //通过
/**
由于Ordered[T]特质有一个接受T作为参数的< 操作符，所以正确
order是一个带有单个参数的函数,被打上implicit标签,
因此它不仅是一个隐式参数,它还是一个隐式转换,所以,我们可以略去order显示调用 */
  def smaller2[T](a: T, b: T)(implicit order: T => Ordered[T])
    = if (a < b) a else b // 通过，将调用order(a)<b，如果a没有带<操作符
/**
21.7 上下文界定
类型参数可以有一个形式为T:M的上下文界定，其中M是另一个泛型类型。
它要求作用域中存在一个类型为M[T]的隐式值。该隐式值可以被用在该类的方法当中。
*/
  class Pair[T: Ordering](var first: T, val second: T) {
    def smaller(implicit ord: Ordering[T]) =
      if (ord.compare(first, second) < 0) first else second
/**
* 如果我们new一个Pair(40,2),编译器将推断出我们需要一个Pair[Int]，
* scala.Predef 有一个类型为Ordering[Int]的隐式值，因此Int满足上下文界定。
* 如果你愿意，你也可以用Predef类的implicitly方法获取该值。
*/
	  def smaller2 =
	    if(implicitly[Ordering[T]].compare(first, second)<0) first else second
/**
* implicitly函数在Predef中定义如下：def implicitly[T](implicit e: T) = e
* 或者利用Ordered特质中定义的从Ordering到Ordered的隐式转换。引入此转换，就可以使用关系操作符
*/
//	  def smaller3 = {
//	      import Ordered._
//        if(first < second) first else second
//	}
/**
* 这样你可以随时实例化Pair[T],只要满足存在类型为Ordering[T]的隐式值得条件即可。
* 举例来说，如果你想要一个Pair[Point],则可以组织一个隐式的Ordering[Point]值:
* implicit object PointOrdering extends Ordering[Point]{
*   def compare(a:Point,b:Point) = ...
* }
*/
  }//Pair[T:Ordering] 类结尾
/**
21.8 类型证明--【看不懂】
在17章中，我们看过下面这样的类型约束，
T =:= U   T是否等于U
T <:< U   T是否是U的子类型
T <%< U   T是否可以被视图(隐式)转换为U
要使用这样的类型约束，做法是提供一个隐式参数。
*/
  def firstLast[A,C](it:C)(implicit ev:C <:< Iterable[A]) = (it.head,it.last)

// =:=,<:<和<%<是带有隐式值得类，定义在Predef对象当中，例如：<:<从本质上讲就是：

  abstract class <:<[-From,+To] extends Function1[From,To]
  
  object <:< {
    implicit def conforms[A] = new (A <:< A){def apply(x:A) =x}
  }
/**
* 假定编译器需要处理约束implicit ev:String <:< AnyRef。它会在伴生对象中查找
* 类型为String <:< AnyRef的隐式对象。注意 <:< 相对于From是逆变的，而相对于To是协变得。
* 因此如下对象：<:<.conforms[String]可以被当做String <:< AnyRef的实例使用。
* 我们把ev称作"类型证明对象"--它的存在证明：String是AnyRef的子类型。
* 这里的类型证明对象是恒等函数(即永远返回参数原值的函数)。要弄明白为什么这个恒等函数是必须的,请看如下代码:
* def firstLast[A, C](it: C)(implicit ev: C <:< Iterable[A]) = (it.head, it.last)
* 编译器实际上并不知道C是一个Iterable[A]，<:< 不是语言特性。因此it.head和it.last
* 这样的调用不合法。但ev是一个带有单个参数的函数，因此也是一个从C到Iterable[A]的隐式转换。
* 编译器将会应用这个隐式转换，计算ev(it).head和ev(it).last
* 
* 21.9 @implicitNotFound注解
* 该注解告诉编译器在不能构造出带有该注解的类型的参数时给出错误提示，这样做的目的是给程序员由意义的错误提示。
* 见P333页
* 21.10 CanBuildFrom解读
* 在第一张中,我们忽略了那个隐式的CanBuildFrom参数,现在我们可以说下它的工作原理.
* 考虑map方法，稍微简化说，map就是一个Iterable[A,Repr]的方法
* def map[B,That](f:(A)=>B)(implicit bf:CanBuildFrom[Repr,B,That]):That={
*   val builder = bf()
*   val iter = iterator()
*   while(iter.hasNext) builder += f(iter.next())
*   builder.result
* }
* Repr的意思是“展现类型”，该参数让我们可以选择合适的构建器工厂来构建诸如Range或String这样的非常规集合。
* 说明:在Scala中,map实际上被定义在TraversableLike[A,Repr]特质中,这样,更常用的Iterable特质就不需要背着Repr这个类型参数的包袱
* 其他说明见书:P334
*/

   }
}

