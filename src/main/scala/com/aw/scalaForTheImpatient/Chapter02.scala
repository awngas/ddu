package com.aw.scalaForTheImpatient

object Chapter02 {
   def main(args: Array[String]) = {
     println("控制结构和函数")
   } 
   val x = 3;val y=4
   
// 2.1 条件表达式
   val s = if(x > 0) 1 else -1
// 每个表达式都有类型，  举例来说
   if(x>0) 1 else -1
// 两个分支的类型都是Int
   if(x>0) "positive" else -1
// 两个类型是公共超类型，一个分支是java.lang.String,一个是Int,公共超类型是Any
// 缺失else的也是有值得，scala引入一个Unit类，写作()，表示“无值”的占位符，类似java中void
   if(x>0) 1 
   if(x>0) 1 else () //与上面等价
// 2.3 块表达式和赋值
// 在scala中，{}块包含一系列表达式，其结果也是一个表达式，块中最后一个表达式的值就是块的值
// 这个特性对于对某个val的初始化需要分多步完成的情况很有用。
   val dis = {val dx = x -2; val dy = 10 -x;dx + dy}
// 赋值语句的值是Unit类型的，所以不要串在一起
// x=y=1 //不能这么做，y=1的值是()
// 2.5 循环
// scala的while和do循环与java相同
// scala的for循环为for(变量 <- 表达式) 
   var isum=0
   for(i <- 0 to 3) isum +=i //0 to 3 返回一个Range(区间)，包含上限，值为6
   for(i <- 0 until 3) isum +=i //不包含上限，值为3
   for(ch <- "hello") isum +=ch //可直接遍历字符串
// scala中没有break与continue退出循环，替代方法1、使用Boolean型的控制变量。
// 2、使用嵌套函数，在函数中return 3、使用Breaks对象中的break方法 ===>P19页
// 2.6 高级for循环和for推导式
// for语句，你可以加多个变量<-表达式，
   for(i<-1 to 3;j<-1 to 3) print((10*i+j)+" ") //11 12 13 21 22 23 31 32 33
// 也可以带一个守卫(以if开头的Boolean表达式)，
   for(i<-1 to 3;j<-1 to 3 if i != j) print((10*i+j)+" ") //12 13 21 23 31 32
// 也可以使用任意多的定义，引入可以在循环中使用的变量。  
   for(i<-1 to 3;from=4-i;j<-from to 3) print((10*i+j)+" ")  //13 22 23 31 32 33
// for推导式：若for循环的循环体以yield开始，则该循环会构造出一个集合，每次迭代生成集合中的一个值，
// for推导式生成的集合与它的第一个生成器的类型是兼容的
   for(c<-"Hello";i<-0 to 1) yield (c+i).toChar //将生成"HIeflmlmop"
   for(i<-0 to 1;c<-"Hello") yield (c+i).toChar //Vector('H','e','l','l','o','I','f','m','m','p')
// 2.8 默认参数和带名参数
   def decorate(str:String,left:String="[",right:String="]")=left+str+right //带默认参数
// 可传入部分参数
   decorate("str",right="right")
// 2.9 变长参数
   def sum(args:Int*)={var result=0;for(arg<-args)result+=arg;result}
   val s2 = sum(1,4,9) //传入的是一个Seq类型的参数
   sum(1 to 5:_*) //将1to5当做参数序列处理，sum(1 to 5)是错误的
// 2.10 过程
// 如果函数体包含在花括号当中但没有前面的=号，那么返回类型就是Unit，这样函数叫做过程(procedure)
// 2.11 懒值
// 当val被声明为lazy时，它的初始化将被推迟，直到首次对它取值，例如
   lazy val words = scala.io.Source.fromFile("文件路径").mkString
// lazy,val,def的区别，val在words被定义时即被取值，lazy在words首次使用时取值，def在每一次words被使用时取值
// 2.12 异常
// 异常必须是java.lang.Throwable子类，没有“受检”异常   
// throw表达式的类型是Nothing
   if(x>=0){x}else{throw new Exception("xx")}
// 异常捕获使用模式匹配语法,更通用的异常应该放在越后面，不需要使用捕获的异常对象可用_替代变量名
// try{ xxxx }catch{
//   case _:MalformedURLException => xxx
//   case ex:IOException => ex.printStackTrace()
// }
// try/finally语句与java使用相同，也会在finally中抛出异常
   
   
}