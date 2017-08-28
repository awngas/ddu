package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter11 {
    public static void main(String[] args) {
        System.out.println("第11章　Java并发编程实践");
    }
}
/**
 常用命令:
 top => 1 查看每个CPU的性能数据
 top => H 查看每个线程的性能信息
 保存线程dump
 sudo -u admin jstack 线程Pid > /1.dump
 dump中的线程ID是十六进制的
 printf "%x\n" 31558 十进制转16进制
 cat /proc/net/dev 查看网络流量
 cat /proc/loadavg 查看系统平均负载
 cat /proc/meminfo 查看系统内存情况
 cat /proc/stat 查看cpu的利用率
 */
