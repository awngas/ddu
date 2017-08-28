package com.aw.theArtOfJavaConcurrencyProgramming;

import java.util.concurrent.*;

public class Chapter08 {
    public static void main(String[] args) {
        System.out.println("��8�¡�Java�еĲ���������");
    }
}
/**
 8.1���ȴ����߳���ɵ�CountDownLatch
 CountDownLatch����һ�������̵߳ȴ������߳���ɲ���,����ʵ��join�Ĺ���
 CountDownLatch ���캯������һ��int���Ͳ�����������,ʹ��countDownʱ,������-1,
 await�����ڻ�������ǰ�߳�ֱ��������Ϊ0.
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
 8.2��ͬ������CyclicBarrier
 CyclicBarrier��������˼�ǿ�ѭ��ʹ�ã�Cyclic�������ϣ�Barrier������Ҫ���������ǣ���һ
 ���̵߳���һ�����ϣ�Ҳ���Խ�ͬ���㣩ʱ��������ֱ�����һ���̵߳�������ʱ�����ϲŻ�
 ���ţ����б��������ص��̲߳Ż�������С�
 8.2.1��CyclicBarrier���
 CyclicBarrier(int) ������ʾ�������ص��߳�����
 ���̵߳���await֪ͨ�Ե�������,Ȼ���̻߳ᱻ����
 ���߼����캯��CyclicBarrier(int,Runnable) ���̴߳ﵽ������,����ִ�в�����Runnable

 CyclicBarrier��CountDownLatch��ͬ����,���ļ���������ʹ��reset()��������,
 8.3�����Ʋ����߳�����Semaphore(�ź���)
 Semaphore��һ�ֻ��ڼ������ź������������趨һ����ֵ�����ڴˣ�����߳̾�����ȡ����źţ������Լ��������黹��
 ������ֵ���߳���������źŽ��ᱻ������Semaphore������������һЩ����أ���Դ��֮��ģ�
 �������ݿ����ӳأ�����Ҳ���Դ�������Ϊ1��Semaphore��������Ϊһ�����ƻ������Ļ��ƣ�
 ��Ҳ�ж�Ԫ�ź�������ʾ���ֻ���״̬��(�ź���������ͬʱ���е��߳���)
 */
 class SemaphoreTest {
    private static final int THREAD_COUNT = 30;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    private static Semaphore s = new Semaphore(10);//���֤����

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) { //��Ȼ��30���߳�,��ֻ��10��ͬʱִ��
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.acquire(); //���һ�����֤
                        System.out.println("save data");
                        s.release(); //�黹���֤
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
/**
 8.4���̼߳佻�����ݵ�Exchanger
 Exchanger�������ߣ���һ�������̼߳�Э���Ĺ����ࡣExchanger���ڽ����̼߳�����ݽ�
 �������ṩһ��ͬ���㣬�����ͬ���㣬�����߳̿��Խ����˴˵����ݡ�
 �������߳�ͨ��
 exchange�����������ݣ������һ���߳���ִ��exchange()����������һֱ�ȴ��ڶ����߳�Ҳ
 ִ��exchange�������������̶߳�����ͬ����ʱ���������߳̾Ϳ��Խ������ݣ������߳�����
 ���������ݴ��ݸ��Է���
 */
 class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<String>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String A = "������ˮA"; // A¼��������ˮ����
                    exgr.exchange(A);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String B = "������ˮB"; // B¼��������ˮ����
                    String A = exgr.exchange("B");
                    System.out.println("A��B�����Ƿ�һ�£�" + A.equals(B) + "��A¼����ǣ�"
                            + A + "��B¼���ǣ�" + B);
                } catch (InterruptedException e) {
                }
            }
        });
        threadPool.shutdown();
    }
}