package com.aw.theArtOfJavaConcurrencyProgramming;

/**
 * Created by andrew.wang on 2017/8/21.
 */
public class Chapter07 {
    public static void main(String[] args) {
        System.out.println("��7�¡�Java�е�13��ԭ�Ӳ�����");
    }
}
/**
 AtomicBoolean��ԭ�Ӹ��²������͡�
 AtomicInteger��ԭ�Ӹ������͡�
 AtomicLong��ԭ�Ӹ��³����͡�

 AtomicInteger���÷���
 addAndGet(int) ԭ�ӷ�ʽ������ֵ��value��Ա���,���ؽ��
 compareAndSet(int,int) ������ֵ����Ԥ��ֵ,����ԭ�ӷ�ʽ����ֵ����Ϊ�����ֵ
 getAndIncrement() ԭ�ӷ�ʽ����ǰֵ��1,��������ǰ��ֵ
 lazySet(int) ���ջὫvalue���ó�����ֵ,ʹ��lazySet����ֵ�󣬿��ܵ�������
 �߳���֮���һС��ʱ���ڻ��ǿ��Զ����ɵ�ֵ
 getAndSet(int) ��ԭ�ӷ�ʽ����Ϊ����ֵ,�����ؾ�ֵ

 �ڲ�ԭ�Ӳ�������Unsafeʵ��
 �����ǰ��ֵ��expected����ԭ�Ӳ���,��Java�������³�x
 compareAndSwapObject,compareAndSwapInt,compareAndSwapLong
 7.2��ԭ�Ӹ�������
 ��AtomicIntegerArray��ԭ�Ӹ��������������Ԫ�ء�
 ��AtomicLongArray��ԭ�Ӹ��³������������Ԫ�ء�
 ��AtomicReferenceArray��ԭ�Ӹ������������������Ԫ�ء�
 ��AtomicIntegerArray����Ҫ���ṩԭ�ӵķ�ʽ��������������ͣ��䳣�÷������¡�
 7.3��ԭ�Ӹ�����������
 ��AtomicReference��ԭ�Ӹ����������͡�
 ��AtomicReferenceFieldUpdater��ԭ�Ӹ���������������ֶΡ�
 ��AtomicMarkableReference��ԭ�Ӹ��´��б��λ���������͡�����ԭ�Ӹ���һ��������
 �͵ı��λ���������͡����췽����AtomicMarkableReference��V initialRef��boolean
 initialMark����
 7.4��ԭ�Ӹ����ֶ���
 ��AtomicIntegerFieldUpdater��ԭ�Ӹ������͵��ֶεĸ�������
 ��AtomicLongFieldUpdater��ԭ�Ӹ��³������ֶεĸ�������
 ��AtomicStampedReference��ԭ�Ӹ��´��а汾�ŵ��������͡����ཫ����ֵ�����ù�����
 ����������ԭ�ӵĸ������ݺ����ݵİ汾�ţ����Խ��ʹ��CAS����ԭ�Ӹ���ʱ���ܳ��ֵ�
 ABA���⡣
 */
