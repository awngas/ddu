package com.aw.scalaForTheImpatient

object Chapter04 {
   def main(args: Array[String]) = {
     println("映射和元组")
   }
// 4.1 构造映射
   val map1 = Map("a" -> 10,"b" -> 5,"c" -> 3) //不可变
   val map2 = scala.collection.mutable.Map("a" -> 10,"b" -> 5) //可变
   val map3 = new scala.collection.mutable.HashMap[String,Int]
   val map4 = Map(("a",10),("b",5))
// 4.2 获取映射中的值
   val value1 = map1("a")
   val value2 = map1.getOrElse("a", 0) //包含a返回对应值,否则返回0
// 4.3 更新映射中的值
   map2("a") = 5 //更新
   map2 += ("d" -> 5,"e" -> 5) //添加
   map2 -= "a" //删除
// 对一个不可变的映射做更新会返回一个新的映射
   val newMap = map1 + ("f" -> 5)
// 4.4 迭代映射
// for((k,v) <- 映射) 处理k和v
   map1.keySet
   for(v <- map1.values) println(v)
// 交换键和值的位置
// for((k,v) <- 映射) yield (v,k)
// 4.5 已排序映射
// scala.collections.immutable.SortedMap 不可变排序映射
// 截至Scala(2.9) 没有可变 树形排序映射,可以用TreeMap
// 按插入顺序访问键用 scala.collecton.mutable.LinkedHashMap
// 4.6 与java的互操作
// 使用隐式转换
   import scala.collection.JavaConversions.mapAsScalaMap
   val map5:scala.collection.mutable.Map[String,Int] = new java.util.TreeMap[String,Int]
   import scala.collection.JavaConversions.propertiesAsScalaMap
   val pro:scala.collection.Map[String,String] = System.getProperties() // Properties -> Map
  
   import scala.collection.JavaConversions.mapAsJavaMap
// val font = new java.awt.Font(Map(FAMILY->"b")) //将scala -> java
   
// 4.7 元组
// tuple 元组是不同类型的值的聚集
   (1,3.4,"a") //这是一个元组,类型Tuple3[Int,Double,java.lang.String]
   val second1 = (1,2)._2
// 通常使用模式匹配来获取元组的组元,
   val t = (1,3.4,"v"); 
   val (first,second,third) =t
// val (first,second,_) = t //如果并不是所有的部件都需要,可在不需要的部件位置使用_
// 4.8 拉链操作
// 使用zip方法把多个集合合成一个
   val sy = Array("a","b","c")
   val ct = Array(1,2,3)
   val pairs = sy.zip(ct) //Array(("a",1),("b",2),("c",3))
}