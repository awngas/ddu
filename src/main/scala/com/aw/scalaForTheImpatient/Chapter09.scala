package com.aw.scalaForTheImpatient

object Chapter09 {
  def main(args: Array[String]) = {
    println("文件和正则表达式")
  }
// 9.1 读取行
   import scala.io.Source
   val source1 = Source.fromFile("file.txt","UTF-8")
   val lineIterator = source1.getLines() // 迭代器
   for( l <- lineIterator) println(l) // 逐条处理
   val lines = source1.getLines().toArray // 放到数组中
   val contents = source1.mkString // 文件全部内容放到字符串中
// 9.2 读取字符
   for(c <- source1) {} // 迭代处理单个字符
   val iter = source1.buffered //查看某个字符但又不处理掉它（类似java中PushbackInputStreamReader）
// 9.3 读取词法单元和数字
// 举例：读取以空格分隔的浮点数文件   
   val tokens = source1.mkString.split("\\S+") //快速获得空格分隔的字符串
   val numbers1 = for (w <- tokens ) yield w.toDouble //转换成double数组
   val numbers2 = tokens.map(_.toDouble) //转换成double数组
   val age = readInt() //从控制台中读取一个数字
// 9.4 从URL或其他源读取
// Source.formURL,Source.formString,Source.stdin //从URL,String,标准输入读取
// 9.5 读取二进制文件
// Scala读取二进制文件需要用Java类库
   import java.io.{File,FileInputStream}
   val file = new File("a.txt")
   val in = new FileInputStream(file)
   val bytes = new Array[Byte](file.length.toInt)
   in.read(bytes)
   in.close()
// 9.6 写入文本文件
// scala没有內建支持，可用java.io.PrintWriter
// 9.7 访问目录
// 方法1   
   import java.io.File
   def subdirs(dir:File):Iterator[File] ={
     val children = dir.listFiles.filter(_.isDirectory)
     children.toIterator ++ children.toIterator.flatMap(subdirs _)
   }
   val dir = new File("目录")
   for(d <- subdirs(dir)){} //利用这个函数遍历所有子目录
// 方法2 使用java.nio.file.Files的walkFileTree方法，该类用到了FileVisitor接口
// 在scala中，我们通常喜欢用函数对象来指定工作内容，而不是接口
   import java.nio.file._
   implicit def makeFileVisitor(f:(Path) => Unit) = new SimpleFileVisitor[Path]{
     override def visitFile(p:Path,attrs:attribute.BasicFileAttributes) = {
       f(p)
       FileVisitResult.CONTINUE
     }
   }
   val dir2 = new java.io.File("")
   Files.walkFileTree(dir2.toPath, (f:Path) => println(f)) //打印所有子目录
   // 9.8 序列化
// 声明一个类可以被序列化
   @SerialVersionUID(42L) class Person extends Serializable
// 序列化
   val fred = new Person
   import java.io._
   val out = new ObjectOutputStream(new FileOutputStream("/序列化/t.obj"))
   out.writeObject(fred); out.close()
// 反序列化
   val inObj = new ObjectInputStream(new FileInputStream("/序列化/t.obj"))
   val saveFred = inObj.readObject().asInstanceOf[Person]
// Scala集合类都是可序列化的
// 9.9 进程控制
// 通过scala.sys.process包，你可以用scala编写shell脚本。该包包含一个从字符串到ProcessBuilder对象的隐式转换
   import sys.process.{ProcessLogger => _ ,_ }
   "ls -l" ! //执行命令，结果到标准输出
   val list = "ls -l" #| "grep sc" ! //还有#>，#>>，#<等其他管道命令
// 其他见P112，文件重定向输入，设置环境变量Process等
// 9.10 正则表达式
// scala.util.matching.Regex
   val xPattern = """\s+[0-9]+\s+""".r
   xPattern.findAllIn("测试字符串") //返回匹配项迭代器。findFirtIn返回第一个匹配性，findPrefixOf检查字符串开始部分能否匹配
// 9.11 正则表达式组
   val numitemPattern = "([0-9]+) ([a-z]+)".r
   val numitemPattern(num,item) = "测试字符串" //匹配组，将对象当做“提取器”使用。
   for( numitemPattern(num,item) <- numitemPattern.findAllIn("测试字符串") ) {} //处理num和Item
// """ """ 之间包含的字符不需要转义   
}