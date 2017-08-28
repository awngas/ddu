package com.aw.scalaForTheImpatient

import java.awt.Rectangle

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object Chapter18 {
   def main(args: Array[String]) = {
     println("高级类型")

     //-----------------------------------------
     trait Bind[F[_]] {
       // note the new ↓ fa argument
       def map[A, B](fa: F[A])(f: A => B): F[B]
       def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
     }

     def tupleTC[F[_], A, B](fa: F[A], fb: F[B])(implicit F: Bind[F]): F[(A, B)] = F.flatMap(fa){a => F.map(fb)((a, _))}

     //-------------------------------------------
   }
// 18.1 单例类型
// 给定任何引用v,你可以得到类型v.type,它有两个可能的值：v和null.
// 例子：
   class Document {
    def setTitle(t: String) = {/*其他代码*/ this}
    def setAuthor(a: String) : this.type = {/*其他代码*/ this} //返回值为this.type
   }
   class Book extends Document{ def addChapter(c: String) = {/*其他代码*/ this} }
   val book = new Book()
//  book.setTitle("1").addChapter("2") //错误，setTitle返回this,类型是Document.解决办法是，声明setTitle返回类型为this.type
   book.setAuthor("1").addChapter("2") //正确
// 有人喜欢讲调用写成英语句子那样，如：
// book set Title to "Scala for the Impatient"
// 该代码将被解析成： book.set(Title).to("Scala for the Impatient")
// 要让这段代码工作，set得是一个参数为单例Title的方法
   object Title
   class Document1 {
     private var useNextArgAs: Any = null
     def set(obj: Title.type): this.type = { useNextArgAs = obj; this }
     def to(arg: String) = {}
   }
// 注意set方法参数，不能用set(obj:Title),因为Title指代的是单例对象，而不是类型
   val book1 = new Document1
   book1 set Title to "Scala for the Impatient"
// 18.2 类型投影
// 在第5章，我们知道嵌套类从属于包含它的外部对象
   class Network{
     class Member(val name: String){
       val contacts = new ArrayBuffer[Member]
     }
     private val members = new ArrayBuffer[Member]
     def join(name:String) = {val m = new Member(name); members += m; m}
   }
// 它的每个实例都有自己Member类，例子：network1.Member和network2.Member是不同的类
   val n1 = new Network; val n2 = new Network; val n1m1 = n1.join("1"); val n2m1 = n2.join("1"); //n1m1.contacts += n2m1 错误
// 如果不想要这个约束， 可以把Member类移动到NetWork类外，比如Network的伴生对象中，
// 或者你要的就是细粒度的类，只是偶尔想使用更为松散的定义，那么可以用“类型投影”
// Network#Member,意思是“任何Network的Member”
   class Network1{ class Member1{ val c = new ArrayBuffer[Network1#Member1] } }
// 适用于在程序中的某些地方但不是所有地方使用“每个对象自己的内部类”这个细粒度特征
// 18.3 路径
// 形如： com.horstmann.impatient.chatter.Member 这样的表达式称之为路径。
// 在最后的类型前，路径的所有组成部分都必须是“稳定的”，也就是说它必须指定到单个、有穷的范围。
// 组成的部分必须是以下当中的一种：包，对象，val，this、super、super[S]、C.this、C.super或C.super[S]
// 路径的组成部分不能是类。也不能是var
   var chatter = new Network
// val fred = new chatter.Member //错误-chatter不稳定
// 18.4类型别名
// 对于复杂类型，可以用type关键字创建一个简单的别名
   class Case1 {
     import scala.collection.mutable._
     type Index = HashMap[String, (Int, Int)]
   }
// Case1.Index可以代替scala.collection.mutable.HashMap[String,(Int,Int)]
// 类型别名必须被嵌套在类或对象中，它不能出现在scala文件的顶层
// 18.5 结构类型
// 所谓结构类型指的是一组关于抽象方法、字段和类型的规格说明，这些抽象方法、字段和类型是满足该规格的类型必须具备的。
   def appendLines(target: { def append(str: String): Any }, lines: mutable.Iterable[String]) {
     for (l <- lines) { target.append(l); target.append("\n") }
   }
// 你可以对任何具备append方法的类的实例调用appendLines方法，这比定义一个Appendable特质更为灵活，
// 因为你可能并不总是能够将该特质添加到使用的类上。
// 幕后scala使用发射来调用target.append，开销比常规方法大，所以，应该只在需要抓住那些无法共享一个特质的类的共通行为的时候才使用结构类型
// 18.6 复合类型
// 复合类型定义如下： T1 with T2 with T3 ；其中T1,T2,T3等是类型，要想成为该复合类型的实例，某个值必须满足每一个类型的要求才行，也称交集类型。
   val image = new ArrayBuffer[java.awt.Shape with java.io.Serializable]()
   image += new Rectangle(1,1,1,1) //可以，Rectangle是Serializable的
// image += new Area(...) //不可以，Area是Shape但不是Serializable
// 你可以把结构类型的声明添加到简单类型或复合类型
// Shape with Serializable {def contains(p:Point): Boolean}
// 该类型的实例必须既是Shape的子类型也是Serializable的子类型，并且必须由一个带Point参数的contains方法
// 从技术上讲，结构类型{def append{str:String}:Any} 是下面代码的简写：AnyRef{def append{str:String}:Any}
// 复合类型 Shape with Serializable 是以下代码的简写：Shape with Serializable{}
// 18.7 中置类型 - Infix Types
// 中置类型是一个带有两个类型参数的类型，以“中置”语法表示，类型名称写在两个类型参数之间，举例来说，你可以写作
// String Map Int 而不是 Map[String,Int]
// 中置表示法在数据中很常见，举例，A×B = {(a,b)|a∈A,b∈B} 指组件类型分别为A和B的对偶的集，在scala中，该类型被写作(A,B)
// 若想用数学表示法，可定义
   type *[A,B] = (A,B)
// 在此刻你就可以写String * Int 而不是(String,Int)了
// 所有中置类型操作符都有相同优先级，左结合的，除非他们名称以:结尾。中置类型的名称可以是任何操作符字符的序列(除了单个*号)
// 18.8 存在类型 - Existential Types
// 存在类型为了与java类型通配符兼容，存在类型定义方式是在类型表达式之后跟上forSome {...}，花括号中包含type和val的声明
// Array[T] forSome { type T <: JComponent} 与17章类型通配符效果一样 Array[_ <: JComponent]
// Scala的类型通配符只不过是存在类型的“语法糖”
// Array[_] 等同于 Array[T] forSome {type T}
// 而Map[_,_]等同于 Map[T,U] forSome { type T; type U}
// forSome表示法允许我们使用更复杂的关系，而不仅限于类型通配符能表达的那些
// Map[T,U] forSome { type T; type U <: T}
// 你可以在forSome代码中使用val声明，
// n.Member forSome { val n: Network } //就其自身而言，并没什么特别用处，完全可以用类型投影Network#Member
// 但是在更复杂的情况下：
   def process[M <: n.Member forSome { val n: Network }](m1: M,m2: M) = (m1,m2)
// 该方法将会接受相同网络的成员，但拒绝那些来自不同网络的成员
   val chatter1 = new Network
   val myFace1 = new Network
   val fred = chatter1.join("Fred")
   val wilma = chatter1.join("Wilma")
   val barney = myFace1.join("Barney")
   process(fred,wilma)
// process(fred,barney) //编译会出错
// 18.9 scala类型系统
// 类型         |  语法                               |   说明
// 类或特质     | class C , trait C                   |   参加5,10章
// 元组类型     | (T1,...,Tn)                         |   4.7节
// 函数类型     | (T1,...,Tn)=>T                      |
// 带注解的类型 | T@A                                 |   15章
// 参数化类型   | A[T1,...,Tn]                        |   17章
// 单例类型     | value.type                          |   18.1节
// 类型投影     | O#I                                 |   18.2节
// 复合类型     | T1 with T2 with ... with Tn {声明}  |   18.6节
// 中置类型     | T1 A T2                             |   18.7节
// 存在类型     | T forSome {type和val声明}           |   18.8节
// 18.10 自身类型 - self types
// 第10章中，你看到了特质可以要求混入它的类拓展自另一个类型，你用自身类型的声明来定义特质
// this: 类型=>
// 例子见10.13节。如果你想要提供多个类型要求，可以用复合类型
// this: T with U with ... =>
// 你可以把自身类型的语法和第五章介绍过的“用于包含this的别名”语法结合在一起使用，
// 如果你给变量起的名称不是this，那么它就可以在子类型中通过那个名称使用
   trait Group{
     outer : Network =>
       class Member{ /*代码*/}
   }
// Group特质要求它被添加到NetWork的子类，而在Member中，你可以用outer来指代Group.this
// 自动类型并不会自动继承。
// trait ManagedException extends LoggedException {...}
// 该代码会报错，说ManagedException并未提供Exception,你需要重复自身类型声明
// trait ManagedException extends LoggedException {
//    this: Exception => ...
// }
// 18.11 依赖注入 Dependency Injection
// 通过组件构建的大型系统，每个组件都会有不同的实现。比如有一个模拟数据库和一个真实数据库，某个实现可能用到某个数据库，其他实现用到别的数据库。
// 组件之间存在某种依赖关系，如，数据访问组件依赖于日志功能，java有spring或OSGI这样的模块系统。每个组件都描述它所依赖的其他组件的接口，而对实际组件实现的引用
// 是在应用程序被组装起来的时候“注入”的。
// scala中，可以通过特质和自身类型达到一个简单的依赖注入效果
// 对日志功能而言。假定我们有如下特质
// trait Longger {def log(msg:String)}
// 我们有该特质两个实现，ConsoleLogger和FileLogger
// 用户认证特质由一个对日志功能的依赖，用于记录认证失败
// trait Auth{ this: Logger => def loggin(id:String,password:String): Boolean}
// 应用逻辑又依赖于上述两个特质
// trait App{ this:Logger with Auth => ...}
// 然后我们可以像这样组装我们的应用
// object MyApp extends App with FileLogger("test.log") with MockAuth("users.txt")
// 这种特质的组合有些别扭。我们是有蛋糕模式做出更好的设置
   trait LoggerComponent {
      trait Logger {}
      val logger: Logger
      class FileLogger(file: String) extends Logger {}
   }
   trait AuthComponent {
      this: LoggerComponent =>
        trait Auth {}
        val auth: Auth
        class MockAuth(file: String) extends Auth {}
   }
// 使用：
   object AppComponents extends LoggerComponent with AuthComponent{
    val logger = new FileLogger("test.log")
    val auth = new MockAuth("users.txt")
   }
// 18.12 抽象类型 - abstract types
// 类或特质可以定义在一个在子类被具体化的抽象类型
   trait Reader{
     type Contents
     def read(fileName:String):Contents
   }
// 在这里，类型Contents是抽象的，具体子类需要指定这个类型
   class StringReader extends Reader{
      type Contents = String
      def read(fileName: String) = Source.fromFile(fileName,"UTF-8").mkString
   }
// 同样效果可以通过类型参数实现
   trait Reader1[C]{
     def read(fileName:String):C
   }
   class StringReader1 extends Reader1[String]{
     def  read(fileName:String) = Source.fromFile(fileName,"UTF-8").mkString
   }
// 哪种方式更好？scala经验法则是：
// 如果类型是在类被实例化时给出，则使用类型参数，比如构造HashMap[String,Int],你会想要在这个时候控制使用的类型
// 如果类型是在子类中给出的，则使用抽象类型。我们的Reader例子。
// 另外，当有多个类型依赖时，抽象类型用起来更方便，避免使用一长串类型参数
   trait Reader3{
     type In; type Contents
     def read(in:In):Contents
   }
// 如果用类型参数，就需要扩展自Reader3[File,String],伸缩性不好，而且抽象类型能够描述类型间那些微妙的相互依赖
// 抽象类型可以由类型界定，就和类型参数一样。
   trait Listener{
     type Event <: java.util.EventObject
   }
// 18.13 家族多态 - Family Polymorphism
// 见书，讲的是如何对那些跟着一起变化的类型家族建模，同时共用代码，并保持类型安全。
// 例子是，客户端java的事件处理，有不同的事件，而每种事件都有单独的监听器接口，例子设计一个管理监听器的通用机制。使用泛型类型和抽象类型
// 18.14 高等类型 - Higher-Kinded Types
// 泛型类型List依赖于类型T并产出一个特定的类型(给一个Int得到类型List[Int])，像List这样的泛型类型有时被称作类型构造器(type constructor)
// 在scala可以定义出依赖于依赖其他类型的类型的类型
// 为什么要用？摘自网上：其主要作用是对一组非原生类型类进行抽象，再结合高阶函数(higher-order functions)和组合函数实现抽象化处理，从而屏蔽复杂类型带来的代码冗余。
// trait Iterable[E]{
//   def iterator():Iterable[E]
//   def map[F](f:(E) => F):Iterable[F]
// }
// class Buffer[E] extends Iterable[E]{ // 实现类
//   def iterator():Iterable[E] = ...
//   def map[F](f:(E) => F):Buffer[F] = ...  // 想返回Buffer[E]
// }
// 如果我们想返回一个Buffer,而不仅只是Iterable，那么我们不能再Iterable中实现这个方法。解决办法：使用类型构造器参数化Iterable
   trait Iterable_1[E, C[_]] {
     def iterator(): Iterator[E]
     def build[F](): C[F]
     def map[F](f: (E) => F): C[F]
   }
// 这样一来，Iterable就一来一个类型构造器来生成结果，以C[_]表示，这使得Iterable成为一个高等类型
// map返回的类型可以是，也可以不是与调用该map方法的原Iterable相同的类型。比如：
// class Range extends Iterable[Int,Buffer]  //第二个参数是类型构造器Buffer
// 要实现Iterable中的map方法，我们需要寻求更多的支持,Iterable需要能够产出一个包含了任何类型F的值得容器，
// 我们来定义一个Container类--你可以向他添加值得东西
    trait Container[E] {
      def +=(e: E): Unit
    }
// build方法被要求产出这样一个对象
   trait Iterable[E, C[X] <: Container[X]] {
     def build[F](): C[F]
     def iterator(): Iterator[E]
     def map[F](f : (E) => F) : C[F] = {
       val res = build[F]()
       val iter = iterator()
       while (iter.hasNext) res += f( iter.next() )  // idea这块提示错误,scala版本2.12.2，提示的错误是，预期E实际F，
       res                                            // 如果将Contaniner[E]改为[F]，就没有错误，应该是IDE BUG，f方法的返回值是F,而res的类型为C[F]，+=函数签名是def +=(e: E)，估计是这个原因，eclipse没有报错
     }
   }
// 类型构造器C现在被限制为一个Container，因为我们知道可以往build方法返回的对象添加项目，我们不再对参数C使用通配符，因为我们需要表明C[X]是一个针对同样的X的容器
// 我们在Iterable特质中实现map，这样，子类就不需要提供它们自己的map实现了。
// 下面是Range类的定义。它是一个Iterable，可以遍历内容，但它不是Container,不能对它添加值
//  class Range(val low: Int, val high: Int) extends Iterable[Int, Buffer] {
//    def iterator() = new Iterator[Int] {
//      private var i = low
//      def hasNext = i <= high
//      def next() = { i += 1; i - 1 }
//    }
//    def build[F]() = new Buffer[F] //这个代码也会提示No Manifest available for F
//  }
// 而Buffer不同，它既是Iterable又是Container
//   class Buffer[E : Manifest] extends Iterable[E, Buffer] with Container[E] {
//     private var capacity = 10
//     private var length = 0
//     private var elems = new Array[E](capacity) // See note
//     def iterator() = new Iterator[E] {
//       private var i = 0
//       def hasNext = i < length
//       def next() = { i += 1; elems(i - 1) }
//     }
////    override def build[F]() = new Buffer[F] //但是改成上面这样会提示 No Manifest available for F，暂时还没有办法解决，以后再弄
//     def build[F : Manifest]() = new Buffer[F] //这个代码有问题，原样提示Buffer类没有实现build类，需要做抽象类，但是
//     def +=(e: E) {
//       if (length == capacity) {
//         capacity = 2 * capacity
//         val nelems = new Array[E](capacity) // See note
//         for (i <- 0 until length) nelems(i) = elems(i)
//         elems = nelems
//       }
//       elems(length) = e
//       length += 1
//     }
//}
// 本例子有个额外的复杂性，它跟高等类型没关系，为了构造出泛型的Array[E],类型E必须满足我们在17讨论过的Manifest上下文界定
// 示例展示了高等类型的典型应用，Iterator依赖Container，但Container不是一个普通类型-它是制作类型的机制
// scala集合类库中的Iterable特质并不带有显式的参数用于制作合集，而是使用隐式参数来带出一个对象用于构建目标集合。

}