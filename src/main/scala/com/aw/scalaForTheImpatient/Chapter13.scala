package com.aw.scalaForTheImpatient

object Chapter13 {
   def main(args: Array[String]) = {
     println("集合")
     
     
   /*
   * 13.1
   * 编写一个函数,给定字符串, 产出一个包含所有字符的下标的映射. 
   * 举例来说,indexes("Mississippi")应返回一个映射, 让'M'对应集{0},
   * 'i'对应集{1,4,7,10}, 以此类推
   * 使用字符到可变集的映射, 另外, 你如何保证集是经过排序的?
   */
    def question1(str: String): Map[Char, Iterable[Int]] =
    (collection.mutable.Map[Char, Iterable[Int]]() /: (str zip (0 until str.size))) {
      (map, kv) => map += kv._1 -> (map.getOrElse(kv._1, Iterable[Int]()) ++ Iterable(kv._2))
    }.toMap
   }
// 13.1 主要的集合特质
// Iterable 子-> Seq 子-> IndexedSeq //先后次序的值得序列，数组或链表
// Iterable 子-> Set 子-> SortedSet //没有先后次序的值
// Iterable 子-> Map 子-> SortedMap
// Iterable指的是那些能生成用来访问集合中所有元素的Iterator的集合 P166
// 每个scala集合特质或类都有一个带有apply方法的伴生对象
// 13.2 可变和不可变集合
// 被引入的scala包和Predef对象有指向不可变特质的类型的别名List,Set,Map
// 所以创建的集合默认都是不可变的集合，使用可变集合要显式import
// 13.3 序列
// 不可变： Seq 子(IndexedSeq子(Vector,Range),List,Stream,Stack,Queue)
// 可变：Seq 子(IndexedSeq子(ArrayBuffer),Stack,Queue,PriorityQueue,LinkedList,DoubleLinkedList)
// 13.4 List
// scala中，List要么是Nil(空表)，要么是一个head元素加上一个tail,tail又是一个List
   val li = List(4,2) //li.head=4,li.tail=List(2),li.tail.head=2,li.tail.tail=Nil
// 我们可以用递归，迭代器，模式匹配遍历List
   def sum(lst:List[Int]):Int = if(lst==Nil) 0 else lst.head+sum(lst.tail) //递归
   def sum2(lst:List[Int]):Int = lst match{case Nil =>0;case h :: t => h+sum(t)} //模式匹配：h=lst.head,t=lst.tail
// 13.5 可变List
// 有LinkedList和DoubleLinkedList两种,在scala 2.11已经被废弃
// 使用，迭代（所有负值改0，去除每两个元素中的一个），修改某节点为最后一个节点 P13.6
// 13.6 Set
   scala.collection.mutable.LinkedHashSet("A","B") //可以记住元素被插入的顺序
   scala.collection.immutable.SortedSet(1,2,3) //已排序的Set,红黑树实现
   scala.collection.mutable.SortedSet(1,3)
// 还有BitSet类
// 常用方法contains,subsetOf,union(|),intersect(&),diff(&~),也可将联合union写作++将差异diff写作--
   Set(1,2) -- Set(4,5) //结果：Set(1, 2)
// 13.7 用于添加或去除元素的操作符
// P173
// 汇总：1.向后(:+)或向前(+:)追加元素 2.添加(+)元素到无次序集合 3.用-移除元素 4.++，--批量添加和移除 5.对于List优先使用::和:::
// 6.改值操作+=、++=、-=、--=
// 13.8 常用方法
// Iterable特质，Seq特质常用方法P176
// 13.9 将函数映射到集合
// map方法可以将某个函数应用到集合中的每个元素并产出其结果的集合
   List("a").map(_.toUpperCase) //List("A") 与 for(n <- list) yield n.toUpperCase效果相同
// 若函数产出一个集合而不是单个值得话，若果想把所有的值串接在一起，可以用flatMap
   List("a","b").map( s => Vector(s.toUpperCase,s.toLowerCase)) //List(Vector("A","a"),Vector("B","b"))
   List("a","b").flatMap( s => Vector(s.toUpperCase,s.toLowerCase)) //List("A","a","B","b")
// 如果你使用flatMap并传入返回Option的函数的话，最终返回的集合将包含所有的值v，前提是函数返回Some(v)
// collect方法用于偏函数(partial function)--那些并没有对所有可能的输入值进行定义的函数，它产出被定义的所有参数的函数值得集合
   "-3+4".collect { case '+' => 1;case '-' => -1} //Vector(-1,1)
// 如果你应用函数到各个元素仅仅是为了遍历它不关心函数返回值的话可以用foreach
   List("a").foreach(println)
// 13.10 化简、折叠和扫描
// map方法将一元函数应用到集合所有元素，还有方法可以将二元函数来组合集合中的元素，如c.reduceLeft(op)
   List(1,7,2,9).reduceLeft(_ - _) //((1-7)-2)-9 = -17 图示见：P178
   List(1,7,2,9).reduceRight(_ - _) //1-(7-(2-9))=-13 //从集合尾部开始
   List(1,7,2,9).foldLeft(0)(_ - _) // 有一个初始的元素，将得到 0-1-7-2-9=-19
   (0 /: List(1,7,2,9))(_ - _) //也可以这么写foldLeft操作
// 对应的还有foldRight和:\
// 折叠有时可以作为循环替代，实例：计算某个字符串中字母出现的频率
   val freq=scala.collection.mutable.Map[Char,Int]()
   for(c <- "Mississippi") freq(c) = freq.getOrElse(c, 0)+1 //freq为Map(i->4,M->1,s->4,p->2)
// 换个思路，在每一步，将频率Map和新遇到的字母结合在一起，产生一个新的频率Map,这就是折叠
   (Map[Char,Int]() /: "Mississippi") {
     (m,c) => m+(c -> (m.getOrElse(c, 0)+1))
   } //这是个不可变Map，每一步都计算出一个新的Map
// 任何while循环都可以用折叠来替代，构建一个把循环中被更新的所有变量结合在一起的数据结构，然后定义一个操作，实现循环中的一步。
// scanLeft和scanRight方法将折叠和映射操作结合在一起，你得到的是包含所有中间结果的集合
   (1 to 10).scanLeft(0)(_ + _) // Vector(0,1,3,6,10,15,21,28,36,45,55)
// 13.11 拉链操作
// 拉链(zip)操作，指的是将两个集合相互对应的元素结合在一起
   List(5.0,20.0) zip List(10,2) //结果List((5.0,10), (20.0,2))
// 若两个集合数量不同，取最短集合元素数量
   List(5.0,20.0,9.95) zip List(10,2) //结果List((5.0,10), (20.0,2))
// zipAll方法可以指定较短List的缺省值
   List(5.0,20.0,9.95).zipAll(List(10,2),0.0,1) //结果List((5.0,10),(20.0,2),(9.95,1))
// zipWinthIndex返回对偶的列表，其中每个对偶中第二个组成部分是每个元素的下标
   "Scala".zipWithIndex //结果Vector((S,0),(c,1),(a,2),(l,3),(a,4))
   "Scala".zipWithIndex.max._2 //取得最大编码值得下标，结果：3，最大编码值是l
// 13.12 迭代器
// 使用iterator方法从集合获得一个迭代器。Iterable中有一些方法可以产出迭代器，比如grouped或sliding
// 使用1：while(iter.hasNext) 对iter.next()执行操作
// 使用2：for(elem <- iter) 对elem执行某种操作
// 上述两种勋魂都会将迭代器移动到集合的末尾，在此之后它就不能再被使用了。
// Iterator类定义了一些与集合方法使用起来完全相同的方法，见13.8节中列表。
// 在调用了诸如map,filter,count,sum,length放方法，迭代器将位于集合的尾端，你不能再继续使用它，其他方法如find或take,迭代器位于已找到元素或已取得元素之后。
// 如果你感觉操作迭代器很麻烦，可以用诸如toArray、toIterable、toSeq、toSet、toMap来将相应的值拷贝到一个新的集合中
// 13.13 stream 流
// 流是一个尾部被懒计算的不可变列表，只有当你需要时它才会被计算
   def numsFrom(n:BigInt):Stream[BigInt] = n #:: numsFrom(n+1) //#::构建一个流
   val tenOrMore = numsFrom(10) //结果：Stream(10,?) 尾部是未被求值的
   tenOrMore.tail.tail.tail //结果：Stream(13,?)
// 流的方法是懒执行的，例如
   val squares = numsFrom(1).map(x => x*x) //结果：Stream(1,?)，你需要调用squares.tail强制对下一个元素求值
   squares.take(5).force //结果：Stream(1,4,9,16,25)，可以使用take+force求出后续的n个值
// 可以从迭代器构造一个流，比如Source.getLines返回一个迭代器，对于每一行只能访问一次，而流将缓存访问过的行，允许你重新访问它们
// val words=scala.io.Source.fromFile("文件").getLines.toStream
// words //Stream(A,?) words(5) //Aachen words //Stream(A,A's,AOL,AOL's,Aachen,?)
// 13.14 懒视图
// 流方法是懒执行的，仅当结果被需要时才计算，你可以使用view方法来得到类似效果，该方法产出一个其方法总是被懒执行的集合
   import scala.math._
   val powers = (0 until 1000).view.map(pow(10,_)) //产出一个未被求值的集合，连第一个元素都未被求值
   powers(100) //pow(10,100)被计算。
// 与流不同，视图不缓存任何值，每次调用都会重新计算。可以用force方法强制求值，返回新的集合
// 懒集合适合处理需要多种方式进行变换的大型集合，它可以避免构建大型中间集合
   (0 to 1000).map(pow(10,_)).map(1 / _) //产生了两个集合
   (0 to 1000).view.map(pow(10,_)).map(1 / _).force //记住了两个map操作的视图，求值时，对于每个元素，两个操作被同时执行，不需要中间集合
// 13.15 于java集合的互操作
// JavaConversions对象提供了用于在scala和java集合之间来回转换的一组方法
   import scala.collection.JavaConversions._
   val props:scala.collection.mutable.Map[String,String] = System.getProperties()
// P187 表13-4：scala到java集合的转换
// P187 表13-5：java到scala集合的转换
// 注意，这些转换产出的是包装器，让你可以使用目标接口来访问原本的值，举例来说，props就是一个包装器
// 其方法会调用底层的java对象，如果你调用
   props("com.a.b") = "scala"
// 那么包装器将调用底层Properties对象的put("com.a.b","scala")
// 13.16 线程安全的集合
// scala提供六个特质，让集合的操作变成同步的,scala 2.11版本已经不推荐使用，请使用java.util.concurrent包中的类
// SynchronizedBuffer SynchronizedMap  SynchronizedPriorityQueue 
// SynchronizedQueue  SynchronizedSet  SynchronizedStack
   val scores=new scala.collection.mutable.HashMap[String,Int] with scala.collection.mutable.SynchronizedMap[String,Int]
// 13.17 并行集合
// 使用par方法会产出当前集合的一个并行实现，该实现会并行的执行集合方法
   List(1,2,3,4).par.count(_ % 2 ==0) //并行计算表达式
   for(i<-(0 until 100).par) print(i + " ") //print 打印的数字不是0到100顺序打印的
   for(i<-(0 until 100).par) yield i+" "   //在for/yield循环中，结果是依次组装的，顺序的0到100的
// par方法返回的并行集合类型扩展自ParSeq、ParSet或ParMap特质，所有特质都是ParIterable自类型，而不是Iterable子类型
// 因此不能讲并行集合传递给预期Iterable、Seq、Set或Map的方法。可以用ser方法将并行集合转换回串行的版本，
// 也可以实现接受通用的GenIterable、GenSeq、GenSet或GenMap类型的参数的方法.最后并不是所有方法都可以并行化，比如reduceLeft，详见P190
}