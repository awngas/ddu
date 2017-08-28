package com.aw.theArtOfJavaConcurrencyProgramming;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class Chapter04 {
    public static void main(String[] args) {
        System.out.println("Java并发编程基础");
    }
}

/**
 4.1.1　什么是线程
 现代操作系统在运行一个程序时，会为其创建一个进程。例如，启动一个Java程序，操作
 系统就会创建一个Java进程。现代操作系统调度的最小单元是线程，也叫轻量级进程（Light
 Weight Process），在一个进程里可以创建多个线程，这些线程都拥有各自的计数器、堆栈和局
 部变量等属性，并且能够访问共享的内存变量。处理器在这些线程上高速切换，让使用者感觉
 到这些线程在同时执行。
 一个Java程序从main()方法开始执行，然后按照既定的代码逻辑执行，看似没有其他线程
 参与，但实际上Java程序天生就是多线程程序，因为执行main()方法的是一个名称为main的线
 程。下面使用JMX来查看一个普通的Java程序包含哪些线程，如代码清单4-1所示。
 */
class MultiThread {
    public static void main(String[] args) {
        /* 获取Java线程管理MXBean */
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 不需要获取同步的monitor和synchronized信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        // 遍历线程信息，仅打印线程ID和线程名称信息
        for (ThreadInfo threadInfo : threadInfos) {
            System.out.println("[" + threadInfo.getThreadId() + "] " + threadInfo.
                    getThreadName());
        }
    }
}
/**
 输出如下所示（输出内容可能不同）。
 [4] Signal Dispatcher　 // 分发处理发送给JVM信号的线程
 [3] Finalizer　　　　 // 调用对象finalize方法的线程
 [2] Reference Handler // 清除Reference的线程
 [1] main　 　　　　 // main线程，用户程序入口
 可以看到，一个Java程序的运行不仅仅是main()方法的运行，而是main线程和多个其他线
 程的同时运行。
 4.1.3　线程优先级
 现代操作系统基本采用时分的形式调度运行的线程，操作系统会分出一个个时间片，线
 程会分配到若干时间片，当线程的时间片用完了就会发生线程调度，并等待着下次分配。线程
 分配到的时间片多少也就决定了线程使用处理器资源的多少，而线程优先级就是决定线程需
 要多或者少分配一些处理器资源的线程属性。
 在Java线程中，通过一个整型成员变量priority来控制优先级，优先级的范围从1~10，在线
 程构建的时候可以通过setPriority(int)方法来修改优先级，默认优先级是5，优先级高的线程分
 配时间片的数量要多于优先级低的线程。设置线程优先级时，针对频繁阻塞（休眠或者I/O操
 作）的线程需要设置较高优先级，而偏重计算（需要较多CPU时间或者偏运算）的线程则设置较
 低的优先级，确保处理器不会被独占。在不同的JVM以及操作系统上，线程规划会存在差异，
 有些操作系统甚至会忽略对线程优先级的设定，示例如代码清单4-2所示。
 4.1.4　线程的状态
 Java线程在运行的生命周期中可能处于表4-1所示的6种不同的状态，在给定的一个时刻，
 线程只能处于其中的一个状态。
 NEW  初始状态,线程被构建,但是还没有调用start()方法
 RUNNABLE 运行状态,java线程将操作系统中的就绪和运行两种状态笼统地称作"运行中"
 BLOCKED 阻塞状态,表示线程阻塞于锁
 WAITTING 等待状态,表示线程进入等待状态,进入该状态表示
 TIME_WAITTING 超时等待状态,该状态不同于WAITTING,它是可以在指定的时间自行返回的
 TERMINATED 终止状态,表示当前线程已经执行完毕
 下面我们使用jstack工具（可以选择打开终端，键入jstack或者到JDK安装目录的bin目录下
 执行命令），尝试查看示例代码运行时的线程信息，更加深入地理解线程状态，示例如代码清
 单4-3所示。
 */
class ThreadState {
    public static void main(String[] args) {
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();
        // 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
    }

