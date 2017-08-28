package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter10 {
    public static void main(String[] args) {
        System.out.println("第10章　Executor框架");
    }
}
/**
 。
 10.1.2　Executor框架的结构与成员
 ·Executor是一个接口，它是Executor框架的基础，它将任务的提交与任务的执行分离开
 来。
 ·ThreadPoolExecutor是线程池的核心实现类，用来执行被提交的任务。
 ·ScheduledThreadPoolExecutor是一个实现类，可以在给定的延迟后运行命令，或者定期执
 行命令。ScheduledThreadPoolExecutor比Timer更灵活，功能更强大。
 ·Future接口和实现Future接口的FutureTask类，代表异步计算的结果。
 ·Runnable接口和Callable接口的实现类，都可以被ThreadPoolExecutor或Scheduled-
 ThreadPoolExecutor执行。

 Executors提供了一系列工厂方法用于创先线程池，返回的线程池都实现了ExecutorService接口。
 newFixedThreadPool(int),newCachedThreadPool(),newSingleThreadExecutor(),newScheduledThreadPool(int)
 ●FixedThreadPool适用于为了满足资源管理的需求，而需要限制当前线程数量的应用场
 景，它适用于负载比较重的服务器。
 实现:new ThreadPoolExecutor(nThreads, nThreads,0L, TimeUnit.MILLISECONDS,
 new LinkedBlockingQueue<Runnable>());
 核心线程数与最大线程数相同,keepAliveTime为0,多余线程会被立即终止,运行示意见:P10.2.1节
 它的队列时无界队列.其实maximumPoolSiz,keepAliveTim都是无效参数.因为是无界队列
 它的特点,稳定的线程数,可以接受无限的工作队列
 ●SingleThreadExecutor适用于需要保证顺序地执行各个任务；并且在任意时间点，不会有多
 个线程是活动的应用场景。
 实现:new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()));
 与上个唯一的区别它是单线程的
 ●CachedThreadPool是大小无界的线程池，适用于执行很多的短期异步任务的小程序，或者
 是负载较轻的服务器。
 实现:ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
 使用没有容量的SynchronousQueue作为工作队列,线程最大数量可以看做无限,空闲线程等待新任务最长时间60秒
 这意味着，如果主线程提交任务的速度高于
 maximumPool中线程处理任务的速度时，CachedThreadPool会不断创建新线程。极端情况下，
 CachedThreadPool会因为创建过多线程而耗尽CPU和内存资源。
 ●ScheduledThreadPoolExecutor通常使用工厂类Executors来创建。Executors可以创建2种类
 型的ScheduledThreadPoolExecutor，如下。
 ·ScheduledThreadPoolExecutor。包含若干个线程的ScheduledThreadPoolExecutor。
 ·SingleThreadScheduledExecutor。只包含一个线程的ScheduledThreadPoolExecutor。
 ●ScheduledThreadPoolExecutor适用于需要多个后台线程执行周期任务，同时为了满足资源
 管理的需求而需要限制后台线程的数量的应用场景。
 它主要用来在给定的延迟之后运行任务，或者定期执行任务
 Java提供的Time类可以周期性地或者延期执行任务，但是有时我们需要并行执行同样的任务，
 这个时候如果创建多个Time对象会给系统带来负担，解决办法是将定时任务放到线程池中执行。
 实现:ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,new DelayedQueue());
 DelayedQueue是一个无界队列,所以maximumPoolSize参数无效,
 ScheduledThreadPoolExecutor的执行主要分为两大部分。
 1）当调用ScheduledThreadPoolExecutor的scheduleAtFixedRate()方法或者scheduleWith-
 FixedDelay()方法时，会向ScheduledThreadPoolExecutor的DelayQueue添加一个实现了
 RunnableScheduledFutur接口的ScheduledFutureTask。
 2）线程池中的线程从DelayQueue中获取ScheduledFutureTask，然后执行任务。
 ScheduledFutureTask里包含了任务执行的具体时间,序号,执行的间隔周期等.
 ●SingleThreadScheduledExecutor适用于需要单个后台线程执行周期任务，同时需要保证顺
 序地执行各个任务的应用场景。
 10.4　FutureTask详解
 在前面的文章中我们讲述了创建线程的2种方式，一种是直接继承Thread，另外一种就是实现Runnable接口。
 这2种方式都有一个缺陷就是：在执行完任务之后无法获取执行结果。
 如果需要获取执行结果，就必须通过共享变量或者使用线程通信的方式来达到效果，这样使用起来就比较麻烦。
 而自从Java 1.5开始，就提供了Callable和Future，通过它们可以在任务执行完毕之后得到任务执行结果。
 (对于具体的Runnable或者Callable任务的执行结果进行取消、查询任务是否被取消，查询是否完成、获取结果。)
 Future接口和实现Future接口的FutureTask类，代表异步计算的结果。
 FutureTask实现了Runnable接口,因此,可以直接FutureTask.run(查了一些资料,没有这么用的)
 cancel方法用来取消任务，如果取消任务成功则返回true，如果取消任务失败则返回false。
 isCancelled方法表示任务是否被取消成功，如果在任务正常完成前被取消成功，则返回 true。
 isDone方法表示任务是否已经完成，若任务完成，则返回true；
 get()方法用来获取执行结果，这个方法会产生阻塞，会一直等到任务执行完毕才返回；
 10.4.3　FutureTask的实现
 FutureTask的实现基于AbstractQueuedSynchronizer（以下简称为AQS）。java.util.concurrent中
 的很多可阻塞类（比如ReentrantLock）都是基于AQS来实现的。AQS是一个同步框架，它提供通
 用机制来原子性管理同步状态、阻塞和唤醒线程，以及维护被阻塞线程的队列。JDK 6中AQS
 被广泛使用，基于AQS实现的同步器包括：ReentrantLock、Semaphore、ReentrantReadWriteLock、
 CountDownLatch和FutureTask。
 每一个基于AQS实现的同步器都会包含两种类型的操作，如下。
 ·至少一个acquire操作。这个操作阻塞调用线程，除非/直到AQS的状态允许这个线程继续
 执行。FutureTask的acquire操作为get()/get（long timeout，TimeUnit unit）方法调用。
 ·至少一个release操作。这个操作改变AQS的状态，改变后的状态可允许一个或多个阻塞
 线程被解除阻塞。FutureTask的release操作包括run()方法和cancel（…）方法。
 基于“复合优先于继承”的原则，FutureTask声明了一个内部私有的继承于AQS的子类
 Sync，对FutureTask所有公有方法的调用都会委托给这个内部子类。
 AQS被作为“模板方法模式”的基础类提供给FutureTask的内部子类Sync，这个内部子类只
 需要实现状态检查和状态更新的方法即可，这些方法将控制FutureTask的获取和释放操作。具
 体来说，Sync实现了AQS的tryAcquireShared（int）方法和tryReleaseShared（int）方法，Sync通过这
 两个方法来检查和更新同步状态。
 如图所示，Sync是FutureTask的内部私有类，它继承自AQS。创建FutureTask时会创建内部
 私有的成员对象Sync，FutureTask所有的的公有方法都直接委托给了内部私有的Sync。

 */