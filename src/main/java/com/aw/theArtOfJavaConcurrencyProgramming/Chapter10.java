package com.aw.theArtOfJavaConcurrencyProgramming;

public class Chapter10 {
    public static void main(String[] args) {
        System.out.println("��10�¡�Executor���");
    }
}
/**
 ��
 10.1.2��Executor��ܵĽṹ���Ա
 ��Executor��һ���ӿڣ�����Executor��ܵĻ���������������ύ�������ִ�з��뿪
 ����
 ��ThreadPoolExecutor���̳߳صĺ���ʵ���࣬����ִ�б��ύ������
 ��ScheduledThreadPoolExecutor��һ��ʵ���࣬�����ڸ������ӳٺ�����������߶���ִ
 �����ScheduledThreadPoolExecutor��Timer�������ܸ�ǿ��
 ��Future�ӿں�ʵ��Future�ӿڵ�FutureTask�࣬�����첽����Ľ����
 ��Runnable�ӿں�Callable�ӿڵ�ʵ���࣬�����Ա�ThreadPoolExecutor��Scheduled-
 ThreadPoolExecutorִ�С�

 Executors�ṩ��һϵ�й����������ڴ����̳߳أ����ص��̳߳ض�ʵ����ExecutorService�ӿڡ�
 newFixedThreadPool(int),newCachedThreadPool(),newSingleThreadExecutor(),newScheduledThreadPool(int)
 ��FixedThreadPool������Ϊ��������Դ��������󣬶���Ҫ���Ƶ�ǰ�߳�������Ӧ�ó�
 �����������ڸ��رȽ��صķ�������
 ʵ��:new ThreadPoolExecutor(nThreads, nThreads,0L, TimeUnit.MILLISECONDS,
 new LinkedBlockingQueue<Runnable>());
 �����߳���������߳�����ͬ,keepAliveTimeΪ0,�����̻߳ᱻ������ֹ,����ʾ���:P10.2.1��
 ���Ķ���ʱ�޽����.��ʵmaximumPoolSiz,keepAliveTim������Ч����.��Ϊ���޽����
 �����ص�,�ȶ����߳���,���Խ������޵Ĺ�������
 ��SingleThreadExecutor��������Ҫ��֤˳���ִ�и������񣻲���������ʱ��㣬�����ж�
 ���߳��ǻ��Ӧ�ó�����
 ʵ��:new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()));
 ���ϸ�Ψһ���������ǵ��̵߳�
 ��CachedThreadPool�Ǵ�С�޽���̳߳أ�������ִ�кܶ�Ķ����첽�����С���򣬻���
 �Ǹ��ؽ���ķ�������
 ʵ��:ThreadPoolExecutor(0, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
 ʹ��û��������SynchronousQueue��Ϊ��������,�߳�����������Կ�������,�����̵߳ȴ��������ʱ��60��
 ����ζ�ţ�������߳��ύ������ٶȸ���
 maximumPool���̴߳���������ٶ�ʱ��CachedThreadPool�᲻�ϴ������̡߳���������£�
 CachedThreadPool����Ϊ���������̶߳��ľ�CPU���ڴ���Դ��
 ��ScheduledThreadPoolExecutorͨ��ʹ�ù�����Executors��������Executors���Դ���2����
 �͵�ScheduledThreadPoolExecutor�����¡�
 ��ScheduledThreadPoolExecutor���������ɸ��̵߳�ScheduledThreadPoolExecutor��
 ��SingleThreadScheduledExecutor��ֻ����һ���̵߳�ScheduledThreadPoolExecutor��
 ��ScheduledThreadPoolExecutor��������Ҫ�����̨�߳�ִ����������ͬʱΪ��������Դ
 ������������Ҫ���ƺ�̨�̵߳�������Ӧ�ó�����
 ����Ҫ�����ڸ������ӳ�֮���������񣬻��߶���ִ������
 Java�ṩ��Time����������Եػ�������ִ�����񣬵�����ʱ������Ҫ����ִ��ͬ��������
 ���ʱ������������Time������ϵͳ��������������취�ǽ���ʱ����ŵ��̳߳���ִ�С�
 ʵ��:ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,new DelayedQueue());
 DelayedQueue��һ���޽����,����maximumPoolSize������Ч,
 ScheduledThreadPoolExecutor��ִ����Ҫ��Ϊ���󲿷֡�
 1��������ScheduledThreadPoolExecutor��scheduleAtFixedRate()��������scheduleWith-
 FixedDelay()����ʱ������ScheduledThreadPoolExecutor��DelayQueue���һ��ʵ����
 RunnableScheduledFutur�ӿڵ�ScheduledFutureTask��
 2���̳߳��е��̴߳�DelayQueue�л�ȡScheduledFutureTask��Ȼ��ִ������
 ScheduledFutureTask�����������ִ�еľ���ʱ��,���,ִ�еļ�����ڵ�.
 ��SingleThreadScheduledExecutor��������Ҫ������̨�߳�ִ����������ͬʱ��Ҫ��֤˳
 ���ִ�и��������Ӧ�ó�����
 10.4��FutureTask���
 ��ǰ������������ǽ����˴����̵߳�2�ַ�ʽ��һ����ֱ�Ӽ̳�Thread������һ�־���ʵ��Runnable�ӿڡ�
 ��2�ַ�ʽ����һ��ȱ�ݾ��ǣ���ִ��������֮���޷���ȡִ�н����
 �����Ҫ��ȡִ�н�����ͱ���ͨ�������������ʹ���߳�ͨ�ŵķ�ʽ���ﵽЧ��������ʹ�������ͱȽ��鷳��
 ���Դ�Java 1.5��ʼ�����ṩ��Callable��Future��ͨ�����ǿ���������ִ�����֮��õ�����ִ�н����
 (���ھ����Runnable����Callable�����ִ�н������ȡ������ѯ�����Ƿ�ȡ������ѯ�Ƿ���ɡ���ȡ�����)
 Future�ӿں�ʵ��Future�ӿڵ�FutureTask�࣬�����첽����Ľ����
 FutureTaskʵ����Runnable�ӿ�,���,����ֱ��FutureTask.run(����һЩ����,û����ô�õ�)
 cancel��������ȡ���������ȡ������ɹ��򷵻�true�����ȡ������ʧ���򷵻�false��
 isCancelled������ʾ�����Ƿ�ȡ���ɹ�������������������ǰ��ȡ���ɹ����򷵻� true��
 isDone������ʾ�����Ƿ��Ѿ���ɣ���������ɣ��򷵻�true��
 get()����������ȡִ�н������������������������һֱ�ȵ�����ִ����ϲŷ��أ�
 10.4.3��FutureTask��ʵ��
 FutureTask��ʵ�ֻ���AbstractQueuedSynchronizer�����¼��ΪAQS����java.util.concurrent��
 �ĺܶ�������ࣨ����ReentrantLock�����ǻ���AQS��ʵ�ֵġ�AQS��һ��ͬ����ܣ����ṩͨ
 �û�����ԭ���Թ���ͬ��״̬�������ͻ����̣߳��Լ�ά���������̵߳Ķ��С�JDK 6��AQS
 ���㷺ʹ�ã�����AQSʵ�ֵ�ͬ����������ReentrantLock��Semaphore��ReentrantReadWriteLock��
 CountDownLatch��FutureTask��
 ÿһ������AQSʵ�ֵ�ͬ������������������͵Ĳ��������¡�
 ������һ��acquire����������������������̣߳�����/ֱ��AQS��״̬��������̼߳���
 ִ�С�FutureTask��acquire����Ϊget()/get��long timeout��TimeUnit unit���������á�
 ������һ��release��������������ı�AQS��״̬���ı���״̬������һ����������
 �̱߳����������FutureTask��release��������run()������cancel������������
 ���ڡ����������ڼ̳С���ԭ��FutureTask������һ���ڲ�˽�еļ̳���AQS������
 Sync����FutureTask���й��з����ĵ��ö���ί�и�����ڲ����ࡣ
 AQS����Ϊ��ģ�巽��ģʽ���Ļ������ṩ��FutureTask���ڲ�����Sync������ڲ�����ֻ
 ��Ҫʵ��״̬����״̬���µķ������ɣ���Щ����������FutureTask�Ļ�ȡ���ͷŲ�������
 ����˵��Syncʵ����AQS��tryAcquireShared��int��������tryReleaseShared��int��������Syncͨ����
 �������������͸���ͬ��״̬��
 ��ͼ��ʾ��Sync��FutureTask���ڲ�˽���࣬���̳���AQS������FutureTaskʱ�ᴴ���ڲ�
 ˽�еĳ�Ա����Sync��FutureTask���еĵĹ��з�����ֱ��ί�и����ڲ�˽�е�Sync��

 */