    // 该线程不断地进行睡眠
    static class TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(100);
            }
        }
    }

    // 该线程在Waiting.class实例上等待
    static class Waiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 该线程在Blocked.class实例上加锁后，不会释放该锁
    static class Blocked implements Runnable {
        public void run() {
            synchronized (Blocked.class) {
                while (true) {
                    SleepUtils.second(100);
                }
            }
        }
    }
}

class SleepUtils {
    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }
}
/**
 使用jstack pid查看
 // BlockedThread-2线程阻塞在获取Blocked.class示例的锁上
 "BlockedThread-2" prio=5 tid=0x00007feacb05d000 nid=0x5d03 waiting for monitor entry [0x000000010fd58000]
 java.lang.Thread.State: BLOCKED (on object monitor)
 // BlockedThread-1线程获取到了Blocked.class的锁
 "BlockedThread-1" prio=5 tid=0x00007feacb05a000 nid=0x5b03 waiting on condition [0x000000010fc55000]
 java.lang.Thread.State: TIMED_WAITING (sleeping)
 // WaitingThread线程在Waiting实例上等待
 "WaitingThread" prio=5 tid=0x00007feacb059800 nid=0x5903 in Object.wait() [0x000000010fb52000]
 java.lang.Thread.State: WAITING (on object monitor)
 // TimeWaitingThread线程处于超时等待
 "TimeWaitingThread" prio=5 tid=0x00007feacb058800 nid=0x5703 waiting on condition [0x000000010fa4f000]
 java.lang.Thread.State: TIMED_WAITING (sleeping)
 通过示例，我们了解到Java程序运行中线程状态的具体含义。线程在自身的生命周期中，
 并不是固定地处于某个状态，而是随着代码的执行在不同的状态之间进行切换，Java线程状态
 变迁如图4-1示。
 图4-1　Java线程状态变迁
 由图4-1中可以看到，线程创建之后，调用start()方法开始运行。当线程执行wait()方法之
 后，线程进入等待状态。进入等待状态的线程需要依靠其他线程的通知才能够返回到运行状
 态，而超时等待状态相当于在等待状态的基础上增加了超时限制，也就是超时时间到达时将
 会返回到运行状态。当线程调用同步方法时，在没有获取到锁的情况下，线程将会进入到阻塞
 状态。线程在执行Runnable的run()方法之后将会进入到终止状态。

 注意　Java将操作系统中的运行和就绪两个状态合并称为运行状态。阻塞状态是线程
 阻塞在进入synchronized关键字修饰的方法或代码块（获取锁）时的状态，但是阻塞在
 java.concurrent包中Lock接口的线程状态却是等待状态，因为java.concurrent包中Lock接口对于
 阻塞的实现均使用了LockSupport类中的相关方法。

 4.1.5　Daemon线程
 Daemon线程是一种支持型线程，因为它主要被用作程序中后台调度以及支持性工作。这
 意味着，当一个Java虚拟机中不存在非Daemon线程的时候，Java虚拟机将会退出。可以通过调
 用Thread.setDaemon(true)将线程设置为Daemon线程。
 注意　Daemon属性需要在启动线程之前设置，不能在启动线程之后设置。
 Daemon线程被用作完成支持性工作，但是在Java虚拟机退出时Daemon线程中的finally块
 并不一定会执行，示例如代码清单4-5所示。
 运行Daemon程序，可以看到在终端或者命令提示符上没有任何输出。main线程（非
 Daemon线程）在启动了线程DaemonRunner之后随着main方法执行完毕而终止，而此时Java虚拟
 机中已经没有非Daemon线程，虚拟机需要退出。Java虚拟机中的所有Daemon线程都需要立即
 终止，因此DaemonRunner立即终止，但是DaemonRunner中的finally块并没有执行。
 注意　在构建Daemon线程时，不能依靠finally块中的内容来确保执行关闭或清理资源
 的逻辑。
 4.2　启动和终止线程
 在前面章节的示例中通过调用线程的start()方法进行启动，随着run()方法的执行完毕，线
 程也随之终止，大家对此一定不会陌生，下面将详细介绍线程的启动和终止。
 4.2.1　构造线程
 在运行线程之前首先要构造一个线程对象，线程对象在构造的时候需要提供线程所需要
 的属性，如线程所属的线程组、线程优先级、是否是Daemon线程等信息。代码清单4-6所示的
 代码摘自java.lang.Thread中对线程进行初始化的部分。
 private void init(ThreadGroup g, Runnable target, String name,long stackSize,AccessControlContext acc) {
     if (name == null) {
        throw new NullPointerException("name cannot be null");
     }
     // 当前线程就是该线程的父线程
     Thread parent = currentThread();
     this.group = g;
     // 将daemon、priority属性设置为父线程的对应属性
     this.daemon = parent.isDaemon();
     this.priority = parent.getPriority();
     this.name = name.toCharArray();
     this.target = target;
     setPriority(priority);
     // 将父线程的InheritableThreadLocal复制过来
     if (parent.inheritableThreadLocals != null)
         this.inheritableThreadLocals=ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
     // 分配一个线程ID
     tid = nextThreadID();
 }
 在上述过程中，一个新构造的线程对象是由其parent线程来进行空间分配的，而child线程
 继承了parent是否为Daemon、优先级和加载资源的contextClassLoader以及可继承的
 ThreadLocal，同时还会分配一个唯一的ID来标识这个child线程。至此，一个能够运行的线程对
 象就初始化好了，在堆内存中等待着运行。
 4.2.2　启动线程
 线程对象在初始化完成之后，调用start()方法就可以启动这个线程。线程start()方法的含义
 是：当前线程（即parent线程）同步告知Java虚拟机，只要线程规划器空闲，应立即启动调用
 start()方法的线程。
 注意　启动一个线程前，最好为这个线程设置线程名称，因为这样在使用jstack分析程
 序或者进行问题排查时，就会给开发人员提供一些提示，自定义的线程最好能够起个名字。
 4.2.3　理解中断
 中断可以理解为线程的一个标识位属性，它表示一个运行中的线程是否被其他线程进行
 了中断操作。中断好比其他线程对该线程打了个招呼，其他线程通过调用该线程的interrupt()
 方法对其进行中断操作。
 线程通过检查自身是否被中断来进行响应，线程通过方法isInterrupted()来进行判断是否
 被中断，也可以调用静态方法Thread.interrupted()对当前线程的中断标识位进行复位。如果该
 线程已经处于终结状态，即使该线程被中断过，在调用该线程对象的isInterrupted()时依旧会返
 回false。
 从Java的API中可以看到，许多声明抛出InterruptedException的方法（例如Thread.sleep(long
 millis)方法）这些方法在抛出InterruptedException之前，Java虚拟机会先将该线程的中断标识位
 清除，然后抛出InterruptedException，此时调用isInterrupted()方法将会返回false。
 在代码清单4-7所示的例子中，首先创建了两个线程，SleepThread和BusyThread，前者不停
 地睡眠，后者一直运行，然后对这两个线程分别进行中断操作，观察二者的中断标识位。
 */
