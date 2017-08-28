package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter01 {
    public static void main(String[] args) {
        System.out.println("并发编程的挑战");
    }
    /**
     * 1.1 上下文切换
     * CPU通过给每个线程分配CPU时间片来实现多线程执行代码,CPU通过时间片分配算法来循环执行任务，
     * 当前任务执行一个时间片后会切换到下一个任务，在切换前会保存上一个任务的状态,
     * 所以任务从保存到再加载的过程就是一次上下文切换。
     * 1.1.4 减少上下文切换实战
     * 通过减少线上大量waitting线程来减少上下文切换
     * 第一步:用jstack命令dump线程信息,看看进程的线程都在干嘛
     * jstack 进程pid > dump.log  //生成程序的线程dump信息
     * 第二步:统计线程都处于什么状态
     * grep java.lang.Thread.State dump.log | awk '{print $2$3$4$5}' | sort | uniq -c
     * 返回:
     * 39 RUNNABLE
     * 21 TIMED_WAITING(onobjectmonitor)
     * 6 TIMED_WAITING(parking)
     * 51 TIMED_WAITING(sleeping)
     * 305 WAITING(onobjectmonitor)
     * 3 WAITING(parking)
     * 第三步:打开dump文件查看处于WAITING（onobjectmonitor）的线程在做什么.
     * 发现这些线程基本全是JBOSS的工作线程，在await。说明JBOSS线程池里线程接收到的任务太少，大量线程都闲着。
     * 第四步:减少JBOSS的工作线程线程数,找到JBOSS的线程池配置信息，将maxThreads降到100。
     *
     * waitting到runnable都会进行一次上下文的切换.
     * 1.2 死锁
     * dump线程可以查看哪个线程出现问题
     * java.lang.Thread.State: BLOCKED (on object monitor)
     * 避免死锁的几个常见方法:
     * 避免一个线程同时获取多个锁.
     * 避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源
     * 尝试使用定时锁，使用lock.tryLock（timeout）来替代使用内部锁机制
     * 对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的情况
     * 1.3 资源限制的挑战
     * 资源限制是指在进行并发编程时，程序的执行速度受限于计算机硬件资源或软件资源
     * 对于硬件资源限制，可以考虑使用集群并行执行程序
     * 对于软件资源限制，可以考虑使用资源池将资源复用
     */
}
