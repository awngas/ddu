package com.aw.scalaForTheImpatient

object Chapter19 {
   def main(args: Array[String]) = {
     println("解析")
   }
// 本章介绍如何使用"组合子解析器[combinator parser]"库来分析固定结构的数据.这样的数据包括以某种编程语言编写的程序,
// 或者是HTTP或JSON格式的数据
// 要点:
// 文法定义中的二选一,拼接,选项和重复在Scala组合子解析器中对应为|,~,opt和rep
// 对于RegexParsers而言,字符串字面量和正则表达式匹配的是词法单元
// 用^^来处理解析结果
// 在提供给~~的函数中使用模式匹配来将~结果拆开
// 用~>或<~来丢弃那些在匹配后不再需要的词法单元
// repsep组合子处理那些常见的用分隔符分隔开的条目
// 基于词法单元的解析器对于解析那种带有保留字和操作符的语言很有用,准备好定义你自己的词法分析器
// 解析器是消费读取器并产出解析结果:成功,失败或错误的函数
// Failure结果提供了用于错误报告的明细信息
// 你可能会想要添加failure语句到你的文法当中来改进错误提示的质量
// 凭借操作符符号,隐式转换和模式匹配,解析器组合子类库让任何能理解无上下文文法的人都可以很容易地编写解析器
// 19.1 文法 - Grammars
// 文法:指的是一组用于产出所有遵循某个特定结构的字符串的规则
// 例如:某个算术表达式由以下规则给出：
// 1、每个整数都是一个算术表达式
// 2、+ - * 是操作符
// 3、如果left和right是算术表达式，而op是操作符的话，left op right 也是算术表达式
// 4、如果expr是算术表达式，则(expr)也是算术表达式
// 根据这些规则，3+4和(3+4)*5都是算术表达式，而3+)或3^4或3+x都不是
// 文法通常以一种被称为巴科斯范式[Backus-Naur Form](缩写BNF)的表示法编写，以下是我们上个表达式语言的BNF定义。
// op ::= "+" | "-" | "*"
// expr ::= number | expr op expr | "(" expr ")"
// 这里number没有被定义，我们可以如下定义它
// digit ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
// number ::= digit | digit number
// 不过在实际的操作当中，更高效的做法实在解析开始之前就收集好数字，这个单独的步骤叫做词法分析[lexical analysis]
// 词法分析器[lexer]会丢掉空白和注释并形成词法单元[token]----标识符、数字或符号。
// 在我们的表达式语言中，词法单元为number和符号 + - * ()
// 注意op和expr不是词法单元，它们是结构化的元素，是文法的作者创造出来的，目的是产出正确的词法单元序列，
// 这样的符号被称作非终结符号[nonterminal symbols]
// 其中有个非终结符号位于层级的顶端，在我们的示例当中就是expr，这个非终结符号也被称为起始符号[start symbol]
// 要产出正确格式的字符串，你应该从起始符号开始，持续应用文法规则，直到所有的非终结符号都被替换掉，
// 只剩下词法单元，例如如下推导过程
// expr -> expr op expr -> number op expr -> number "+" expr -> number "+" number
// 表明 3+4 是一个合法的表达式
// 最常用的“拓展巴科斯范式[extended Backus-Naur form]”，或称EBNF,允许给出可选元素和重复。
// 我将使用大家熟悉的正则操作符 ? * + 来分别表示0个或1个,0个或更多、1个或更多，
// 举例，一个逗号分隔符数字列表可以用以下文法描述
// numberList ::= number ( "," numberList )? 或者 numberList ::= number ( "," number )*
// 作为另一个EBNF的示例，让我们对算术表达式的文法做一些改进，让它支持操作符优先级，以下是修改过后的文法
// expr ::= term ( ( "+" | "-" ) expr )?
// term ::= factor ( "*" factor )*
// factor ::= number | "(" expr ")"
// 19.2 组合解析器操作 - Combining Parser Operations
// 为了使用scala解析库，我们需要提供一个扩展自Parsers特质的类并定义那些由基本操作组合起来的解析操作，基本操作操作包括：
// 1、匹配一个词法单元
// 2、在两个操作之间做选择(|)
// 3、依次执行两个操作(~)
// 4、重复一个操作(rep)
// 5、可选择地执行一个操作(opt)
// 如下这个解析器可以识别算术表达式。它扩展自RegexParser,这是Parsers的一个子特质，可以用正则表达式来匹配词法单元，
// 在这里我们用正则表达式"[0-9]+".r来表示number
   import scala.util.parsing.combinator.RegexParsers
   class ExprParser extends RegexParsers {
     val number = "[0-9]+".r
     def expr: Parser[Any] = term ~ opt(("+" | "-") ~ expr)
     def term: Parser[Any] = factor ~ rep("*" ~ factor)
     def factor: Parser[Any] = number | "(" ~ expr ~ ")"
   }
// 这个解析器是直接从前一节的EBNF翻译过来的，只是简单的用~操作符来组合各个部分，并使用opt和rep来取代?和*
// 在我们的示例中，每个函数的返回类型都是Parser[Any]，这个类型并不十分有用，我们在19.3改进它
// 要运行该解析器，可以调用继承下来的parser方法，例如:
   val parser = new ExprParser
   val result = parser.parseAll(parser.expr, "3-4*5")
   if (result.successful) println(result.get)
// parseAll方法接受两个参数:要调用的解析方法---即与文法的起始符号对应的那个方法---和要解析的字符串
// 程序输出结果:((3~List())~Some((-~((4~List((*~5)))~None))))
// 解析上面输出,需要知道:
// 字符串字面量和正则表达式返回String值
// p ~ q 返回样例类的一个实例,这个样例类和对偶很相似
// opt(p)返回一个Option,要么是Some(..),要么是None
// rep(p)返回一个List
// 对expr的调用返回的结果是一个term(这段:(3~List()))加上一个可选的部分Some(..),其他分析略
// 由于term的定义为 def term = factor ~ rep("*" ~ factor)
// 它返回的结果是一个factor加上一个List,这是个空列表,因为在-的左边的子表达式中没有*
// 个人对这段3-4*5的解析:
// 对象:
// => 3-4*5
// => expr                                         //所有表达式都先看成expr
// => (  term ~ Some((- ~ expr))  )
// => (  (factor ~ List()) ~ Some((- ~ expr))  )   //-左边只有一个3,没有*所以List为空的
// => (  (3 ~ List()) ~ Some(  (- ~ expr)  )  )
// => (  (3 ~ List()) ~ Some(  (- ~ (term ~ None))  )  ) //为None是因为在表达式的右边没有找到+或者-
// => (  (3 ~ List()) ~ Some(  (- ~ ((factor ~ List((* ~ factor))) ~ None))  )  )
// => (  (3 ~ List()) ~ Some(  (- ~ ((4 ~ List((* ~ factor))) ~ None))  )  )
// => (  (3 ~ List()) ~ Some(  (- ~ ((4 ~ List((* ~ 5))) ~ None))  )  )
// 最后的结果就是((3~List())~Some((-~((4~List((*~5)))~None))))
// 19.3 解析器结果变换 Transforming Parser Results
// 与其让解析器构造出一整套由 ~ 和可选项和列表构成的复杂结构,不如将中间输出变换成有用的形式.
// 拿我们的算术表达式解析器来说,如果我们的目标是对表达式求值,那么每个函数,
// expr,term,factor都应该返回经过解析的子表达式的值.
// 让我们从以下定义开始: def factor:Parser[Any] = number | "(" - expr ~ ")"
// 我们想让他返回Int,def factor:Parser[Int] = ...
// 当接收到整数时,我们想得到该整数的值
// def factor: Parser[Int] = number ^^ { _.toInt } | ...
// 这里的^^操作符将函数{_.toInt}应用到number对应的解析结果上,(^^无特别意义,比~优先级低,比|高)
// 假定expr被改成返回Parser[Int],对"(" ~ expr ~ ")"求值的话,我们可以直接返回expr,而expr会产出Int,以下是一种实现方式
// def factor: Parser[Int] = ... | "(" ~ expr ~ ")" ^^ {
//   case _ ~ e ~ _ => e
// }
// 在本例中,^^操作符的参数为偏函数{ case _ ~ e ~ _ => e }
// ~组合子返回的是~样例类的实例而不是对偶 P290
// 类似的模式匹配产出和或差,注意opt产出一个Option:要么是None,要么是Some(..)
// def expr: Parser[Int] = term ~ opt(("+" | "-") ~ expr) ^^ {
//   case t ~ None => t
//   case t ~ Some("+" ~ e) => t + e
//   case t ~ Some("-" ~ e) => t - e
// }
// 最后,要计算因子的乘积,注意rep("*" ~ factor)产出的是一个List,其元素形式为"*" ~ f,其中f是一个Int
// 我们需要提取出每个~对偶中的第二个组元并计算它们的乘积:
// def term: Parser[Int] = factor ~ rep("*" ~ factor) ^^ {
//   case f ~ r => f * r.map(_._2).product
// }
// 全部例子:
   class ExprParser2 extends RegexParsers {
      val number = "[0-9]+".r
      def expr: Parser[Int] = term ~ opt(("+" | "-") ~ expr) ^^ {
        case t ~ None => t
        case t ~ Some("+" ~ e) => t + e
        case t ~ Some("-" ~ e) => t - e
      }
      def term: Parser[Int] = factor ~ rep("*" ~ factor) ^^ {
        case f ~ r => f * r.map(_._2).product
      }
      def factor: Parser[Int] = number ^^ { _.toInt } | "(" ~ expr ~ ")" ^^ {
       case _ ~ e ~ _ => e
      }
   }
   val parser2 = new ExprParser2
   val result2 = parser2.parseAll(parser2.expr, "3-4*5")
   if (result2.successful) println(result2.get) //输出-17
// 在本例中,我们只是简单地计算出表达式的值.要构建编译器或解释器的话,通常的目标是构建一颗解析树(oarse tree)
// 解析树是一个描述解析结果的树形结构,参加19.5
// 注意:你也可以写p?而不是opt(p),写p*而不是rep(p),例如:
// def expr: Parser[Any] = term ~ (("+" | "-") ~ expr)?
// def term: Parser[Any] = factor ~ ("*" ~ factor)*
// 但是问题是它们与~有冲突,你必须添加另一组圆括号,比如:
// def term: Parser[Any] = factor ~ (("*" ~ factor)*) ^^ { ... }
// 19.4 丢弃词法单元 Discarding Tokens
// 正如你在前一节看到的,我们在分析匹配项时,处理那些词法单元的过程很单调无趣.
// 对于解析来说,词法单元是必须的,但在匹配之后它们通常可以被丢弃掉.
// ~> 和 <~ 操作符可以用来匹配并丢弃词法单元,举例来说,"*"~>factor的结果只是factor的计算结果,而不是"*"~f的值
// 用这种表示法,我们可以将term函数简化为:
// def term = factor ~ rep("*" ~> factor) ^^ {
//   case f ~ r => f * r.product
// }
// 同样地,我们也可以丢弃掉某个表达式外围的圆括号,就像这样:
// def factor = number ^^ { _.toInt } | "(" ~> expr <~ ")"
// 在表达式"(" ~> expr <~ ")"中,我们不再需要做变换,因为它的值现在就是e,已经可以产出结果Int,
// 注意:~>和<~操作符的箭头指向被保留下来的部分
// 注意:在同一个表达式中使用多个~,~>,<~时需要特别小心,例如:"if" ~> "(" ~> expr <~ ")" ~ expr
// 这个表达式不仅丢掉")",而是整个子表达式")" ~ expr.解决办法使用括号:"if" ~> "(" ~> (expr <~ ")") ~ expr.
// 19.5 生成解析树 Generating Parse Trees
// 先前示例中的解析器只是计算数值结果,当你构建解释器或者编译器的时候,你会想要构建出一颗解析树.
// 这通常是用样例类来实现的,举例来说,如下的类可以表示一个算术表达式:
   class Expr
   case class Number(value:Int) extends Expr
   case class Operator(op:String,left:Expr,right: Expr) extends Expr
// 解析器的工作就是讲诸如3+4*5这样的输入变换成如下的样子:
// Operator("+", Number(3), Operator("*", Number(4), Number(5)))
// 在解释器中,这样的表达式可以被求值,在编译器中,它可以被用来生成代码.要生成解析树,你需要用^^操作符,给出产生树节点的函数.
// 例如:
// class ExprParser extends RegexParsers {
//   ...
//   def term: Parser[Expr] = (factor ~ opt("*" ~> term)) ^^ {
//     case a ~ None => a
//     case a ~ Some(b) => Operator("*", a, b)
//   }
//   def factor: Parser[Expr] = wholeNumber ^^ (n => Number(n.toInt)) | "(" ~> expr <~ ")"
// }
// 19.6 避免左递归 Avoiding Left Recursion
// 如果解析器函数在解析输入之前就调用自己的话,就会一直递归下去,
// 比如:下面函数本意想要解析由1组成的任意长度的序列;
// def ones:Parser[Any] = "1" ~ ones
// 这样的函数我们称之为左递归,要避免递归,可以重新表述文法.
// def ones: Parser[Any] = "1" ~ ones | "1" 或者 def ones: Parser[Any] = rep1("1")
// 这个问题现实中经常出现,比如我们的算术表达式解析器:
// def expr: Parser[Any] = term ~ opt(("+" | "-") ~ expr)
// expr的规则对于减法来说存在一个很不幸的效果,表达式的分组顺序是错的.P293
// 当输入为3-4-5时, 3被接受为term,而-4-5被作为"-"~expr,这就产生的错误的结果4,而不是-6
// 我们可以把文法颠倒过来吗?
// def expr: Parser[Any] = expr ~ opt(("+" | "-") ~ term)
// 这样虽然能得到正确的解析树,但是这行不通,这个expr函数是左递归的.
// 原来的版本消除了左递归,但代价是计算解析结果更难了,你需要收集中间结果,然后 按照正确的顺序组合起来
// 如果能用重复项的话,收集中间结果就会比较容易,因为重复项能产出收集到的值得list
// 举例来说,expr可以看做是一组由+或-组合起来的term值:
// def expr: Parser[Any] = expr ~ opt(("+" | "-") ~ term)
// 如果要对这个表达式求值,则把重复项中的每个s~t都根据s是"+"还是"-"分别替换成t或-t,然后计算列表之和
// def expr: Parser[Int] = term ~ rep(
//   ("+" | "-") ~ term ^^ {
//     case "+" ~ t => t
//     case "-" ~ t => -t
// }) ^^ { case t ~ r => t + r.sum }
// 如果重写文法太麻烦的话,可以参加19.9节中介绍的另一种方案
// 19.7 更多的组合子 More Combinators
// rep方法匹配零个或多个重复项,表19-1展示了该组合子的不同变种,其中最常用的是repsep.
// 举例来说,一个以逗号分隔的数字列表可以被定义为:
// def numberList = number ~ rep("," ~> number) 或者更精简版 def numberList = repsep(number, ",")
// 表19-2展示了其他偶尔会用得到的组合子.into组合子可以存储先前组合子的信息到变量中供之后的组合子使用.
// 例如在如下文法规则当中:
// def term: Parser[Any] = factor ~ rep("*" ~> factor)
// 可以将第一个因子(factor)存入变量:
// def term: Parser[Int] = factor into { first =>
//   rep("*" ~> factor) ^^ { first * _.product }
// }
// log组合子可以帮助我们调试文法,将解析器p替换成log(p)(str),你将在每次p被调用时得到一个日志输出.例如:
// def factor: Parser[Int] = log(number)("number") ^^ { _.toInt } | ...
// 产出类似如下这样的输出:
// trying number at scala.util.parsing.input.CharSequenceReader@76f7c5
//   number --> [1.2] parsed: 3
//
// 表19-1 用于表示重复项的组合子 见书P294
// rep(p) 和 rep1(p) 和 rep1(p,q) 和 repN(n,p) 和 repsep(p,s) 和 rep1sep(p,s) 和 chain1(p,s)
// 表19-2 其他组合子 见书P295
// p^^^v 和 p into f 或p >> f 和 p ^? f p ^? (f,error) 和 log(p)(str) 和 guard(p) 和 not(p)
// p ~! q 和 accept(descr,f) 和 success(v) 和 failure(msg) err(msg) 和 phrase(p) 和 positioned(p)
// 19.8 避免回溯 Avoiding Backtracking
// 每当二选一的p | q被解析而p失败时,解析器会用同样的输入尝试q
// 这样的回溯(backtracking)很低效,考虑带有如下规则的算术表达式解析器:
// def expr: Parser[Any] = term ~ ("+" | "-") ~ expr | term
// def term: Parser[Any] = factor ~ "*" ~ term | factor
// def factor: Parser[Any] = "(" ~ expr ~ ")" | number
// 如果表达式(3+4)*5被解析,term将匹配整个输入.接下来"+"或"-"的匹配会失败,解析器回溯到第二个选项,再一次解析term
// 通常我们可以通过重新整理文法规则来避免回溯,例如:
// def expr: Parser[Any] = term ~ opt(("+" | "-") ~ expr)
// def term: Parser[Any] = factor ~ rep("*" ~ factor)
// 你可以用~!操作符而不是~来表示我们不需要回溯
// def expr: Parser[Any] = term ~! opt(("+" | "-") ~! expr)
// def term: Parser[Any] = factor ~! rep("*" ~! factor)
// def factor: Parser[Any] = "(" ~! expr ~! ")" | number
// 当p ~! q被解析而q失败时,在该表达式外围带|的表达式中其他选项不会被尝试.
// 举例来说,如果factor找到了一个"("但接下来的expr不匹配的话,解析器不会哪怕只是尝试去匹配number
// 19.9 记忆式解析器 Packrat Parsers
// 记忆式解析器使用一个高效的解析算法,该算法会捕获到之前的解析结果,这样做有两个好处:
// 1.解析时间可以确保与输入长度成比例关系 2.解析器可接受左递归的语句
// 要在scala中使用记忆式解析,你需要:
// 1.将PackratParsers特质混入你的解析器
// 2.使用val或lazy val而不是def来定义你的每个解析函数,这很重要,因为解析器会缓存这些值,
// 且解析器有赖于它们始终是同一个这个事实.(def每次被调用会返回不同的值)
// 3.让每个解析方法返回PackratParser[T]而不是Parser[T]
// 4.使用PackratReader并提供parserAll方法(PackratParsers特质并不包含这个方法)
// 例如:
// class OnesPackratParser extends RegexParsers with PackratParsers {
//   lazy val ones: PackratParser[Any] = ones ~ "1" | "1"
//   def parseAll[T](p: Parser[T], input: String) =
//     phrase(p)(new PackratReader(new CharSequenceReader(input)))
// }
// 19.10 解析器说到底是什么 What Exactly Are Parsers?
// 从技术上讲,Parser[T]是一个带有单个参数的函数,参数类型为Reader[Elem],而返回值的类型为ParseResult[T]
// 在本节中,我们将更仔细的看一下这些类型
// 类型Elem是Parsers特质的一个抽象类型(抽象类型见18.12).
// RegexParsers特质将Elem定义为Char,而StdTokenParsers特质将Elem定义为Token(19.12介绍如何进行基于词法单元的解析)
// Reader[Elem]从某个输入源读取一个Elem值(即字符或词法单元)的序列,并跟踪它们的位置,用于报告错误.
// 当我们把读取器作为参数去调用Parser[T]时,它将返回ParseResult[T]的三个子类当中的一个的对象:Success[T],Failure或Error
// Error将终止解析器以及任何调用该解析器的代码,它可能在如下情形中发生
// 解析器p ~! q未能成功匹配q
// commit(p)失败
// 遇到了err(msg)组合子
// Failure只不过意味着某个解析器匹配失败,通常情况下它将会触发其外围带|的表达式中的其他选项.
// Success[T]最重要的是带有一个类型为T的result,它同时还带有一个名为next的Reader[Elem],
// 包含了匹配到的内容之外其他将被解析的输入.
// 考虑我们的算术表达式解析器中的如下部分:
// val number = "[0-9]+".r
// def expr = number | "(" ~ expr ~ ")"
// 我们的解析器扩展自RegexParsers,该特质有一个从Regex到Parser[String]的隐式转换.
// 正则表达式number被转换成这样一个解析器---以Reader[Char]为参数的函数
// 如果读取器中最开始的字符与正则表达式相匹配,解析器函数将返回Success[String]
// 返回对象中的result属性是已匹配的输入,而next属性则为移除了匹配项的读取器.
// 如果读取器中最开始的字符与正则表达式不匹配,解析器函数将返回Failure对象
// | 方法将两个解析器组合到一起,也就是说,如果p和q是函数,则p|q也是函数,组合在一起的函数以一个读取器作为参数,比如说r.
// 它将首先调用p(r),如果这次调用返回Success或Error,那么这就是p|q的返回值,否则,返回值就是q(r)的计算结果.
// 19.11 正则解析器 Regex Parsers
// RegexParsers特质提供了两个用于定义解析器的隐式转换:
// literal从一个字符串字面量(比如"+"),做出一个Parser[String]
// regex从一个正则表达式(比如"[0-9]".r) 做出一个Parser[String]
// 默认情况下,正则解析器会跳过空白,如果你对空白的使用不同于缺省的"""\s""".r所定义的(比如你想要跳过注释),
// 则可以用自己的定义重写whiteSpace,如果不想跳过空白,则可以用:
// override val whiteSpace = "".r
// JavaTokenParsers特质扩展自RegexParsers并给出了五个词法单元的定义,如表19.3所示,这些定义没有一个与java中的写法完全对应,
// 因此这个特质的适用范围是有限的.
// 表19-3 JavaTokenParsers中预定义的词法单元, (词法单元对应的正则表达式)
// ident,wholeNumber,decimalNumber,stringLiteral,floatingPointNumber
// 19.12 基于词法单元的解析器 Token-Based Parsers
// 基于词法单元的解析器使用Reader[Token]而不是Reader[Char].
// Token类型定义在特质scala.util.parsing.combinator.token.Tokens特质中,
// StdTokens子特质定义了四种在解析编程语言时经常会遇到的词法单元:
// Identifier 标识符
// Keyword 关键字
// NumericLit 数值字面量
// StringLit 字符串字面量
// StandardTokenParsers类提供了一个产出这些词法单元的解析器,标识符由字母,数字或_组成,但不以数字开头.
// 数值字面量是一个数字的序列,字符字面量被包括在"..."或'...'中,不带转义符.
// 被包含在/*...(/或者从//开始直到行尾的注释被当做空白处理.
// 当你扩展该解析器时,可将任何需要用到的保留字和特殊词法单元分别添加到lexical.reserved 和 lexical.delimiters集中:
// class MyLanguageParser extends StandardTokenParser {
//   lexical.reserved += ("auto", "break", "case", "char", "const", ...)
//   lexical.delimiters += ("=", "<", "<=", ">", ">=", "==", "!=", ...)
//   ...
// }
// 当解析器遇到保留字时,该保留字将成为Keyword而不是Identifier.
// 解析器根据"最大化匹配" 原则拣出定界符(delimiter),举例来说,如果输入包含<=,你将会得到单个词法单元,
// 而不是一个<加上=的序列
// ident函数解析标识符;而numericLit和stringLit解析字面量
// 举例来说,以下是使用StandardTokenParsers实现的算术表达式文法:
   import scala.util.parsing.combinator.syntactical.StandardTokenParsers
   class ExprParser3 extends StandardTokenParsers {
     lexical.delimiters += ("+", "-", "*", "(", ")")
     def expr: Parser[Any] = term ~ rep(("+" | "-") ~ term)
     def term: Parser[Any] = factor ~ rep("*" ~> factor)
     def factor: Parser[Any] = numericLit | "(" ~> expr <~ ")"
     def parseAll[T](p: Parser[T], in: String): ParseResult[T] =
       phrase(p)(new lexical.Scanner(in))
   }
// 注意你需要提供parseAll方法,这个方法在StandardTokenParsers特质中并未定义.在该方法中,你用到的是一个lexical.Scanner,
// 这是StdLexical特质提供的Reader[Token]
// 提示:如果你需要处理不同词法单元的语言,要调整词法单元解析器是很容易的.
// 扩展StdLexical并重写token方法以识别你需要的那些词法单元类型,可以查看StdLexical的源码作为指引---代码很短.
// 然后在扩展StdTokenParsers并重写lexical
// class MyParser extends StdTokenParsers {
//   val lexical = new MyLexical
//   ...
// }
// 提示:StdLexical的token方法写起来挺枯燥的,如果我们能用正则表达式来定义词法单元就更好了,扩展StdLexical时,
// 添加如下定义:
// def regex(r: Regex): Parser[String] = new Parser[String] {
//   def apply(in: Input) = r.findPrefixMatchOf( in.source.subSequence(in.offset, in.source.length)) match {
//     case Some(matched) => Success(in.source.subSequence(in.offset,
//             in.offset + matched.end).toString, in.drop(matched.end))
//     case None => Failure("string matching regex `" + r + "' expected but " + in.first + " found", in)
//   }
// }
// 19.13 错误处理 Error Handling
// 当解析器不能接受某个输入时,你会想得到准确的消息,指出错误发生的位置
// 解析器会生成一个错误提示,描述解析器在某个位置无法继续了,如果有多个失败点,最后访问到的那个将被报告.
// 在定义二选一或多选一的时候,你可能需要时刻记得有错误报告这回事,举例来说,假定你有如下规则:
// def value: Parser[Any] = numericLit | "true" | "false"
// 如果解析器未能匹配它们当中的任何一个,那么得知输入未能匹配"false"这样的错误提示就不是很有用,
// 解决方案是添加一个failure语句,显示地给出错误提示
// def value: Parser[Any] = numericLit | "true" | "false" |
//   failure("Not a valid value")
// 如果解析器失败了,parseAll方法将返回Failure结果,它的msg属性是一个错误提示,让你显示给用户,
// 而next属性是指向失败发生时还未解析的输入的Reader,你会想要显示行号和列,
// 这些值可以通过next.pos.line和next.pos.column得到
// 最后,next.first是失败发生时被处理的词法元素,如果你用的是RegexParsers特质,那么这个元素就是一个Char,对于错误报告而言,
// 并不是很有用.但对于词法单元解析器而言,next.first是一个词法单元,是值得报告的.
// 提示:如果你想要在成功解析后报告那些你检测到的错误(比如编程语言中的类型错误),
// 那么你可以用positioned组合子来将位置信息添加到解析结果当中
// 返回结果的类型必须扩展Positional特质.例如
// def vardecl = "var" ~ positioned(ident ^^ { Ident(_) }) ~ "=" ~ value
}