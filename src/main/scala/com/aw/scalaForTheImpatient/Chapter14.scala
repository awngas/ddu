package com.aw.scalaForTheImpatient

object Chapter14 {
   def main(args: Array[String]) = {
     println("模式匹配和样例类")
   abstract class Item
   case class Article(description:String,price:Double) extends Item
   case class Bundle(description:String,discount:Double,items:Item*) extends Item
   val bb = Bundle("123",20.0,
       Article("456",39.95),
//       Bundle("789",10.0,Article("abc",39.95),Article("def",39.95)),
       Article("456",39.95),
       Article("456",39.95)
//       Bundle( "hig",10.0,Article("abc",39.95) )
       )
   bb match {
      case Bundle(_,_,Article(descr,_),_*) =>println(0)
      case Bundle(_,_,art @ Article(_,_),rest) => println(art + "+++++" + rest)
      case Bundle(_,_,art @ Article(_,_),rest @ _*) => println(1)
     }
     
   def price(it:Item):Double = it match{
     case Article(_,p) => p
     case Bundle(_,disc,its @ _*) => its.map(price _).sum - disc
   }
   val a  = price(bb);
   println(a);
   
   }
// 14.1 更好的switch
   val sign = 'c' match {
     case '+' => 1
     case _ => 0  //与defalut等效，没有的话，若没有模式能匹配，抛出MatchError一次
   }
// 模式匹配不会掉入下一个分支。表达式中可以使用任何类型，而不仅仅是数字
// 14.2 守卫
// 比如想匹配数所有数字，可以写成 case _ if Character.isDigit(ch) => ..
// 守卫可以是任何Boolean条件，模式总是从上向下匹配，如果带守卫的这个模式不能匹配，则捕获所有的模式(case _)会被用来尝试进行匹配
// 14.3 模式中的变量
// case _ 特殊的写法，如果case关键字后面跟着一个变量名，那么匹配的表达式会被赋值给那个变量
// 你可以在守卫中使用变量，变量必须以小写字母开头，某则会被认为常量，小写字母开头的常量要包在反引号中
   var digit = 0; 'a' match{ case ch => digit=Character.digit(ch,10) }
// 14.4 类型模式
// 你可以对表达式的类型进行匹配,我们更倾向于使用模式匹配，而不是isInstanceOf操作符
// obj match{
//   case x : Int => x //配置到的值被当做Int绑到x
//   case s : String => Integer.parseInt(s)
//   case BigInt => -1 //匹配类型时，必须给出一个变量名，否则会拿对象本身进行匹配。这里：匹配类型为Class的BigInt对象
// }
// 匹配发生在运行期，java虚拟机中泛型的类型信息是被擦掉的，所以不能匹配特定的Map类型
// case m:Map[String,Int] => //不能这样做。但是可以匹配通用的映射case n:Map[_,_] => 对于数组而言元素的类型信息是完好的，你可以匹配到ArrayList[Int]
// 14.5 匹配数组、列表和元组
// 配置数组的内容，用Array表达式
// case Array(0) //匹配包含0的数组
// case Array(x,y) //匹配任何带有两个元素的数组，并将两个元素绑定到x和y
// case Array(0,_*) //匹配任何以0开始的数组
// 也可以使用List表达式匹配List.你也可以使用::操作符
// case 0::Nil;case x::y;case 0::tail
// 对于元组，可以在模式中使用元组表示法
// case (0,_); case (y,0);
// 14.6 提取器
// 模式能匹配数组、列表和元组，是因为提取器(extractor)机制--带有从对象提取值得unapply或unapplySqe方法的对象。
// 11章已经介绍过，unapply方法用于提取固定数量的对象，而unapplySeq提取的是一个序列，可长可短。
// 例如：arr match{case Array(0,x)=>...} //调用了Array.unapplySeq(arr)产出值.并第一个值与0进行比较，第二个值被赋值给x
// 14.7 变量声明中的模式
// 模式可以带变量。在变量声明中使用这样的模式
   val (x,y) = (1,2) //同时把x定义为1，把y定义为2，对于返回对偶的函数很有用
   val (q,r) = BigInt(10) /% 3 //返回包含商和余数的对偶
// 同样的语法也可以用于任何带有变量的模式
// val Array(first,second,_*) = arr
// 14.8 for表达式中的模式
// 你可以在for推导式中使用带变量的模式。
   import scala.collection.JavaConversions.propertiesAsScalaMap
   for((k,v) <- System.getProperties()) println(k+"->"+v)
   for((k,"") <- System.getProperties()) println(k) //打印所有值为空白的键，其他忽略
   for((k,v) <- System.getProperties() if v == "") println(k+"->"+v) //也可以使用守护
// 14.9 样例类
// 是一种特殊的类，他们经过优化以被用于模式匹配。
// 样例类用case关键字修饰，如：case class Nothing1 extends Amount
// 当你声明样例类时，
// 构造器中的每一个参数都成为val-除非它被显式的声明为var（不建议这样做）
// 在伴生对象中提供apply方法
// 提供unapply方法让模式匹配可以工作--见11章
// 生成toString、equals、hashCode和copy方法，除非显式的给出这些方法的定义
// 14.10 copy方法和带名参数
// 样例类的copy发放创建一个与现有对象值相同的新对象
// 你可以使用带名参数来修改某些属性
// val price = amt.copy(unit ="kk")
// 14.11 case语句中的中置表示法
// 如果unapply方法产出一个对偶，你可以在case语句中使用中置表示法，尤其是对于有两个参数的样例类，你可以使用中置表示法表示它
// amt match {case a Currency u => ...} //等同于 case Currency(a,u)
// 这个特性本意的要匹配序列，举例来说每个List对象要么是Nil,要么是样例类::,定义如下
// case class ::[E](head:E,tail:List[E]) extends List[E] //这句话并不能编译成功，换成ArrayBuffer成功了，
// 因此。你可以这样写 lst match{ case h :: t=> ...} //等同于case ::(h,t),将调用::.unapply(lst)   
// 在19章中，会看到将解析结果组合在一起的~样例类。
// result match{case p -q -r =>..} 这样写法好于~(~(p,q),r)
// 中置表示法可用于任何返回对偶的unapply方法
// 如果操作符以冒号结尾，则它是从右向左结合的
// 14.12 匹配嵌套结构
// 样例类经常被用于嵌套结构，例如，某个商店售卖物品，有时我们会将物品捆绑在一起打折出售
   abstract class Item
   case class Article(description:String,price:Double) extends Item
   case class Bundle(description:String,discount:Double,items:Item*) extends Item
   val que = Bundle("123",20.0,Article("456",39.95),Bundle("789",10.0,Article("abc",39.95),Article("def",39.95)))
   que match {
     case Bundle(_,_,art @ Article(_,_),rest) => println(art + "+++++" + rest)
     case Bundle(_,_,art @ Article(_,_),rest @ _*) => println(1)
   }
// 模式可以匹配到特定的嵌套，比如：
// case Bundle(_,_,Article(descr,_),_*) => .. //将descr绑定到Bundle的第一个Article的描述
// 也可以用@表示法将嵌套的值绑定到变量
// case Bundle(_,_,art @ Article(_,_),rest @ _*) //art就是Bundle中的第一个Article,而rest则是剩余Item的序列
// case Bundle(_,_,art @ Article(_,_),rest) //art是Bundle中的第一个Article,而rest则是一个Item或子类，这种匹配只能匹配，第三个参数是Article类型，第四个参数只能是一个对象的情况
// 实际应用,计算某个Item价格的函数
   def price(it:Item):Double = it match{
     case Article(_,p) => p
     case Bundle(_,disc,its @ _*) => its.map(price _).sum - disc
   }
// 14.13 样例类是邪恶的吗
// 样例类适合用于那种标记不会改变的结构。scala的list就是用样例类实现的
// 当用在合适的地方，样例类更容易把我们引向更精简的代码，原因:P204
// 14.14 密封类
// 用样例类做模式匹配时，可能想让编译器帮你确保你已经列出了所有可能的选择，
// 你需要将样例类的通用超类声明为sealed
   sealed abstract class Amount2
// 密封类的所有子类必须在与该密封类相同的文件中定义
// 14.15 模拟枚举 使用sealed + 类 +模式匹配  模拟枚举类型
// 14.16 Option类型
// 标准类库中的Option类型用样例类来表示那种可能存在、也可能不存在的值。
// 这比使用空字符串的意图更加清晰，比使用null来表示缺少某值得做法更加安全
// 样例子类Some包装了某个值，而样例对象Node表示没有值，Option支持泛型
// Map类的get方法返回一个Option,如果对应键没有值，get返回None,否则为包含值得Some.
// 你可以将Option当做一个要么为空，要么带有单个元素的集合
// 例：scs.get("a").foreach(println _) //如果get返回None的话，什么也不做
// 14.17 偏函数
// 被包在花括号内的一组case语句是一个偏函数---一个并非对所有输入值都有定义的函数
// 他是PartialFunction[A,B]类的一个实例，A参数类型B返回类型。
// 该类有两个方法：apply从匹配到的模式计算函数值，isDefinedAt在输入至少匹配其中一个模式时返回true
   var f:PartialFunction[Char,Int] = {case '+' =>1;case '-' => -1} // f('-') 调用f.apply返回-1 f('0') //抛出MatchError
// 有些方法接受PartialFunction做为参数，举例来说，GenTraversable特质的collect方法将一个偏函数应用到所有在该偏函数有定义的元素，并返回包含这些结果的序列
   "-3+4".collect{ case '+' => 1;case '-' => -1}
}