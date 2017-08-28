package com.aw.scalaForTheImpatient

object Chapter20 {
   def main(args: Array[String]) = {
     println("actor")
   }
// actor库从2.11版本开始,已经被弃用
// actor库从2.11版本开始,已经被弃用
// actor提供了并发程序中与传统的基于锁的结构不同的另一种选择,通过尽可能避免锁和共享状态,
// actor使得我们能够更容易设计出正确,没有死锁或争用状况的程序.scala类库提供了一个actor模型简单实现,更高阶实现还有akka
// 要点:
// 每个actor都要扩展actor类并提供act方法
// 要往actor发送消息,可以用actor ! message
// 消息发送是异步的:发完就忘
// 要接收消息,actor可以调用receive或react,通常是在循环中这样做
// receive/react的参数是由case语句组成的代码块(从技术讲这是一个偏函数)
// 不同actor之间不应该共享状态,总是使用消息来发送数据.
// 不要直接调用actor的方法,通过消息进行通信
// 避免同步消息---将发送消息和等待响应分开
// 不同actor可以通过react而不是receive来共享线程,前提是消息处理器的控制流转足够简单
// 让actor挂掉是OK的,前提是你有其他actor监控着actor的生死,用链接来设置监控关系
// 20.1 创建和启动Actor
// Actor有一个抽象方法act,该方法与Runnable接口的run方法很相似
// 启动actor调用start方法,act方法是并行运行的
// import scala.actors.Actor
// class HiActor extends Actor {
//   def act() {
//     while (true) {
//       receive {
//         case "Hi" => println("Hello")
//       }
//     }
//   }
// }
// 20.2 发送消息
// actor1 ! "Hi"
// 20.3 接收消息
// 发送到actor的消息被存放在一个"邮箱"中,receive方法从邮箱获取下一条消息.receive调用时没有消息会阻塞
// 消息的投递是异步的,他们不一定会以发送顺序到达.
// 20.4 向其他Actor发送消息
// 20.5 消息通道
// 20.6 同步消息和Future
// 20.7 共享线程
// 20.8 Actor的生命周期
// 20.9 将多个Actor链接在一起
// 20.10 Actor的设计
}