class Interrupted {
    public static void main(String[] args) throws Exception {
        // sleepThread不停的尝试睡眠
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        // busyThread不停的运行
        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);
        sleepThread.start();
        busyThread.start();
        // 休眠5秒，让sleepThread和busyThread充分运行
        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();
        busyThread.interrupt();
        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
        // 防止sleepThread和busyThread立刻退出
        SleepUtils.second(2);
    }
    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(10);
            }
        }
    }
    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}
/**
 输出如下。
 SleepThread interrupted is false
 BusyThread interrupted is true
 从结果可以看出，抛出InterruptedException的线程SleepThread，其中断标识位被清除了，
 而一直忙碌运作的线程BusyThread，中断标识位没有被清除。
 4.2.4　过期的suspend()、resume()和stop()
 大家对于CD机肯定不会陌生，如果把它播放音乐比作一个线程的运作，那么对音乐播放
 做出的暂停、恢复和停止操作对应在线程Thread的API就是suspend()、resume()和stop()。
 不建议使用的原因主要有：以suspend()方法为例，在调用后，线程不会释放已经占有的资
 源（比如锁），而是占有着资源进入睡眠状态，这样容易引发死锁问题。同样，stop()方法在终结
 一个线程时不会保证线程的资源正常释放，通常是没有给予线程完成资源释放工作的机会，
 因此会导致程序可能工作在不确定状态下。
 4.2.5　安全地终止线程
 在4.2.3节中提到的中断状态是线程的一个标识位，而中断操作是一种简便的线程间交互
 方式，而这种交互方式最适合用来取消或停止任务。除了中断以外，还可以利用一个boolean变
 量来控制是否需要停止任务并终止该线程。
 在代码清单4-9所示的例子中，创建了一个线程CountThread，它不断地进行变量累加，而
 主线程尝试对其进行中断操作和停止操作。
 */
