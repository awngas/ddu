package com.aw.scalaForTheImpatient

object Chapter10 {
   def main(args: Array[String]) = {
     println("特质")
   }

// 特质可以由抽象的字段和方法，也可以提供具体的字段和方法
// 未实现方法默认是抽象的，特质重写特质抽象方法不需要override关键字 
// 使用extends(一个) 加 with(多个)添加额外的特质，类可以实现多个特质
// 10.4 带有特质的对象
// 在构造单个对象时，你可以为它添加特质
// val ob1 = new SaveClass1 with ConsoleLoger
// 10.5 叠在一起的特质
   trait Logged{ def log(msg:String){println(msg)} }
   trait TimeLogger extends Logged{ //打印时间特质
     override def log(msg:String){ super.log(new java.util.Date()+msg)}
   }
   trait ShortLogger extends Logged{ //截图过长log的特质
     override def log(msg:String){
       super.log( if(msg.length <= 4) msg else msg.substring(0,4)+".." )
     }
   }

   class Account{ val id = Account.nln() }
   object Account{ private def nln() = 1 } // 伴生对象
   class SavingsAccout extends Account with Logged{
     def wc = log("123456")
   }
// 特质中的super.log与类不同，super.log调用的是特质层级中的下一个特质，根据特质添加顺序决定
// 一般来说，从最后一个特质开始处理
   val act1 = new SavingsAccout with TimeLogger with ShortLogger
   val act2 = new SavingsAccout with ShortLogger with TimeLogger
   act1.wc //打印：Sat May 27 15:42:27 CST 20171234..
   act2.wc //打印：Sat ..
// 10.6 在特质中重写抽象方法
// 在上例中，如果Logged.log是抽象的，那么编译将会报错，要加入abstract和override关键字
   trait Logged2{def log(msg:String) }
   trait TimeLogger2 extends Logged2{
     abstract override def log(msg:String){ 
       super.log(msg)
     }
   }
// 10.7 当做富接口使用的特质
// 普遍的做法，在特质中添加多个不同的具体方法，它们调用一个抽象方法，抽象方法由混入该特质的类实现。这样可以给抽象方法添加更多的特性，比如日志分级。例子：P124
// 10.8 特质中的具体字段
// 特质可以由具体字段，使用该特质的类都会获得一个字段与之对应，这些字段不是被继承的，他们只是被简单被加到了子类当中（特质中的具体字段可以理解为就是直接在类中定义的字段，而不是从超类中继承的）
// 10.9 特质中的抽象字段
// 1.特质中的具体方法可以使用抽象字段 2.子类必须重写抽象字段 
// 10.10 特质构造顺序
// 特质也有构造器，构造顺序如下
// 1.先调用超类的构造器
// 2.特质构造器在超类构造器之后、类构造器之前执行
// 3.特质由左到右被构造
// 4.每个特质当中，父特质先被构造
// 5.如果多个特质共有一个父特质，而父特质已经构造完，则不会再次构造
// 6.所有特质构造完毕，子类被构造
// 10.11 初始化特质中的字段
// 如果特质在构造器中使用了子类需要实现的抽象字段
   trait Class1{val n:String;println(n)}
   class Class2 extends Class1{ val n = "test1" }
   val test1 = new Class2 //会打印null
// val test2 = new Class2 with Class1{ val n = "test2" } 这样也不行，这种是建立了继承Class2的匿名子类
// 解决办法，1.使用 提前定义 特性，提前定义发生在常规的构造系列之前
   val test3 = new { val n="test3"} with Class1 //new 之后是提前定义块
   class Class4 extends { val n="test4"} with Class1 {/*Class4类的实现*/} //extends后是提前定义块
// 2.使用懒值 lazy,但是懒值每次使用前都会检查是否已经初始化，不是很高效
// 10.12 扩展类的特质
// 特质也可以扩展类，这个类将会自动成为所有混入该特质的超类
// 如果类已经扩展了另一个类，那么这个类必须是特质的超类的子类。否则不能混入这个特质
// trait LoggedException extends Exception with Logged { ... }
// 10.13 自身类型
// 当特质使用this:类型 => 开始定义时，那么它只能被混入这个类型的子类
   trait LoggedException extends Logged{
     this:Exception => def log(){log(getMessage())}
   }
// 该特质并不是扩展Exception类,而是有一个自身类型Exception.它只能被混入Exception的子类.
// 在特质的方法中,我们可以调用该类自身类型的任何方法
// 自身类型可以解决特质间的循环依赖(两个互相依赖的特质),自身类型也同样可以处理结构类型(structural type)
// 结构类型只给出类必须拥有的方法,而不是类的名称.
   trait LoggedException2 extends Logged{
     this:{ def getMessage():String} =>
       def log() { log(getMessage())}
   }
// 这个特质可以被混入任何拥有getMessage方法的类
// 10.14 背后发生了什么
// 只有抽象方法的特质被简单地变成一个java接口
// 特质有具体的方法,scala会帮我们创建出一个伴生类,该伴生类用静态方法存放特质的方法
// 这些伴生类不会有任何字段,特质中的字段对应到接口中的抽象的getter和setter方法,当某个类实现该特质时,字段被自动加入.伴生类中有一个初始化方法,会调用setter方法初始化.
// 当特质被混入类的时候,类将会得到一个带有getter和setter的对应字段,那个类的构造器会调用初始化方法.
// 如果特质扩展自某个超类,则伴生类并不继承这个超类,该超类会被任何实现该特质的类继承.
}