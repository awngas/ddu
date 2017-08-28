package com.aw.scalaForTheImpatient

object Chapter08 {
   def main(args: Array[String]) = {
     println("继承")
   }
   val a = new Ant
   println(a.env.length)



// 8.1 扩展类
// final、extends关键字和java相同
// 8.2 重写方法
// 重写方法必须用override修饰
// 调用超类的方法用super
// 8.3 类型检查和转换
// 测试某个对象是否属于某个给定的类及其子类，可以用isInstanceOf方法。
// 对象.isInstanceOf[类]，如果对象是null则返回false
// 测试某个对象属于某个类（不包含子类）：p.getClass==classOf(类)
// scala和java中的类型检查和转换
// scala                  #Java
// obj.isInstanceOf[C1]   #obj instanceof C1
// obj.asInstanceOf[C1]   #(C1) obj
// classOf[C1]            #C1.class
// scala有模式匹配更加适合类型检查和转换
// p.math{
//   case s:Employee => .. //将s作为Employee处理
//   case _ => //p不是Employee
// }
// 8.4 受保护字段和方法
// 可将字段或方法声明为protected，该成员可以被任何子类访问，但不能再其他位置看到
// 与java不同，protected成员对于类所属的包而言，是不可见的。（想要可见可以用包修饰符，见第7章）
// 8.5 超类的构造
// 只有主构造器可以调用超类的主构造器
// class SubClass1(n:String,a:Int,val s:Double) extends Class1(n,a)
// 该段代码定义了一个类和一个调用超类构造器的主构造器
// 在scala中，在构造器中不能super(参数)调用超类构造器
// scala类可以扩展java类，但是主构造器必须调用java超类的某一个构造方法
   class Class1(x:Int,y:Int,w:Int) extends java.awt.Rectangle(x,y,w,w)
// 8.6 重写字段
// scala字段是由一个私有字段和getter/setter方法构成的。
// 你可以用另一个同名的val字段重写一个val或不带参数def。子类会有一个私有字段和公有getter方法，而这个getter方法重写了超类的getter方法
// 限制：1.def只能重写另一个def 2.val只能重写另一个val或不带参数def 3.var只能重写另一个抽象的var
// 表：重写val、def、var
// 重写谁        #用val                                  # 用def       # 用var
// 重写val  #子类有一个私有字段(与超类字段名字相同)      # 错误                   # var可以重写getter/setter对，只重写getter会报错
//          #getter方法重写超类getter方法                             # 和java一样      # 
// 重写def  #子类有一个私有字段，getter方法重写超类方法                                    #
// 重写var  #错误                                                                                   # 错误                  # 仅当超类的var是抽象的才可以
// 8.7 匿名子类
// 与java相同，你可以通过包含带有定义或重写的代码块的方式创建一个匿名的子类
   class Class2{
      val v1 = new Class1(1,2,3){
         def gret = "hehehe"
      }
   }
// 技术上讲，这将会创造出一个结构类型的对象（18章），该类型标记为Person{def get:String}
// 你可以用这个类型作为参数类型的定义：def meet(p:Person{def greeting:String}){ ... }
// 8.8 抽象类 8.9 抽象字段
// 抽象类使用abstract关键字修饰类，方法不需要用abstract修饰，不写方法体就是抽象方法
// 子类重写超类抽象方法/字段，不需要使用override关键字
// 除了抽象方法外，类可以拥有抽象字段（没有初始值的字段）
   abstract class Class3{
     def getId:Int //抽象方法
     val cd:Int //抽象字段，带有一个抽象的getter方法
     var name:String //抽象字段，带有抽象getter和setter方法
   }
// 子类实现必须提供具体的字段
   class Class4 extends Class3{
     val cd = 1; var name = "hh"; def getId = 1
   }
// 你可以随时用匿名类型定义抽象字段 val fr = new Class3{ ... }
// 8.10 构造顺序和提前定义
// 当你在子类中重写val并且在超类的构造器中使用该值得话，会产生不确定因素。比如：   
   class Creature{
     val range:Int =0
     val env:Array[Int] = new Array[Int](range)
   }
   class Ant extends Creature{ override val range = 2}
   
// 你想得到env为2的数组但这里数组大小为0
// 主要的原因是调用父类调用子类重写的range的getter方法时，子类的range还没有初始化（值为0）,所以数组为0，具体见：P98
// 解决办法：
// 1.将val声明为final，（子类中），安全不灵活
// 2.将val声明为lazy，（超类中，见第2章），安全不高效
// 3.在子类中使用提前定义语法,（提前定义的等号右侧只能引用已有的提前定义，不能使用类中其他字段或方法）
   class Ant2 extends { override val range=2 } with Creature
// 8.11 scala继承层级
// Any 超级父类     Any子类AnyVal和AnyRef
// AnyVal:所有值类型的父类Boolean,Byte,Char,Double,Float,Int,Long,Short,Unit
// AnyRef:所有其他类都是AnyRef的子类，它与Java的Object类是同义词。
// AnyRef类追加了wait，notify/notifyAll,和等同于java的synchronized块的synchronized方法
// 所有scala类都实现ScalaObject特质
// Any实现Nothing特质，Nothing是所有类型的子类型,它没有实例。常用于泛型结构
// AnyRef实现Null特质，Null是所有引用类型的子类型，Null类型的唯一实例是null
// 在Scala中，void由Unit类型表示，该类型只有一个值：()。虽然Unit不是任何其他类型的超类型，但是，编译器依然允许任何值被替换成()
// 8.12 对象相等性
// 在Scala中，AnyRef的eq方法检测两个引用是否指向同一个对象。


}