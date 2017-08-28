package com.aw.scalaForTheImpatient

object Chapter22 {
   def main(args: Array[String]) = {
     println("定界延续")
   }
// 延续(Continuations)是一个强大的结构,允许你实现与人们熟知的分支,循环,函数调用和异常不同的控制流转机制.
// 22.1 捕获并执行延续 Capturing and Invoking a Continuation
// 延续可以让你回到程序中之前的一个点,(个人理解像C中的goto)
// 延续使用shift块保存, 延续是有界的,只能延展到给定的边界,由reset标出
//  import scala.continuations.ContextControl._
//  var cont: (Unit => Unit) = null //延续
//  var filename = "myfile.txt"
//  var contents = ""
//  reset {
//    while (contents == "") {
//      try {
//        contents = scala.io.Source.fromFile(filename, "UTF-8").mkString
//      } catch { case _ => }
//      shift { k: (Unit => Unit) =>          //捕获延续
//        cont = k
//      } //对cont的调用将从此处开始
//    }
//  }
// 要重试的话,只需要执行延续即可.
//  if (contents == "") {
//    print("Try another filename: ");
//    filename = readLine()
//    cont() // 调回到shift
//  }
//  println(contents)
// 22.2 "运算当中挖个洞" The “Computation with a Hole”
// 22.3 reset和shift的控制流转 The Control Flow of reset and shift
// 22.4 reset表达式的值 The Value of a reset Expression
// 如果reset块推出是因为由于执行了shift,那么得到的值就是shift块的值
// val result = reset { shift { k: (String => String) => "Exit" }; "End" } //result为Exit
// 如果reset块执行到自己的末尾的话,它的值就是reset块的值--块中最后一个表达式的值
// val result = reset { if (false) shift { k: (String => String) => "Exit" }; "End" }
// 22.5 reset和shift表达式的类型
// reset和shift都是带有类型参数的方法,分别是reset[B, C]和shift[A, B, C].
// 22.6 CPS注解
// 某些虚拟机中,延续实现方式是抓取运行期栈的块照,当调用延续时,运行期栈被恢复成快照的样子.
// java虚拟机不允许对栈进行这样操作,为实现延续,
// scala编译器将对reset块中的代码进行"延续传递风格[continuation-passing style]"(CPS)的变换.
// 变换的代码与常规scala代码不一样,而且不能混用这两种风格,如果方法包含shift,它不会被编译成常规方法,
// 必须将它注解为一个"被变换"的方法.要调用一个包含shift[A,B,C]的方法,它必须被注解为@cpsParam(B,C).
// 22.7 将递归访问转化为迭代
// 比如:递归遍历目录,但只想看前100的文件,没法中间停掉递归,使用延续的话就很简单,
// if (f.isDirectory)
//   processDirectory(f)
// else {
//    shift { //每当被指向,就会跳到reset的末尾,同时会捕获到延续
//      k: (Unit => Unit) => {
//        cont = k
//      }
//    }
//    println(f)
//  }
// 我们将过程的启动点用reset抱起来,然后就可以用想要调用的次数来调用捕获到的延续
// reset {
//  processDirectory(new File(rootDirName))
//} for (i <- 1 to 100) cont()
// 当然该方法需要一个CPS注解def processDirectory(dir : File) : Unit @cps[Unit]
// 完整的程序:P348
// 22.8 撤销控制反转
// 程序代码P351 撤销GUI或Web编程中的"控制反转"
// 22.9 CPS变换
// 22.10 转换嵌套的控制上下文
//
}