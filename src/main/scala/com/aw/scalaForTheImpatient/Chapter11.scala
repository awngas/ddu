package com.aw.scalaForTheImpatient

object Chapter11 {
   def main(args: Array[String]) = {
     println("操作符")
   }
   
// 11.1 标识符
// 变量,函数,类等的名称统称为标识符
// 11.2 中置操作符
// a 标识符 b 为中置操作符. 1.to(10)
// 11.3 一元操作符
// a 标识符,后置操作符,例:1.toString()
// 标识符 a,前置操作符,+-!~可以作为前置操作符,编译器会转换成unary_操作符的方法调用
// -a 等同于 a.unary_-
// 11.4 赋值操作符
// a 操作符= b 等同 a = a 操作符 b;<=,>=,!=和以=开头的操作符不是赋值操作符,如果a有一个名为操作符=的方法,该方法会被直接调用
// 11.5 优先级
// 后置操作符的优先级低于中置操作符 a 中置操作符 b 后置操作符 等于(a 中置操作符 b) 后置操作符
// 11.6 结合性
// scala当中除了以:结尾的操作符和赋值操作符,所有操作符都是左结合的
// 11.7 apply和update方法
// f(参数..) 等于 f.apply(参数..)  f(参数..) = value 等于f.update(参数..,value)
// 11.8 提取器
// 提取器:带有unapply方法的对象.是apply方法的反向操作,upaply接受一个对象,然后从中提起值
// 一般用于变量定义和模式匹配 case Fraction(a,b) => .. 或val a="N B";val Name(first,last) = a
// unapply返回一个Option其内包含一个元组
// 11.9 带单个参数或无参数的提取器
// 11.10 unapplySeq方法
// 要提取任意长度的值的序列,应使用unapplySeq,它返回一个Option[Seq[A]]
}