class Shutdown {
    public static void main(String[] args) throws Exception {
        Runner one = new Runner();
        Thread countThread = new Thread(one, "CountThread");
        countThread.start();
// 睡眠1秒，main线程对CountThread进行中断，使CountThread能够感知中断而结束
        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();
        Runner two = new Runner();
        countThread = new Thread(two, "CountThread");
        countThread.start();
// 睡眠1秒，main线程对Runner two进行取消，使CountThread能够感知on为false而结束
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }
    private static class Runner implements Runnable {
        private long i;
        private volatile boolean on = true;
        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()){
                i++;
            }
            System.out.println("Count i = " + i);
        }
        public void cancel() {
            on = false;
        }
    }
}
/**
 4.3　线程间通信
 4.3.1　volatile和synchronized关键字
 Java支持多个线程同时访问一个对象或者对象的成员变量，由于每个线程可以拥有这个
 变量的拷贝（虽然对象以及成员变量分配的内存是在共享内存中的，但是每个执行的线程还是
 可以拥有一份拷贝，这样做的目的是加速程序的执行，这是现代多核处理器的一个显著特
 性），所以程序在执行过程中，一个线程看到的变量并不一定是最新的。
 关键字volatile可以用来修饰字段（成员变量），就是告知程序任何对该变量的访问均需要
 从共享内存中获取，而对它的改变必须同步刷新回共享内存，它能保证所有线程对变量访问
 的可见性。
 举个例子，定义一个表示程序是否运行的成员变量boolean on=true，那么另一个线程可能
 对它执行关闭动作（on=false），这里涉及多个线程对变量的访问，因此需要将其定义成为
 volatile boolean on＝true，这样其他线程对它进行改变时，可以让所有线程感知到变化，因为所
 有对on变量的访问和修改都需要以共享内存为准。但是，过多地使用volatile是不必要的，因为
 它会降低程序执行的效率。
 关键字synchronized可以修饰方法或者以同步块的形式来进行使用，它主要确保多个线程
 在同一个时刻，只能有一个线程处于方法或者同步块中，它保证了线程对变量访问的可见性
 和排他性。
 在代码清单4-10所示的例子中，使用了同步块和同步方法，通过使用javap工具查看生成
 的class文件信息来分析synchronized关键字的实现细节，示例如下。
 public class Synchronized {
     public static void main(String[] args) {
        // 对Synchronized Class对象进行加锁
        synchronized (Synchronized.class) {
         }
         // 静态同步方法，对Synchronized Class对象进行加锁
         m();
     }
     public static synchronized void m() {
     }
 }
 在Synchronized.class同级目录执行javap–v Synchronized.class，部分相关输出如下所示：
 public static void main(java.lang.String[]);
 // 方法修饰符，表示：public staticflags: ACC_PUBLIC, ACC_STATIC
 Code:
 stack=2, locals=1, args_size=1
 0: ldc #1　　// class com/murdock/books/multithread/book/Synchronized
 2: dup
 3: monitorenter　　// monitorenter：监视器进入，获取锁
 4: monitorexit　　 // monitorexit：监视器退出，释放锁
 5: invokestatic　　#16 // Method m:()V
 8: return
 public static synchronized void m();
 // 方法修饰符，表示： public static synchronized
 flags: ACC_PUBLIC, ACC_STATIC, ACC_SYNCHRONIZED
 Code:
 stack=0, locals=0, args_size=0
 0: return
 上面class信息中，对于同步块的实现使用了monitorenter和monitorexit指令，而同步方法则
 是依靠方法修饰符上的ACC_SYNCHRONIZED来完成的。无论采用哪种方式，其本质是对一
 个对象的监视器（monitor）进行获取，而这个获取过程是排他的，也就是同一时刻只能有一个
 线程获取到由synchronized所保护对象的监视器。
 任意一个对象都拥有自己的监视器，当这个对象由同步块或者这个对象的同步方法调用
 时，执行方法的线程必须先获取到该对象的监视器才能进入同步块或者同步方法，而没有获
 取到监视器（执行该方法）的线程将会被阻塞在同步块和同步方法的入口处，进入BLOCKED
 状态。
 图4-2描述了对象、对象的监视器、同步队列和执行线程之间的关系。

 从图4-2中可以看到，任意线程对Object（Object由synchronized保护）的访问，首先要获得
 Object的监视器。如果获取失败，线程进入同步队列，线程状态变为BLOCKED。当访问Object
 的前驱（获得了锁的线程）释放了锁，则该释放操作唤醒阻塞在同步队列中的线程，使其重新
 尝试对监视器的获取。
 4.3.2　等待/通知机制
 notify() 通知一个在对象上等待的线程,使其从wait()方法返回,而返回的前提是该线程获取到了对象的锁
 notifyAll() 通知所有等待在该对象上的线程
 wait() 调用该方法的线程进入waitting状态,只有等待另外线程的通知或被中断才会返回,需要注意,调用wait()方法后
        会释放对象的锁.
 wait(long) 超时等待一段时间,单位毫秒,如果没有通知就超时返回
 wait(long,int) 对于超时时间更细粒度的控制,可以达到纳秒

 等待/通知机制，是指一个线程A调用了对象O的wait()方法进入等待状态，而另一个线程B
 调用了对象O的notify()或者notifyAll()方法，线程A收到通知后从对象O的wait()方法返回，进而
 执行后续操作。上述两个线程通过对象O来完成交互，而对象上的wait()和notify/notifyAll()的
 关系就如同开关信号一样，用来完成等待方和通知方之间的交互工作。
 */
