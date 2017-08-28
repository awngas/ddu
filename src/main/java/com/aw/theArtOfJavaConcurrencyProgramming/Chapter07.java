package com.aw.theArtOfJavaConcurrencyProgramming;

/**
 * Created by andrew.wang on 2017/8/21.
 */
public class Chapter07 {
    public static void main(String[] args) {
        System.out.println("第7章　Java中的13个原子操作类");
    }
}
/**
 AtomicBoolean：原子更新布尔类型。
 AtomicInteger：原子更新整型。
 AtomicLong：原子更新长整型。

 AtomicInteger常用方法
 addAndGet(int) 原子方式将输入值与value成员相加,返回结果
 compareAndSet(int,int) 若输入值等于预期值,则以原子方式将该值设置为输入的值
 getAndIncrement() 原子方式将当前值加1,返回自增前的值
 lazySet(int) 最终会将value设置成输入值,使用lazySet设置值后，可能导致其他
 线程在之后的一小段时间内还是可以读到旧的值
 getAndSet(int) 以原子方式设置为输入值,并返回旧值

 内部原子操作都由Unsafe实现
 如果当前数值是expected，则原子操作,将Java变量更新成x
 compareAndSwapObject,compareAndSwapInt,compareAndSwapLong
 7.2　原子更新数组
 ·AtomicIntegerArray：原子更新整型数组里的元素。
 ·AtomicLongArray：原子更新长整型数组里的元素。
 ·AtomicReferenceArray：原子更新引用类型数组里的元素。
 ·AtomicIntegerArray类主要是提供原子的方式更新数组里的整型，其常用方法如下。
 7.3　原子更新引用类型
 ·AtomicReference：原子更新引用类型。
 ·AtomicReferenceFieldUpdater：原子更新引用类型里的字段。
 ·AtomicMarkableReference：原子更新带有标记位的引用类型。可以原子更新一个布尔类
 型的标记位和引用类型。构造方法是AtomicMarkableReference（V initialRef，boolean
 initialMark）。
 7.4　原子更新字段类
 ·AtomicIntegerFieldUpdater：原子更新整型的字段的更新器。
 ·AtomicLongFieldUpdater：原子更新长整型字段的更新器。
 ·AtomicStampedReference：原子更新带有版本号的引用类型。该类将整数值与引用关联起
 来，可用于原子的更新数据和数据的版本号，可以解决使用CAS进行原子更新时可能出现的
 ABA问题。
 */
