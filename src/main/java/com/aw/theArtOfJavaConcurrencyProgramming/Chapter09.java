package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter09 {
    public static void main(String[] args) {
        System.out.println("第9章　Java中的线程池");
    }
}
/**
 一个比较简单的线程池至少应包含线程池管理器、工作线程、任务队列、任务接口等部分。
 其中线程池管理器的作用是创建、销毁并管理线程池，将工作线程放入线程池中；
 工作线程是一个可以循环执行任务的线程，在没有任务时进行等待；
 任务队列的作用是提供一种缓冲机制，将没有处理的任务放在任务队列中；
 任务接口是每个任务必须实现的接口，主要用来规定任务的入口、任务执行完后的收尾工作、
    任务的执行状态等，工作线程通过该接口调度任务的执行。
 9.1　java线程池的实现原理
 线程池的处理流程如下。
 1.判断核心线程池是否已满，即已创建线程数是否小于corePoolSize？
    没满则创建一个新的工作线程来执行任务。已满则进入下个流程。
 2.判断工作队列是否已满？没满则将新提交的任务添加在工作队列，等待执行。已满则进入下个流程。
 3.判断整个线程池是否已满，即已创建线程数是否小于maximumPoolSize？
    没满则创建一个新的工作线程来执行任务，已满则交给饱和策略来处理这个任务。
 多一个核心线城市的原因是,在执行execute()方法时,尽可能避免获取全局锁,当线程数大于corePoolSize,
 几乎所有的execute()方法调用都是执行步骤2.而步骤二不需要全局锁,步骤1需要
 9.2.1　线程池的创建
 public ThreadPoolExecutor(int corePoolSize,
 int maximumPoolSize,
 long keepAliveTime,
 TimeUnit unit,
 BlockingQueue<Runnable> workQueue,
 ThreadFactory threadFactory,
 RejectedExecutionHandler handler)
 ●corePoolSize（线程池基本大小）：当向线程池提交一个任务时，若线程池已创建的线程数小于corePoolSize，
 即便此时存在空闲线程，也会通过创建一个新线程来执行该任务，
 直到已创建的线程数大于或等于corePoolSize时，才会根据是否存在空闲线程，
 来决定是否需要创建新的线程。除了利用提交新任务来创建和启动线程（按需构造），
 也可以通过 prestartCoreThread() 或 prestartAllCoreThreads() 方法来提前启动线程池中的基本线程。
 ●maximumPoolSize（线程池最大大小）：线程池所允许的最大线程个数。当队列满了，
 且已创建的线程数小于maximumPoolSize，则线程池会创建新的线程来执行任务。
 另外，对于无界队列，可忽略该参数。
 ●keepAliveTime（线程存活保持时间）：默认情况下，当线程池的线程个数多于corePoolSize时，
 线程的空闲时间超过keepAliveTime则会终止。
 ●allowCoreThreadTimeOut(boolean) 方法也可将此超时策略应用于核心线程。
 另外，也可以使用setKeepAliveTime()动态地更改参数。
 ●unit（存活保持时间的单位）：时间单位，分为7类，从细到粗顺序：NANOSECONDS（纳秒），
 MICROSECONDS（微妙），MILLISECONDS（毫秒），SECONDS（秒），MINUTES（分），
 HOURS（小时），DAYS（天）；
 ●workQueue（任务队列）：用于传输和保存等待执行任务的阻塞队列。
 可以使用此队列与线程池进行交互：
 如果运行的线程数少于 corePoolSize，则 Executor 始终首选添加新的线程，而不进行排队。
 如果运行的线程数等于或多于 corePoolSize，则 Executor 始终首选将请求加入队列，而不添加新的线程。
 如果无法将请求加入队列，则创建新的线程，除非创建此线程超出 maximumPoolSize，在这种情况下，
 任务将被拒绝。
 可以选择一下几个阻塞队列:
 ·ArrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按FIFO（先进先出）原
 则对元素进行排序。
 ·LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按FIFO排序元素，吞吐量通
 常要高于ArrayBlockingQueue。静态工厂方法Executors.newFixedThreadPool()使用了这个队列。
 ·SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用
 移除操作，否则插入操作一直处于阻塞状态，吞吐量通常要高于Linked-BlockingQueue，静态工
 厂方法Executors.newCachedThreadPool使用了这个队列。
 ·PriorityBlockingQueue：一个具有优先级的无限阻塞队列。
 ●threadFactory（线程工厂）：用于创建新线程。由同一个threadFactory创建的线程，
 属于同一个ThreadGroup，创建的线程优先级都为Thread.NORM_PRIORITY，以及是非守护进程状态。
 threadFactory创建的线程也是采用new Thread()方式，threadFactory创建的线程名都具有统一的风格：
 pool-m-thread-n（m为线程池的编号，n为线程池内的线程编号）;
 ●handler（线程饱和策略）：当线程池和队列都满了，则表明该线程池已达饱和状态。
 ，那么将采取一种什么策略处理提交的新任务,handler就是策略.
 ThreadPoolExecutor.AbortPolicy：处理程序遭到拒绝，
 则直接抛出运行时异常 RejectedExecutionException。(默认策略)
 ThreadPoolExecutor.CallerRunsPolicy：调用者所在线程来运行该任务，此策略提供简单的反馈控制机制，
 能够减缓新任务的提交速度。
 ThreadPoolExecutor.DiscardPolicy：无法执行的任务将被删除。
 ThreadPoolExecutor.DiscardOldestPolicy：如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，
 然后重新尝试执行任务（如果再次失败，则重复此过程）。
 9.2.2　向线程池提交任务
 execute() 提交任务,没有返回值
 submit() 提交任务,返回future类型对象,可以监控任务,判断任务是否成功
 9.2.3　关闭线程池
 shutdown和shutdownNow关闭线程池,原理是遍历工作线程,调用线程interrupt方法中断线程,
 所以无法响应中断的任务
 可能永远无法终止。但是它们存在一定的区别，shutdownNow首先将线程池的状态设置成
 STOP，然后尝试停止所有的正在执行或暂停任务的线程，并返回等待执行任务的列表，而
 shutdown只是将线程池的状态设置成SHUTDOWN状态，然后中断所有没有正在执行任务的线
 程。
 只要调用了这两个关闭方法中的任意一个，isShutdown方法就会返回true。当所有的任务
 都已关闭后，才表示线程池关闭成功，这时调用isTerminaed方法会返回true。至于应该调用哪
 一种方法来关闭线程池，应该由提交到线程池的任务特性决定，通常调用shutdown方法来关闭
 线程池，如果任务不一定要执行完，则可以调用shutdownNow方法。
 9.2.5　线程池的监控
 在监控线程池的时候可以使用以下属性。
 ·taskCount：线程池需要执行的任务数量。
 ·completedTaskCount：线程池在运行过程中已完成的任务数量，小于或等于taskCount。
 ·largestPoolSize：线程池里曾经创建过的最大线程数量。通过这个数据可以知道线程池是
 否曾经满过。如该数值等于线程池的最大大小，则表示线程池曾经满过。
 ·getPoolSize：线程池的线程数量。如果线程池不销毁的话，线程池里的线程不会自动销
 毁，所以这个大小只增不减。
 ·getActiveCount：获取活动的线程数
 通过扩展线程池进行监控。可以通过继承线程池来自定义线程池，重写线程池的
 beforeExecute、afterExecute和terminated方法，也可以在任务执行前、执行后和线程池关闭前执
 行一些代码来进行监控。例如，监控任务的平均执行时间、最大执行时间和最小执行时间等。
 这几个方法在线程池里是空方法。
 */