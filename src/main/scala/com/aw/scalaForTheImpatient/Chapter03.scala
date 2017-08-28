package com.aw.scalaForTheImpatient

object Chapter03 {
   def main(args: Array[String]) = {
     println("数组相关操作")
   }
// 3.1 定长数组,Array以java数组方式实现
   val nums = new Array[Int](10)
   val nums2 = Array("A","B")
// 3.2 变长数组:数组缓冲
   import scala.collection.mutable.ArrayBuffer
   val b = ArrayBuffer[Int]()
   b += 1
// 3.3 遍历数组和数组缓冲
   for(i <- 0 until nums.length)
     println(i + ": "+nums(i))
// 3.4 数组转换
// 使用for推导式产生一个新的数组
// 3.5 常用算法
   Array(1,6,3).sum //求和
   ArrayBuffer(1,3,5).sortWith(_ > _) //排序
// 3.7 多维数组
   val matrix = Array.ofDim[Double](3,4); matrix(0)(0)=42
// 3.8 与java的互操作
// scala转java使用隐式转换
   import scala.collection.JavaConversions.bufferAsJavaList
   import scala.collection.mutable.ArrayBuffer
   val pb = new ProcessBuilder(ArrayBuffer("ls","-l"))
// java转scala
   import scala.collection.JavaConversions.asScalaBuffer
   import scala.collection.mutable.Buffer
   val cmd:Buffer[String] = pb.command()
}