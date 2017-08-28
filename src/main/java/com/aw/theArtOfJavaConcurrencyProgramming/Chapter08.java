package com.aw.theArtOfJavaConcurrencyProgramming;

import java.util.concurrent.*;

public class Chapter08 {
    public static void main(String[] args) {
        System.out.println("第8章　Java中的并发工具类");
    }
}
/**
 8.1　等待多线程完成的CountDownLatch
 CountDownLatch允许一个或多个线程等待其他线程完成操作,可以实现join的功能
 CountDownLatch 构造函数接收一个int类型参数做计数器,使用countDown时,计数器-1,
 await方法在会阻塞当前线程直到计数器为0.
 */
class CountDownLatchTest {
    static CountDownLatch c = new CountDownLatch(2);
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
                c.countDown();
                System.out.println(2);
                c.countDown();
            }
        }).start();
        c.await();
        System.out.println("3");
    }
}
/**
 8.2　同步屏障CyclicBarrier
 CyclicBarrier的字面意思是可循环使用（Cyclic）的屏障（Barrier）。它要做的事情是，让一
 组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会
 开门，所有被屏障拦截的线程才会继续运行。
 8.2.1　CyclicBarrier简介
 CyclicBarrier(int) 参数表示屏障拦截的线程数量
 子线程调用await通知以到达屏障,然后线程会被阻塞
 更高级构造函数CyclicBarrier(int,Runnable) 在线程达到屏障是,优先执行参数二Runnable

 CyclicBarrier与CountDownLatch不同的是,它的计数器可以使用reset()方法重置,
 8.3　控制并发线程数的Semaphore(信号量)
 Semaphore是一种基于计数的信号量。它可以设定一个阈值，基于此，多个线程竞争获取许可信号，做完自己的申请后归还，
 超过阈值后，线程申请许可信号将会被阻塞。Semaphore可以用来构建一些对象池，资源池之类的，
 比如数据库连接池，我们也可以创建计数为1的Semaphore，将其作为一种类似互斥锁的机制，
 这也叫二元信号量，表示两种互斥状态。(信号量限制了同时并行的线程数)
 */
 class SemaphoreTest {
    private static final int THREAD_COUNT = 30;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    private static Semaphore s = new Semaphore(10);//许可证数量

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) { //虽然有30个线程,但只有10个同时执行
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.acquire(); //获得一个许可证
                        System.out.println("save data");
                        s.release(); //归还许可证
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
/**
 8.4　线程间交换数据的Exchanger
 Exchanger（交换者）是一个用于线程间协作的工具类。Exchanger用于进行线程间的数据交
 换。它提供一个同步点，在这个同步点，两个线程可以交换彼此的数据。
 这两个线程通过
 exchange方法交换数据，如果第一个线程先执行exchange()方法，它会一直等待第二个线程也
 执行exchange方法，当两个线程都到达同步点时，这两个线程就可以交换数据，将本线程生产
 出来的数据传递给对方。
 */
 class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<String>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String A = "银行流水A"; // A录入银行流水数据
                    exgr.exchange(A);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String B = "银行流水B"; // B录入银行流水数据
                    String A = exgr.exchange("B");
                    System.out.println("A和B数据是否一致：" + A.equals(B) + "，A录入的是："
                            + A + "，B录入是：" + B);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.shutdown();
    }
}