class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();
    public static void main(String[] args) throws Exception {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }
    static class Wait implements Runnable {
        public void run() {
            // 加锁，拥有lock的Monitor
            synchronized (lock) {
                // 当条件不满足时，继续wait，同时释放了lock的锁
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                // 条件满足时，完成工作
                System.out.println(Thread.currentThread() + " flag is false. running@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }
    static class Notify implements Runnable {
        public void run() {
            // 加锁，拥有lock的Monitor
            synchronized (lock) {
                // 获取lock的锁，然后进行通知，通知时不会释放lock的锁，
                // 直到当前线程释放了lock后，WaitThread才能从wait方法中返回
                System.out.println(Thread.currentThread() + " hold lock. notify @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                flag = false;
                SleepUtils.second(5);
            }
            // 再次加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again. sleep@ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                SleepUtils.second(5);
            }
        }
    }
}
/**
 * 输出如下（输出内容可能不同，主要区别在时间上）。
 Thread[WaitThread,5,main] flag is true. wait @ 22:23:03
 Thread[NotifyThread,5,main] hold lock. notify @ 22:23:04
 Thread[NotifyThread,5,main] hold lock again. sleep @ 22:23:09
 Thread[WaitThread,5,main] flag is false. running @ 22:23:14

 调用wait()、notify()以
 及notifyAll()时需要注意的细节，如下。
 1）使用wait()、notify()和notifyAll()时需要先对调用对象加锁。
 2）调用wait()方法后，线程状态由RUNNING变为WAITING，并将当前线程放置到对象的
 等待队列。
 3）notify()或notifyAll()方法调用后，等待线程依旧不会从wait()返回，需要调用notify()或
 notifAll()的线程释放锁之后，等待线程才有机会从wait()返回。
 4）notify()方法将等待队列中的一个等待线程从等待队列中移到同步队列中，而notifyAll()
 方法则是将等待队列中所有的线程全部移到同步队列，被移动的线程状态由WAITING变为
 BLOCKED。
 5）从wait()方法返回的前提是获得了调用对象的锁。
 从上述细节中可以看到，等待/通知机制依托于同步机制，其目的就是确保等待线程从
 wait()方法返回时能够感知到通知线程对变量做出的修改。
 在图4-3中，WaitThread首先获取了对象的锁，然后调用对象的wait()方法，从而放弃了锁
 并进入了对象的等待队列WaitQueue中，进入等待状态。由于WaitThread释放了对象的锁，
 NotifyThread随后获取了对象的锁，并调用对象的notify()方法，将WaitThread从WaitQueue移到
 SynchronizedQueue中，此时WaitThread的状态变为阻塞状态。NotifyThread释放了锁之后，
 WaitThread再次获取到锁并从wait()方法返回继续执行。
 4.3.3　等待/通知的经典范式
 该范式分为两部分，分别针对等待方（消费者）和通知方（生产者）。
 等待方遵循如下原则。
 1）获取对象的锁。
 2）如果条件不满足，那么调用对象的wait()方法，被通知后仍要检查条件。
 3）条件满足则执行对应的逻辑。
 通知方遵循如下原则。
 1）获得对象的锁。
 2）改变条件。
 3）通知所有等待在对象上的线程。
 4.3.4　管道输入/输出流
 它主要用于线程之间的数据传输，而传输的媒介为内存。
 管道输入/输出流主要包括了如下4种具体实现：PipedOutputStream、PipedInputStream、
 PipedReader和PipedWriter，前两种面向字节，而后两种面向字符。
 4.3.5　Thread.join()的使用
 如果一个线程A执行了thread.join()语句，其含义是：当前线程A等待thread线程终止之后才
 从thread.join()返回。线程Thread除了提供join()方法之外，还提供了join(long millis)和join(long
 millis,int nanos)两个具备超时特性的方法。这两个超时方法表示，如果线程thread在给定的超时
 时间里没有终止，那么将会从该超时方法中返回。
 4.3.6　ThreadLocal的使用
 ThreadLocal，即线程变量，是一个以ThreadLocal对象为键、任意对象为值的存储结构。这
 个结构被附带在线程上，也就是说一个线程可以根据一个ThreadLocal对象查询到绑定在这个
 线程上的一个值。
 4.4　线程应用实例
 4.4.1　等待超时模式
 4.4.2　一个简单的数据库连接池示例
 4.4.3　线程池技术及其示例
 */
interface ThreadPool<Job extends Runnable> {
    // 执行一个Job，这个Job需要实现Runnable
    void execute(Job job);
    // 关闭线程池
    void shutdown();
    // 增加工作者线程
    void addWorkers(int num);
    // 减少工作者线程
    void removeWorker(int num);
    // 得到正在等待执行的任务数量
    int getJobSize();
}

class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    // 线程池最大限制数
    private static final int MAX_WORKER_NUMBERS = 10;
    // 线程池默认的数量
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    // 线程池最小的数量
    private static final int MIN_WORKER_NUMBERS = 1;
    // 这是一个工作列表，将会向里面插入工作
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    // 工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    // 工作者线程的数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    // 线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWokers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWokers(workerNum);
    }

    public void execute(Job job) {
        if (job != null) {
            // 添加一个工作，然后进行通知
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    public void addWorkers(int num) {
        synchronized (jobs) {
            // 限制新增的Worker数量不能超过最大值
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWokers(num);
            this.workerNum += num;
        }
    }

    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond workNum");
            }
            // 按照给定的数量停止Worker
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    public int getJobSize() {
        return jobs.size();
    }

    // 初始化线程工作者
    private void initializeWokers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    // 工作者，负责消费任务
    class Worker implements Runnable {
        // 是否工作
        private volatile boolean running = true;

        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    // 如果工作者列表是空的，那么就wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException ex) {
                            // 感知到外部对WorkerThread的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出一个Job
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception ex) {
                        // 忽略Job执行中的Exception
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
/**
 从线程池的实现可以看到，当客户端调用execute(Job)方法时，会不断地向任务列表jobs中
 添加Job，而每个工作者线程会不断地从jobs上取出一个Job进行执行，当jobs为空时，工作者线
 程进入等待状态。
 添加一个Job后，对工作队列jobs调用了其notify()方法，而不是notifyAll()方法，因为能够
 确定有工作者线程被唤醒，这时使用notify()方法将会比notifyAll()方法获得更小的开销（避免
 将等待队列中的线程全部移动到阻塞队列中）。
 可以看到，线程池的本质就是使用了一个线程安全的工作队列连接工作者线程和客户端
 线程，客户端线程将任务放入工作队列后便返回，而工作者线程则不断地从工作队列上取出
 工作并执行。当工作队列为空时，所有的工作者线程均等待在工作队列上，当有客户端提交了
 一个任务之后会通知任意一个工作者线程，随着大量的任务被提交，更多的工作者线程会被
 唤醒。
 4.4.4　一个基于线程池技术的简单Web服务器
 */
class SimpleHttpServer {
    // 处理HttpRequest的线程池
    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<HttpRequestHandler>(1);
    // SimpleHttpServer的根路径
    static String basePath;
    static ServerSocket serverSocket;
    // 服务监听端口
    static int port = 8080;
    public static void setPort(int port) {
        if (port > 0) {
            SimpleHttpServer.port = port;
        }
    }
    public static void setBasePath(String basePath) {
        if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory()) {
            SimpleHttpServer.basePath = basePath;
        }
    }
    // 启动SimpleHttpServer
    public static void start() throws Exception {
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while ((socket = serverSocket.accept()) != null) {
            // 接收一个客户端Socket，生成一个HttpRequestHandler，放入线程池执行
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }
    static class HttpRequestHandler implements Runnable {
        private Socket socket;
        public HttpRequestHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            String line = null;
            BufferedReader br = null;
            BufferedReader reader = null;
            PrintWriter out = null;
            InputStream in = null;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = reader.readLine();
                // 由相对路径计算出绝对路径
                String filePath = basePath + header.split(" ")[1];
                out = new PrintWriter(socket.getOutputStream());
                // 如果请求资源的后缀为jpg或者ico，则读取资源并输出
                if (filePath.endsWith("jpg") || filePath.endsWith("ico")) {
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while ((i = in.read()) != -1) {
                        baos.write(i);
                    }
                    byte[] array = baos.toByteArray();
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: " + array.length);
                    out.println("");
                    socket.getOutputStream().write(array, 0, array.length);
                } else {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("");
                    while ((line = br.readLine()) != null) {
                        out.println(line);
                    }
                }
                out.flush();
            } catch (Exception ex) {
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
            } finally {
                close(br, in, reader, out, socket);
            }
        }
    }
    // 关闭流或者Socket
    private static void close(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
/**
 该Web服务器处理用户请求的时序图如，图44所示。
 在图4-4中，SimpleHttpServer在建立了与客户端的连接之后，并不会处理客户端的请求，
 而是将其包装成HttpRequestHandler并交由线程池处理。在线程池中的Worker处理客户端请求
 的同时，SimpleHttpServer能够继续完成后续客户端连接的建立，不会阻塞后续客户端的请求。
 接下来，通过一个测试对比来认识线程池技术带来服务器吞吐量的提高。我们准备了一
 个简单的HTML页面，内容如代码清单4-22所示。
 <html>
 <head>
 <title>测试页面</title>
 </head>
 <body >
 <h1>第一张图片</h1>
 <img src="1.jpg" />
 <h1>第二张图片</h1>
 <img src="2.jpg" />
 <h1>第三张图片</h1>
 <img src="3.jpg" />
 </body>
 </html>
 将SimpleHttpServer的根目录设定到该HTML页面所在目录，并启动SimpleHttpServer，通
 过Apache HTTP server benchmarking tool（版本2.3）来测试不同线程数下，SimpleHttpServer的吞
 吐量表现。
 */