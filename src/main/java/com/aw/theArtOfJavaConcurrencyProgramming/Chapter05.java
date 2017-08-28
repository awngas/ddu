package com.aw.theArtOfJavaConcurrencyProgramming;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class Chapter05 {
    public static void main(String[] args) {
        System.out.println("java�е���");
    }
}
/**
 5.1��Lock�ӿ�
 Lock�ӿ�����ʵ��������,Ŀ�����滻synchronized�ؼ���.
 ʹ��synchronized�ؼ��ֽ�����ʽ�ػ�ȡ���������������Ļ�ȡ���ͷŹ̻��ˣ�Ҳ�����Ȼ�ȡ���ͷš�
 ������û����չ��,���磬���һ���������ְ��ֽ�������ȡ���ͷţ��Ȼ����A��Ȼ���ٻ�ȡ��B������B��ú�
 �ͷ���Aͬʱ��ȡ��C������C��ú����ͷ�Bͬʱ��ȡ��D���Դ����ơ����ֳ����£�
 synchronized�ؼ��־Ͳ���ô����ʵ���ˣ���ʹ��Lockȴ������ࡣ
 Lockʹ��ʱ��Ҫ��ʽ�ػ�ȡ���ͷ���,��ӵ��������ȡ���ͷŵĿɲ����ԡ����жϵĻ�ȡ���Լ�
 ��ʱ��ȡ���ȶ���synchronized�ؼ��������߱���ͬ�����ԡ�

 �����嵥5-1��Lock��ʹ�õķ�ʽ��
 �����嵥5-1��LockUseCase.java
 Lock lock = new ReentrantLock();
 lock.lock();
 try {
 } finally {
 lock.unlock();
 }
 ��finally�����ͷ�����Ŀ���Ǳ�֤�ڻ�ȡ����֮�������ܹ����ͷš�
 ��Ҫ����ȡ���Ĺ���д��try���У���Ϊ����ڻ�ȡ�����Զ�������ʵ�֣�ʱ�������쳣��
 �쳣�׳���ͬʱ��Ҳ�ᵼ�����޹��ͷš�
 Lock�ӿ��ṩ��synchronized�ؼ��������߱�����Ҫ�������5-1��ʾ��
 ��5-1��Lock�ӿ��ṩ��synchronized�ؼ��ֲ��߱�����Ҫ����
 --------------------------------------------------------------------
 ���Է������ػ�ȡ��:��ǰ�̳߳��Ի�ȡ��,�����һʱ��û�б������̻߳�ȡ��,��ɹ���ȡ��������
 �ܱ��жϵػ�ȡ��:��synchronized��ͬ,��ȡ�������߳��ܹ���Ӧ�ж�,����ȡ�������̱߳��ж�ʱ,�ж��쳣���ᱻ�׳�,
                    ͬʱ���ᱻ�ͷ�
 ��ʱ��ȡ��:��ָ���Ľ�ֹʱ��֮ǰ��ȡ��,�����ֹʱ�䵽���Ծ��޷���ȡ��,�򷵻�
 ---------------------------------------------------------------------
 Lock��һ���ӿڣ�������������ȡ���ͷŵĻ���������Lock��API���5-2��ʾ��
 -------------------------------------------------------------------
 lock():��ȡ��,���ø÷�����ǰ�߳̽����ȡ��,������ú�,�Ӹ÷�������
 lockInterruptibly():���жϵػ�ȡ��,��lock()�����Ĳ�֮ͬ�����ڸ÷�������Ӧ�ж�,�������Ļ�ȡ�п����жϵ�ǰ�߳�
 tryLock() ���Է������Ļ�ȡ��,���ø÷�������������,����ܹ���ȡ�򷵻�true,���򷵻�false
 tryLock(long time,TimeUnit unit):��ʱ�Ļ�ȡ��,��ǰ�߳�������3������»᷵��:
    1.��ǰ�߳��ڳ�ʱʱ���ڻ������ 2,��ǰ�߳��ڳ�ʱʱ�����ж� 3,��ʱʱ�����,����false
 unlock() �ͷ���
 Condition newCondition() ��ȡ�ȴ�֪ͨ���,������͵�ǰ������,��ǰ�߳�ֻ�л������,���ܵ��ø������wait()����,
                �����ú�,��ǰ�߳̽��ͷ���
 -------------------------------------------------------------------
 �����ȼ򵥽���һ��Lock�ӿڵ�API�������½ڻ���ϸ����ͬ����
 AbstractQueuedSynchronizer�Լ�����Lock�ӿڵ�ʵ��ReentrantLock��Lock�ӿڵ�ʵ�ֻ�������
 ͨ���ۺ���һ��ͬ����������������̷߳��ʿ��Ƶġ�
 5.2������ͬ����
 ����ͬ����AbstractQueuedSynchronizer�����¼��ͬ����������������������������ͬ����
 ���Ļ�����ܣ���ʹ����һ��int��Ա������ʾͬ��״̬��ͨ�����õ�FIFO�����������Դ��
 ȡ�̵߳��Ŷӹ����������������ߣ�Doug Lea���������ܹ���Ϊʵ�ִ󲿷�ͬ������Ļ�����
 ͬ��������Ҫʹ�÷�ʽ�Ǽ̳У�����ͨ���̳�ͬ������ʵ�����ĳ��󷽷�������ͬ��״
 ̬���ڳ��󷽷���ʵ�ֹ������ⲻ��Ҫ��ͬ��״̬���и��ģ���ʱ����Ҫʹ��ͬ�����ṩ��3
 ��������getState()��setState(int newState)��compareAndSetState(int expect,int update)�������в�
 ������Ϊ�����ܹ���֤״̬�ĸı��ǰ�ȫ�ġ������Ƽ�������Ϊ�Զ���ͬ������ľ�̬�ڲ�
 �࣬ͬ��������û��ʵ���κ�ͬ���ӿڣ��������Ƕ���������ͬ��״̬��ȡ���ͷŵķ�����
 ���Զ���ͬ�����ʹ�ã�ͬ�����ȿ���֧�ֶ�ռʽ�ػ�ȡͬ��״̬��Ҳ����֧�ֹ���ʽ�ػ�
 ȡͬ��״̬�������Ϳ��Է���ʵ�ֲ�ͬ���͵�ͬ�������ReentrantLock��
 ReentrantReadWriteLock��CountDownLatch�ȣ���
 ͬ������ʵ������Ҳ����������ͬ��������Ĺؼ���������ʵ���оۺ�ͬ����������ͬ��
 ��ʵ���������塣��������������֮��Ĺ�ϵ����������ʹ���ߵģ���������ʹ����������
 ���Ľӿڣ�����������������̲߳��з��ʣ���������ʵ��ϸ�ڣ�ͬ���������������ʵ���ߣ�
 ����������ʵ�ַ�ʽ��������ͬ��״̬�����̵߳��Ŷӡ��ȴ��뻽�ѵȵײ����������ͬ
 �����ܺõظ�����ʹ���ߺ�ʵ���������ע������
 5.2.1������ͬ�����Ľӿ���ʾ��
 ͬ����������ǻ���ģ�巽��ģʽ�ģ�Ҳ����˵��ʹ������Ҫ�̳�ͬ��������дָ����
 ���������ͬ����������Զ���ͬ�������ʵ���У�������ͬ�����ṩ��ģ�巽��������Щ
 ģ�巽���������ʹ������д�ķ�����
 ��дͬ����ָ���ķ���ʱ����Ҫʹ��ͬ�����ṩ������3�����������ʻ��޸�ͬ��״̬��
 ��getState()����ȡ��ǰͬ��״̬��
 ��setState(int newState)�����õ�ǰͬ��״̬��
 ��compareAndSetState(int expect,int update)��ʹ��CAS���õ�ǰ״̬���÷����ܹ���֤״̬
 ���õ�ԭ���ԡ�
 ͬ��������д�ķ������������5-3��ʾ��
 tryAcquire(int) ��ռʽ��ȡͬ��״̬,ʵ�ָ÷�����Ҫ��ѯ��ǰ״̬���ж�ͬ��״̬�Ƿ����Ԥ��,
                    Ȼ���ٽ���CAS����ͬ��״̬
 tryRelease(int) ��ռʽ�ͷ�ͬ��״̬,�ȴ���ȡͬ��״̬���߳̽��л����ȡͬ��״̬
 tryAcquireShared(int) ����ʽ��ȡͬ��״̬,���ش��ڵ���0��ֵ,��ʾ��ȡ�ɹ�,��֮,��ȡʧ��
 tryReleaseShared(int) ����ʽ�ͷ�ͬ��״̬
 isHeldExclusively() ��ǰͬ�����Ƿ��ڶ�ռģʽ�±��߳�ռ��,һ��÷�����ʾ�Ƿ񱻵�ǰ�̶߳�ռ
 ʵ���Զ���ͬ�����ʱ���������ͬ�����ṩ��ģ�巽������Щ�����֣�ģ�巽��������
 ���5-4��ʾ��
 acquire(int) ��ռʽ��ȡͬ��״̬,�����ǰ�̻߳�ȡͬ��״̬�ɹ�,���ɸ÷�������,����,�������ͬ�����еȴ�,
              �÷������������д��tryAcquire(int)����
 acquireInterruptibly(int) ��acquire(int)��ͬ,���Ǹ÷�����Ӧ�ж�,��ǰ�߳�δ��ȡ��ͬ��״̬������ͬ��������,
              �����ǰ�߳��ڳ�ʱʱ����û�л�ȡ��ͬ��״̬,��ô���᷵��false,�����ȡ���˷���true
 tryAcquireNanos(int,long) ��acquireInterruptibly(int) �����������˳�ʱ����,�����ǰ�߳��ڳ�ʱʱ����û�л�ȡ��ͬ��״̬,
          ��ô���᷵��false,�����ȡ���˷���true
 acquireShared(int) ����ʽ�Ļ�ȡͬ��״̬,�����ǰ�߳�δ��ȡ��ͬ��״̬,�������ͬ�����еȴ�,
              ���ռʽ��ȡ����Ҫ��������ͬһʱ�̿����ж���̻߳�ȡ��ͬ��״̬.
 acquireSharedInterruptibly(int) ��acquireShared(int)��ͬ,�÷�����Ӧ�ж�
 tryAcquireSharedNanos(int,long) ��acquireSharedInterruptibly(int)�����������˳�ʱ����
 release(int) ��ռʽ���ͷ�ͬ��״̬,�÷��������ͷ�ͬ��״̬֮��,��ͬ�������е�һ���ڵ�������̻߳���
 boolean releaseShared(int) ����ʽ���ͷ�ͬ��״̬
 Collection<Thread> getQueuedThreads()��ȡ�ȴ���ͬ�������ϵ��̼߳���
 ͬ�����ṩ��ģ�巽�������Ϸ�Ϊ3�ࣺ��ռʽ��ȡ���ͷ�ͬ��״̬������ʽ��ȡ���ͷ�
 ͬ��״̬�Ͳ�ѯͬ�������еĵȴ��߳�������Զ���ͬ�������ʹ��ͬ�����ṩ��ģ�巽��
 ��ʵ���Լ���ͬ�����塣
 ֻ��������ͬ�����Ĺ���ԭ����ܸ����������Ⲣ�����������Ĳ����������������
 ͨ��һ����ռ����ʾ���������˽�һ��ͬ�����Ĺ���ԭ��
 ����˼�壬��ռ��������ͬһʱ��ֻ����һ���̻߳�ȡ��������������ȡ�����߳�ֻ��
 ����ͬ�������еȴ���ֻ�л�ȡ�����߳��ͷ���������̵��̲߳��ܹ���ȡ����������嵥5-
 2��ʾ��
 */
class Mutex implements Lock {
    // ��̬�ڲ��࣬�Զ���ͬ����
    private static class Sync extends AbstractQueuedSynchronizer {
        // �Ƿ���ռ��״̬
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
        // ��״̬Ϊ0��ʱ���ȡ��
        public boolean tryAcquire(int acquires) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
        // �ͷ�������״̬����Ϊ0
        protected boolean tryRelease(int releases) {
            if (getState() == 0) throw new
                    IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        // ����һ��Condition��ÿ��condition��������һ��condition����
        Condition newCondition() { return new ConditionObject(); }
    }
    // ����Ҫ����������Sync�ϼ���
    private final Sync sync = new Sync();
    public void lock() { sync.acquire(1); }
    public boolean tryLock() { return sync.tryAcquire(1); }
    public void unlock() { sync.release(1); }
    public Condition newCondition() { return sync.newCondition(); }
    public boolean isLocked() { return sync.isHeldExclusively(); }
    public boolean hasQueuedThreads() { return sync.hasQueuedThreads(); }
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }
}
/**
 ����ʾ���У���ռ��Mutex��һ���Զ���ͬ�����������ͬһʱ��ֻ����һ���߳�ռ��
 ����Mutex�ж�����һ����̬�ڲ��࣬���ڲ���̳���ͬ������ʵ���˶�ռʽ��ȡ���ͷ�ͬ��
 ״̬����tryAcquire(int acquires)�����У��������CAS���óɹ���ͬ��״̬����Ϊ1����������
 ȡ��ͬ��״̬������tryRelease(int releases)������ֻ�ǽ�ͬ��״̬����Ϊ0���û�ʹ��Mutexʱ
 ������ֱ�Ӻ��ڲ�ͬ������ʵ�ִ򽻵������ǵ���Mutex�ṩ�ķ�������Mutex��ʵ���У��Ի�
 ȡ����lock()����Ϊ����ֻ��Ҫ�ڷ���ʵ���е���ͬ������ģ�巽��acquire(int args)���ɣ���
 ǰ�̵߳��ø÷�����ȡͬ��״̬ʧ�ܺ�ᱻ���뵽ͬ�������еȴ��������ʹ�󽵵���ʵ��
 һ���ɿ��Զ���ͬ��������ż���
 5.2.2������ͬ������ʵ�ַ���
 ����������ʵ�ֽǶȷ���ͬ�������������߳�ͬ���ģ���Ҫ������ͬ�����С���ռʽͬ
 ��״̬��ȡ���ͷš�����ʽͬ��״̬��ȡ���ͷ��Լ���ʱ��ȡͬ��״̬��ͬ�����ĺ�������
 �ṹ��ģ�巽����
 1.ͬ������
 ͬ���������ڲ���ͬ�����У�һ��FIFO˫����У������ͬ��״̬�Ĺ�����ǰ�̻߳�ȡ
 ͬ��״̬ʧ��ʱ��ͬ�����Ὣ��ǰ�߳��Լ��ȴ�״̬����Ϣ�����Ϊһ���ڵ㣨Node��������
 ����ͬ�����У�ͬʱ��������ǰ�̣߳���ͬ��״̬�ͷ�ʱ������׽ڵ��е��̻߳��ѣ�ʹ����
 �γ��Ի�ȡͬ��״̬��
 ͬ�������еĽڵ㣨Node�����������ȡͬ��״̬ʧ�ܵ��߳����á��ȴ�״̬�Լ�ǰ����
 ��̽ڵ㣬�ڵ�����������������Լ��������5-5��ʾ��
 ��5-5���ڵ�����������������Լ�����
 waitStatus �ȴ�״̬:��������״̬1.cancelled,ֵ1,������ͬ�������еȴ����̵߳ȴ���ʱ���߱��ж�,
            ��Ҫ��ͬ��������ȡ���ȴ�,�ڵ�����״̬������仯.
 2.signal,ֵΪ-1,�����ڵ���̴߳��ڵȴ�״̬,����ǰ�ڵ���߳�����ͷ���ͬ��״̬���߱�ȡ��,����֪ͨ�����ڵ�,
             ʹ�����ڵ���̵߳�������
 3.condition,ֵΪ-2,�ڵ��ڵȴ�������,�ڵ��̵߳ȴ���Condition��,�������̶߳�Condition������signal()������,
 �ýڵ㽫��ӵȴ�������ת�Ƶ�ͬ��������,���뵽��ͬ��״̬�Ļ�ȡ��
 4.propagate,ֵ-3,��ʾ��һ�ι���ʽͬ��״̬��ȡ�����������ر�������ȥ
 5.initial,ֵΪ0,��ʼ״̬
 prep ǰ���ڵ�,���ڵ����ͬ������ʱ������(β�����)
 next ��̽ڵ�
 nextWaiter �ȴ������еĺ�̽ڵ�,�����ǰ�ڵ��ǹ����,��ô����ֶν���һ��shared����,
          Ҳ����˵�ڵ�����(��ռ�͹���)�͵ȴ������еĺ�̽ڵ㹲��ͬһ���ֶ�
 thread ��ȡͬ��״̬���߳�
 �ڵ��ǹ���ͬ�����У��ȴ����У���5.6���н�����ܣ��Ļ�����ͬ����ӵ���׽ڵ㣨head��
 ��β�ڵ㣨tail����û�гɹ���ȡͬ��״̬���߳̽����Ϊ�ڵ����ö��е�β����ͬ�����е�
 �����ṹ��ͼ5-1��ʾ��
 ��ͼ5-1�У�ͬ���������������ڵ����͵����ã�һ��ָ��ͷ�ڵ㣬����һ��ָ��β�ڵ㡣
 ����һ�£���һ���̳߳ɹ��ػ�ȡ��ͬ��״̬�����������������߳̽��޷���ȡ��ͬ��״̬��ת
 ���������Ϊ�ڵ㲢���뵽ͬ�������У������������еĹ��̱���Ҫ��֤�̰߳�ȫ�����
 ͬ�����ṩ��һ������CAS������β�ڵ�ķ�����compareAndSetTail(Node expect,Node
 update)������Ҫ���ݵ�ǰ�̡߳���Ϊ����β�ڵ�͵�ǰ�ڵ㣬ֻ�����óɹ��󣬵�ǰ�ڵ����ʽ
 ��֮ǰ��β�ڵ㽨��������
 ͬ�������ڵ���뵽ͬ�����еĹ�����ͼ5-2��ʾ��
 ͬ��������ѭFIFO���׽ڵ��ǻ�ȡͬ��״̬�ɹ��Ľڵ㣬�׽ڵ���߳����ͷ�ͬ��״̬
 ʱ�����ỽ�Ѻ�̽ڵ㣬����̽ڵ㽫���ڻ�ȡͬ��״̬�ɹ�ʱ���Լ�����Ϊ�׽ڵ㣬�ù���
 ��ͼ5-3��ʾ��
 ͼ5-3���׽ڵ������
 ��ͼ5-3�У������׽ڵ���ͨ����ȡͬ��״̬�ɹ����߳�����ɵģ�����ֻ��һ���߳���
 ���ɹ���ȡ��ͬ��״̬���������ͷ�ڵ�ķ���������Ҫʹ��CAS����֤����ֻ��Ҫ���׽�
 �����ó�Ϊԭ�׽ڵ�ĺ�̽ڵ㲢�Ͽ�ԭ�׽ڵ��next���ü��ɡ�
 2.��ռʽͬ��״̬��ȡ���ͷ�
 ͨ������ͬ������acquire(int arg)�������Ի�ȡͬ��״̬���÷������жϲ����У�Ҳ����
 �����̻߳�ȡͬ��״̬ʧ�ܺ����ͬ�������У��������߳̽����жϲ���ʱ���̲߳����ͬ
 ���������Ƴ����÷�������������嵥5-3��ʾ��
 �����嵥5-3��ͬ������acquire����
 public final void acquire(int arg) {
 if (!tryAcquire(arg) &&
 acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
 selfInterrupt();
 }
 ����������Ҫ�����ͬ��״̬��ȡ���ڵ㹹�졢����ͬ�������Լ���ͬ��������������
 ������ع���������Ҫ�߼��ǣ����ȵ����Զ���ͬ����ʵ�ֵ�tryAcquire(int arg)�������÷���
 ��֤�̰߳�ȫ�Ļ�ȡͬ��״̬�����ͬ��״̬��ȡʧ�ܣ�����ͬ���ڵ㣨��ռʽ
 Node.EXCLUSIVE��ͬһʱ��ֻ����һ���̳߳ɹ���ȡͬ��״̬����ͨ��addWaiter(Node node)
 �������ýڵ���뵽ͬ�����е�β����������acquireQueued(Node node,int arg)������ʹ�ø�
 �ڵ��ԡ���ѭ�����ķ�ʽ��ȡͬ��״̬�������ȡ�����������ڵ��е��̣߳����������̵߳�
 ������Ҫ����ǰ���ڵ�ĳ��ӻ������̱߳��ж���ʵ�֡�
 �������һ����ع����������ǽڵ�Ĺ����Լ�����ͬ�����У�������嵥5-4��ʾ��
 �����嵥5-4��ͬ������addWaiter��enq����
 private Node addWaiter(Node mode) {
     Node node = new Node(Thread.currentThread(), mode);
     // ���ٳ�����β�����
     Node pred = tail;
     if (pred != null) {
         node.prev = pred;
         if (compareAndSetTail(pred, node)) {
             pred.next = node;
             return node;
         }
     }
     enq(node);
     return node;
 }
 private Node enq(final Node node) {
     for (;;) {
         Node t = tail;
         if (t == null) { // Must initialize
             if (compareAndSetHead(new Node()))
                 tail = head;
             } else {
                 node.prev = t;
                 if (compareAndSetTail(t, node)) {
                 t.next = node;
                 return t;
             }
         }
     }
 }
 ��������ͨ��ʹ��compareAndSetTail(Node expect,Node update)������ȷ���ڵ��ܹ�����
 �̰�ȫ��ӡ�����һ�£����ʹ��һ����ͨ��LinkedList��ά���ڵ�֮��Ĺ�ϵ����ô��һ����
 �̻�ȡ��ͬ��״̬������������߳����ڵ���tryAcquire(int arg)������ȡͬ��״̬ʧ�ܶ�����
 �ر���ӵ�LinkedListʱ��LinkedList�����Ա�֤Node����ȷ��ӣ����յĽ�������ǽڵ����
 ����ƫ�����˳��Ҳ�ǻ��ҵġ�
 ��enq(final Node node)�����У�ͬ����ͨ������ѭ��������֤�ڵ����ȷ��ӣ��ڡ���ѭ
 ������ֻ��ͨ��CAS���ڵ����ó�Ϊβ�ڵ�֮�󣬵�ǰ�̲߳��ܴӸ÷������أ����򣬵�ǰ��
 �̲��ϵس������á����Կ�����enq(final Node node)������������ӽڵ������ͨ��CAS��
 �á����л����ˡ�
 �ڵ����ͬ������֮�󣬾ͽ�����һ�������Ĺ��̣�ÿ���ڵ㣨����˵ÿ���̣߳�������
 ʡ�ع۲죬���������㣬��ȡ����ͬ��״̬���Ϳ��Դ���������������˳�����������������
 �����������У����������ڵ���̣߳���������嵥5-5��ʾ��
 �����嵥5-5��ͬ������acquireQueued����
 final boolean acquireQueued(final Node node, int arg) {
     boolean failed = true;
     try {
         boolean interrupted = false;
         for (;;) {
             final Node p = node.predecessor();
             if (p == head && tryAcquire(arg)) {
             setHead(node);
             p.next = null; // help GC
             failed = false;
             return interrupted;
         }
         if (shouldParkAfterFailedAcquire(p, node) &&  parkAndCheckInterrupt())
             interrupted = true;
         }
     } finally {
        if (failed)
        cancelAcquire(node);
     }
 }
 ��acquireQueued(final Node node,int arg)�����У���ǰ�߳��ڡ���ѭ�����г��Ի�ȡͬ��״
 ̬����ֻ��ǰ���ڵ���ͷ�ڵ���ܹ����Ի�ȡͬ��״̬������Ϊʲô��ԭ�������������¡�
 ��һ��ͷ�ڵ��ǳɹ���ȡ��ͬ��״̬�Ľڵ㣬��ͷ�ڵ���߳��ͷ���ͬ��״̬֮�󣬽���
 �������̽ڵ㣬��̽ڵ���̱߳����Ѻ���Ҫ����Լ���ǰ���ڵ��Ƿ���ͷ�ڵ㡣
 �ڶ���ά��ͬ�����е�FIFOԭ�򡣸÷����У��ڵ�������ȡͬ��״̬����Ϊ��ͼ5-4��ʾ��
 ��ͼ5-4�У����ڷ��׽ڵ��߳�ǰ���ڵ���ӻ��߱��ж϶��ӵȴ�״̬���أ��������
 ����ǰ���Ƿ���ͷ�ڵ㣬��������Ի�ȡͬ��״̬�����Կ����ڵ�ͽڵ�֮����ѭ�����
 �Ĺ����л������໥ͨ�ţ����Ǽ򵥵��ж��Լ���ǰ���Ƿ�Ϊͷ�ڵ㣬������ʹ�ýڵ����
 �Ź������FIFO������Ҳ���ڶԹ���֪ͨ�Ĵ�������֪ͨ��ָǰ���ڵ㲻��ͷ�ڵ���߳�
 �����ж϶������ѣ���
 ��ռʽͬ��״̬��ȡ���̣�Ҳ����acquire(int arg)�����������̣���ͼ5-5��ʾ��
 ��ͼ5-5�У�ǰ���ڵ�Ϊͷ�ڵ����ܹ���ȡͬ��״̬���ж��������߳̽���ȴ�״̬�ǻ�
 ȡͬ��״̬���������̡���ͬ��״̬��ȡ�ɹ�֮�󣬵�ǰ�̴߳�acquire(int arg)�������أ����
 ���������ֲ���������ԣ������ŵ�ǰ�̻߳�ȡ������
 ��ǰ�̻߳�ȡͬ��״̬��ִ������Ӧ�߼�֮�󣬾���Ҫ�ͷ�ͬ��״̬��ʹ�ú����ڵ���
 ��������ȡͬ��״̬��ͨ������ͬ������release(int arg)���������ͷ�ͬ��״̬���÷�������
 ����ͬ��״̬֮�󣬻ỽ�����̽ڵ㣨����ʹ��̽ڵ����³��Ի�ȡͬ��״̬�����÷�����
 ��������嵥5-6��ʾ��
 �����嵥5-6��ͬ������release����
 public final boolean release(int arg) {
     if (tryRelease(arg)) {
         Node h = head;
         if (h != null && h.waitStatus != 0)
             unparkSuccessor(h);
         return true;
     }
     return false;
 }
 �÷���ִ��ʱ���ỽ��ͷ�ڵ�ĺ�̽ڵ��̣߳�unparkSuccessor(Node node)����ʹ��
 LockSupport���ں�����½ڻ�ר�Ž��ܣ������Ѵ��ڵȴ�״̬���̡߳�
 �����˶�ռʽͬ��״̬��ȡ���ͷŹ��̺��ʵ������ܽ᣺�ڻ�ȡͬ��״̬ʱ��ͬ����ά
 ��һ��ͬ�����У���ȡ״̬ʧ�ܵ��̶߳��ᱻ���뵽�����в��ڶ����н����������Ƴ�����
 ����ֹͣ��������������ǰ���ڵ�Ϊͷ�ڵ��ҳɹ���ȡ��ͬ��״̬�����ͷ�ͬ��״̬ʱ��ͬ��
 ������tryRelease(int arg)�����ͷ�ͬ��״̬��Ȼ����ͷ�ڵ�ĺ�̽ڵ㡣
 3.����ʽͬ��״̬��ȡ���ͷ�
 ����ʽ��ȡ���ռʽ��ȡ����Ҫ����������ͬһʱ���ܷ��ж���߳�ͬʱ��ȡ��ͬ��״
 ̬�����ļ��Ķ�дΪ�������һ�������ڶ��ļ����ж���������ô��һʱ�̶��ڸ��ļ���д��
 ���������������������ܹ�ͬʱ���С�д����Ҫ�����Դ�Ķ�ռʽ���ʣ��������������ǹ���
 ʽ���ʣ����ֲ�ͬ�ķ���ģʽ��ͬһʱ�̶��ļ�����Դ�ķ����������ͼ5-6��ʾ��
 ��ͼ5-6�У���벿�֣�����ʽ������Դʱ����������ʽ�ķ��ʾ�����������ռʽ���ʱ�
 �������Ұ벿���Ƕ�ռʽ������Դʱ��ͬһʱ���������ʾ���������
 ͨ������ͬ������acquireShared(int arg)�������Թ���ʽ�ػ�ȡͬ��״̬���÷���������
 �����嵥5-7��ʾ��
 �����嵥5-7��ͬ������acquireShared��doAcquireShared����
 public final void acquireShared(int arg) {
     if (tryAcquireShared(arg) < 0)
        doAcquireShared(arg);
 }
 private void doAcquireShared(int arg) {
     final Node node = addWaiter(Node.SHARED);
     boolean failed = true;
     try {
         boolean interrupted = false;
         for (;;) {
             final Node p = node.predecessor();
             if (p == head) {
             int r = tryAcquireShared(arg);
             if (r >= 0) {
             setHeadAndPropagate(node, r);
             p.next = null;
             if (interrupted)
             selfInterrupt();
             failed = false;
             return;
             }
         }
         if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
            interrupted = true;
         }
     } finally {
        if (failed)
            cancelAcquire(node);
     }
 }

 ��acquireShared(int arg)�����У�ͬ��������tryAcquireShared(int arg)�������Ի�ȡͬ��״
 ̬��tryAcquireShared(int arg)��������ֵΪint���ͣ�������ֵ���ڵ���0ʱ����ʾ�ܹ���ȡ��ͬ
 ��״̬����ˣ��ڹ���ʽ��ȡ�����������У��ɹ���ȡ��ͬ��״̬���˳���������������
 tryAcquireShared(int arg)��������ֵ���ڵ���0�����Կ�������doAcquireShared(int arg)��������
 �������У������ǰ�ڵ��ǰ��Ϊͷ�ڵ�ʱ�����Ի�ȡͬ��״̬���������ֵ���ڵ���0����ʾ
 �ôλ�ȡͬ��״̬�ɹ����������������˳���
 ���ռʽһ��������ʽ��ȡҲ��Ҫ�ͷ�ͬ��״̬��ͨ������releaseShared(int arg)��������
 �ͷ�ͬ��״̬���÷�������������嵥5-8��ʾ��
 �����嵥5-8��ͬ������releaseShared����
 public final boolean releaseShared(int arg) {
 if (tryReleaseShared(arg)) {
 doReleaseShared();
 return true;
 }
 return false;
 }
 �÷������ͷ�ͬ��״̬֮�󣬽��ỽ�Ѻ������ڵȴ�״̬�Ľڵ㡣�����ܹ�֧�ֶ����
 ��ͬʱ���ʵĲ������������Semaphore�������Ͷ�ռʽ��Ҫ��������tryReleaseShared(int arg)
 ��������ȷ��ͬ��״̬��������Դ�����̰߳�ȫ�ͷţ�һ����ͨ��ѭ����CAS����֤�ģ���Ϊ
 �ͷ�ͬ��״̬�Ĳ�����ͬʱ���Զ���̡߳�
 4.��ռʽ��ʱ��ȡͬ��״̬
 ͨ������ͬ������doAcquireNanos(int arg,long nanosTimeout)�������Գ�ʱ��ȡͬ��״
 ̬������ָ����ʱ����ڻ�ȡͬ��״̬�������ȡ��ͬ��״̬�򷵻�true�����򣬷���false����
 �����ṩ�˴�ͳJavaͬ������������synchronized�ؼ��֣������߱������ԡ�
 �ڷ����÷�����ʵ��ǰ���Ƚ���һ����Ӧ�жϵ�ͬ��״̬��ȡ���̡���Java 5֮ǰ����һ
 ���̻߳�ȡ����������������synchronized֮��ʱ���Ը��߳̽����жϲ�������ʱ���̵߳���
 �ϱ�־λ�ᱻ�޸ģ����߳����ɻ�������synchronized�ϣ��ȴ��Ż�ȡ������Java 5�У�ͬ����
 �ṩ��acquireInterruptibly(int arg)��������������ڵȴ���ȡͬ��״̬ʱ�������ǰ�̱߳���
 �ϣ������̷��أ����׳�InterruptedException��
 ��ʱ��ȡͬ��״̬���̿��Ա�������Ӧ�жϻ�ȡͬ��״̬���̵ġ���ǿ�桱��
 doAcquireNanos(int arg,long nanosTimeout)������֧����Ӧ�жϵĻ����ϣ������˳�ʱ��ȡ��
 ���ԡ���Գ�ʱ��ȡ����Ҫ��Ҫ�������Ҫ˯�ߵ�ʱ����nanosTimeout��Ϊ�˷�ֹ����֪ͨ��
 nanosTimeout���㹫ʽΪ��nanosTimeout-=now-lastTime������nowΪ��ǰ����ʱ�䣬lastTimeΪ��
 �λ���ʱ�䣬���nanosTimeout����0���ʾ��ʱʱ��δ������Ҫ����˯��nanosTimeout���룬
 ��֮����ʾ�Ѿ���ʱ���÷�������������嵥5-9��ʾ��
 �����嵥5-9��ͬ������doAcquireNanos����
 private boolean doAcquireNanos(int arg, long nanosTimeout)
 throws InterruptedException {
 long lastTime = System.nanoTime();
 final Node node = addWaiter(Node.EXCLUSIVE);
 boolean failed = true;
 try {
 for (;;) {
 final Node p = node.predecessor();
 if (p == head && tryAcquire(arg)) {
 setHead(node);
 p.next = null; // help GC
 failed = false;
 return true;
 }
 if (nanosTimeout <= 0)
 return false;
 if (shouldParkAfterFailedAcquire(p, node)
 && nanosTimeout > spinForTimeoutThreshold)
 LockSupport.parkNanos(this, nanosTimeout);
 long now = System.nanoTime();
 //����ʱ�䣬��ǰʱ��now��ȥ˯��֮ǰ��ʱ��lastTime�õ��Ѿ�˯��
 //��ʱ��delta��Ȼ��ԭ�г�ʱʱ��nanosTimeout��ȥ���õ���
 //��Ӧ��˯�ߵ�ʱ��
 nanosTimeout -= now - lastTime;
 lastTime = now;
 if (Thread.interrupted())
 throw new InterruptedException();
 }
 } finally {
 if (failed)
 cancelAcquire(node);
 }
 }
 �÷��������������У����ڵ��ǰ���ڵ�Ϊͷ�ڵ�ʱ���Ի�ȡͬ��״̬�������ȡ�ɹ�
 ��Ӹ÷������أ�������̺Ͷ�ռʽͬ����ȡ�Ĺ������ƣ�������ͬ��״̬��ȡʧ�ܵĴ���
 ��������ͬ�������ǰ�̻߳�ȡͬ��״̬ʧ�ܣ����ж��Ƿ�ʱ��nanosTimeoutС�ڵ���0��ʾ
 �Ѿ���ʱ�������û�г�ʱ�����¼��㳬ʱ���nanosTimeout��Ȼ��ʹ��ǰ�̵߳ȴ�
 nanosTimeout���루���ѵ����õĳ�ʱʱ�䣬���̻߳��LockSupport.parkNanos(Object
 blocker,long nanos)�������أ���
 ���nanosTimeoutС�ڵ���spinForTimeoutThreshold��1000���룩ʱ��������ʹ���߳̽���
 ��ʱ�ȴ������ǽ�����ٵ��������̡�ԭ�����ڣ��ǳ��̵ĳ�ʱ�ȴ��޷�����ʮ�־�ȷ�����
 ��ʱ�ٽ��г�ʱ�ȴ����෴����nanosTimeout�ĳ�ʱ�������ϱ��ֵ÷�������ȷ����ˣ��ڳ�
 ʱ�ǳ��̵ĳ����£�ͬ����������������Ŀ���������
 ��ռʽ��ʱ��ȡͬ��̬��������ͼ5-7��ʾ��
 ��ͼ5-7�п��Կ�������ռʽ��ʱ��ȡͬ��״̬doAcquireNanos(int arg,long nanosTimeout)
 �Ͷ�ռʽ��ȡͬ��״̬acquire(int args)�������Ϸǳ����ƣ�����Ҫ��������δ��ȡ��ͬ��״
 ̬ʱ�Ĵ����߼���acquire(int args)��δ��ȡ��ͬ��״̬ʱ������ʹ��ǰ�߳�һֱ���ڵȴ�״
 ̬����doAcquireNanos(int arg,long nanosTimeout)��ʹ��ǰ�̵߳ȴ�nanosTimeout���룬�����
 ǰ�߳���nanosTimeout������û�л�ȡ��ͬ��״̬������ӵȴ��߼����Զ����ء�
 5.�Զ���ͬ���������TwinsLock
 ��ǰ����½��У���ͬ����AbstractQueuedSynchronizer������ʵ�ֲ���ķ���������ͨ��
 ��дһ���Զ���ͬ������������ͬ��������⡣
 ���һ��ͬ�����ߣ��ù�����ͬһʱ�̣�ֻ�������������߳�ͬʱ���ʣ����������̵߳�
 ���ʽ������������ǽ����ͬ����������ΪTwinsLock��
 ���ȣ�ȷ������ģʽ��TwinsLock�ܹ���ͬһʱ��֧�ֶ���̵߳ķ��ʣ�����Ȼ�ǹ���ʽ
 ���ʣ���ˣ���Ҫʹ��ͬ�����ṩ��acquireShared(int args)�����Ⱥ�Shared��صķ��������Ҫ
 ��TwinsLock������дtryAcquireShared(int args)������tryReleaseShared(int args)��������������
 ��֤ͬ�����Ĺ���ʽͬ��״̬�Ļ�ȡ���ͷŷ�������ִ�С�
 ��Σ�������Դ����TwinsLock��ͬһʱ���������������̵߳�ͬʱ���ʣ�����ͬ����Դ
 ��Ϊ2�������������ó�ʼ״̬statusΪ2����һ���߳̽��л�ȡ��status��1�����߳��ͷţ���
 status��1��״̬�ĺϷ���ΧΪ0��1��2������0��ʾ��ǰ�Ѿ��������̻߳�ȡ��ͬ����Դ����ʱ
 ���������̶߳�ͬ��״̬���л�ȡ�����߳�ֻ�ܱ���������ͬ��״̬���ʱ����Ҫʹ��
 compareAndSet(int expect,int update)������ԭ���Ա��ϡ�
 �������Զ���ͬ������ǰ����½��ᵽ���Զ���ͬ�����ͨ������Զ���ͬ��������
 ��ͬ�����ܣ�һ��������Զ���ͬ�����ᱻ����Ϊ�Զ���ͬ��������ڲ��ࡣ
 TwinsLock�����֣�����������嵥5-10��ʾ��
 public class TwinsLock implements Lock {
 private final Sync sync = new Sync(2);
 private static final class Sync extends AbstractQueuedSynchronizer {
 Sync(int count) {
 if (count <= 0) {
 throw new IllegalArgumentException("count must large
 than zero.");
 }
 setState(count);
 }
 public int tryAcquireShared(int reduceCount) {
 for (;;) {
 int current = getState();
 int newCount = current - reduceCount;
 if (newCount < 0 || compareAndSetState(current,
 newCount)) {
 return newCount;
 }
 }
 }
 public boolean tryReleaseShared(int returnCount) {
 for (;;) {
 int current = getState();
 int newCount = current + returnCount;
 if (compareAndSetState(current, newCount)) {
 return true;
 }
 }
 }
 }
 public void lock() {
 sync.acquireShared(1);
 }
 public void unlock() {
 sync.releaseShared(1);
 }
 // �����ӿڷ�����
 }
 ������ʾ���У�TwinsLockʵ����Lock�ӿڣ��ṩ������ʹ���ߵĽӿڣ�ʹ���ߵ���lock()
 ������ȡ����������unlock()�����ͷ�������ͬһʱ��ֻ���������߳�ͬʱ��ȡ������
 TwinsLockͬʱ������һ���Զ���ͬ����Sync������ͬ���������̷߳��ʺ�ͬ��״̬���ơ���
 ����ʽ��ȡͬ��״̬Ϊ����ͬ�������ȼ������ȡ���ͬ��״̬��Ȼ��ͨ��CASȷ��״̬����
 ȷ���ã���tryAcquireShared(int reduceCount)��������ֵ���ڵ���0ʱ����ǰ�̲߳Ż�ȡͬ��״
 ̬�������ϲ��TwinsLock���ԣ����ʾ��ǰ�̻߳��������
 ͬ������Ϊһ�������������̷߳����Լ�ͬ��״̬���Ƶȵײ㼼���벻ͬ�������������
 Lock��CountDownLatch�ȣ��Ľӿ����塣
 �����дһ����������֤TwinsLock�Ƿ��ܰ���Ԥ�ڹ������ڲ��������У������˹�����
 �߳�Worker�����߳���ִ�й����л�ȡ��������ȡ��֮��ʹ��ǰ�߳�˯��1�루�����ͷ�������
 ����ӡ��ǰ�߳����ƣ�����ٴ�˯��1�벢�ͷ�������������������嵥5-11��ʾ��
 �����嵥5-11��TwinsLockTest.java
 public class TwinsLockTest {
    @Test
    public void test() {
        final Lock lock = new TwinsLock();
        class Worker extends Thread {
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        SleepUtils.second(1);
                        System.out.println(Thread.currentThread().getName());
                        SleepUtils.second(1);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
        // ����10���߳�
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setDaemon(true);
            w.start();
        }
        // ÿ��1�뻻��
        for (int i = 0; i < 10; i++) {
            SleepUtils.second(1);
            System.out.println();
        }
    }
}
 ���иò������������Կ����߳����Ƴɶ������Ҳ������ͬһʱ��ֻ�������߳��ܹ���
 ȡ�����������TwinsLock���԰���Ԥ����ȷ������
 5.3��������
 ������ReentrantLock������˼�壬����֧���ؽ������������ʾ�����ܹ�֧��һ���̶߳�
 ��Դ���ظ�����������֮�⣬�����Ļ�֧�ֻ�ȡ��ʱ�Ĺ�ƽ�ͷǹ�ƽ��ѡ��
 ������ͬ����һ���е�ʾ����Mutex����ͬʱ�������³�������һ���̵߳���Mutex��lock()
 ������ȡ��֮������ٴε���lock()����������߳̽��ᱻ�Լ���������ԭ����Mutex��ʵ��
 tryAcquire(int acquires)����ʱû�п���ռ�������߳��ٴλ�ȡ���ĳ��������ڵ���
 tryAcquire(int acquires)����ʱ������false�����¸��̱߳��������򵥵�˵��Mutex��һ����֧��
 �ؽ����������synchronized�ؼ�����ʽ��֧���ؽ��룬����һ��synchronized���εĵݹ鷽
 �����ڷ���ִ��ʱ��ִ���߳��ڻ�ȡ����֮������������εػ�ø�����������Mutex���ڻ�
 ȡ������������һ�λ�ȡ��ʱ���������Լ��������
 ReentrantLock��Ȼû����synchronized�ؼ���һ��֧����ʽ���ؽ��룬�����ڵ���lock()��
 ��ʱ���Ѿ���ȡ�������̣߳��ܹ��ٴε���lock()������ȡ��������������
 �����ᵽһ������ȡ�Ĺ�ƽ�����⣬����ھ���ʱ���ϣ��ȶ������л�ȡ������һ����
 �����㣬��ô������ǹ�ƽ�ģ���֮���ǲ���ƽ�ġ���ƽ�Ļ�ȡ����Ҳ���ǵȴ�ʱ�������
 �������Ȼ�ȡ����Ҳ����˵����ȡ��˳��ġ�ReentrantLock�ṩ��һ�����캯�����ܹ�������
 �Ƿ��ǹ�ƽ�ġ�
 ��ʵ�ϣ���ƽ������������û�зǹ�ƽ��Ч�ʸߣ����ǣ��������κγ���������TPS��Ϊ
 Ψһ��ָ�꣬��ƽ���ܹ����١������������ĸ��ʣ��ȴ�Խ�õ�����Խ���ܹ��õ��������㡣
 ���潫���ط���ReentrantLock�����ʵ���ؽ���͹�ƽ�Ի�ȡ�������ԣ���ͨ��������
 ��֤��ƽ�Ի�ȡ�������ܵ�Ӱ�졣
 1.ʵ���ؽ���
 �ؽ�����ָ�����߳��ڻ�ȡ����֮���ܹ��ٴλ�ȡ���������ᱻ���������������Ե�ʵ
 ����Ҫ��������������⡣
 1���߳��ٴλ�ȡ��������Ҫȥʶ���ȡ�����߳��Ƿ�Ϊ��ǰռ�������̣߳�����ǣ�����
 �γɹ���ȡ��
 2�����������ͷš��߳��ظ�n�λ�ȡ����������ڵ�n���ͷŸ����������߳��ܹ���ȡ��
 ���������������ͷ�Ҫ�������ڻ�ȡ���м���������������ʾ��ǰ�����ظ���ȡ�Ĵ���������
 ���ͷ�ʱ�������Լ�������������0ʱ��ʾ���Ѿ��ɹ��ͷš�
 ReentrantLock��ͨ������Զ���ͬ������ʵ�����Ļ�ȡ���ͷţ��Էǹ�ƽ�ԣ�Ĭ�ϵģ�ʵ
 ��Ϊ������ȡͬ��״̬�Ĵ���������嵥5-12��ʾ��
 �����嵥5-12��ReentrantLock��nonfairTryAcquire����
 final boolean nonfairTryAcquire(int acquires) {
     final Thread current = Thread.currentThread();
     int c = getState();
     if (c == 0) {
     if (compareAndSetState(0, acquires)) {
     setExclusiveOwnerThread(current);
     return true;
     }
     } else if (current == getExclusiveOwnerThread()) {
     int nextc = c + acquires;
     if (nextc < 0)
     throw new Error("Maximum lock count exceeded");
     setState(nextc);
     return true;
     }
     return false;
 }
 �÷����������ٴλ�ȡͬ��״̬�Ĵ����߼���ͨ���жϵ�ǰ�߳��Ƿ�Ϊ��ȡ�����߳���
 ������ȡ�����Ƿ�ɹ�������ǻ�ȡ�����߳��ٴ�������ͬ��״ֵ̬�������Ӳ�����
 true����ʾ��ȡͬ��״̬�ɹ���
 �ɹ���ȡ�����߳��ٴλ�ȡ����ֻ��������ͬ��״ֵ̬����Ҳ��Ҫ��ReentrantLock���ͷ�
 ͬ��״̬ʱ����ͬ��״ֵ̬���÷����Ĵ���������嵥5-13��ʾ��
 �����嵥5-13��ReentrantLock��tryRelease����
 protected final boolean tryRelease(int releases) {
 int c = getState() - releases;
 if (Thread.currentThread() != getExclusiveOwnerThread())
 throw new IllegalMonitorStateException();
 boolean free = false;
 if (c == 0) {
 free = true;
 setExclusiveOwnerThread(null);
 }
 setState(c);
 return free;
 }
 �����������ȡ��n�Σ���ôǰ(n-1)��tryRelease(int releases)�������뷵��false����ֻ��ͬ
 ��״̬��ȫ�ͷ��ˣ����ܷ���true�����Կ������÷�����ͬ��״̬�Ƿ�Ϊ0��Ϊ�����ͷŵ���
 ������ͬ��״̬Ϊ0ʱ����ռ���߳�����Ϊnull��������true����ʾ�ͷųɹ���
 2.��ƽ��ǹ�ƽ��ȡ��������
 ��ƽ���������Ի�ȡ�����Եģ����һ�����ǹ�ƽ�ģ���ô���Ļ�ȡ˳���Ӧ�÷���
 ����ľ���ʱ��˳��Ҳ����FIFO��
 �ع���һС���н��ܵ�nonfairTryAcquire(int acquires)���������ڷǹ�ƽ����ֻҪCAS����
 ͬ��״̬�ɹ������ʾ��ǰ�̻߳�ȡ����������ƽ����ͬ��������嵥5-14��ʾ��
 �����嵥5-14��ReentrantLock��tryAcquire����
 protected final boolean tryAcquire(int acquires) {
 final Thread current = Thread.currentThread();
 int c = getState();
 if (c == 0) {
 if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
 setExclusiveOwnerThread(current);
 return true;
 }
 } else if (current == getExclusiveOwnerThread()) {
 int nextc = c + acquires;
 if (nextc < 0)
 throw new Error("Maximum lock count exceeded");
 setState(nextc);
 return true;
 }
 return false;
 }
 �÷�����nonfairTryAcquire(int acquires)�Ƚϣ�Ψһ��ͬ��λ��Ϊ�ж���������
 hasQueuedPredecessors()��������������ͬ�������е�ǰ�ڵ��Ƿ���ǰ���ڵ���жϣ������
 ��������true�����ʾ���̱߳ȵ�ǰ�̸߳���������ȡ���������Ҫ�ȴ�ǰ���̻߳�ȡ����
 ����֮����ܼ�����ȡ����
 �����дһ���������۲칫ƽ�ͷǹ�ƽ���ڻ�ȡ��ʱ�������ڲ��������ж������ڲ�
 ��ReentrantLock2��������Ҫ������getQueuedThreads()�������÷����������ڵȴ���ȡ������
 ���б������б������������Ϊ�˷���۲�����������з�ת���������������֣��������
 ��5-15��ʾ��
 �����嵥5-15��FairAndUnfairTest.java
 public class FairAndUnfairTest {
 private static Lock fairLock = new ReentrantLock2(true);
 private static Lock unfairLock = new ReentrantLock2(false);
 @Test
 public void fair() {
 testLock(fairLock);
 }
 @Test
 public void unfair() {
 testLock(unfairLock);
 }
 private void testLock(Lock lock) {
 // ����5��Job���ԣ�
 }
 private static class Job extends Thread {
 private Lock lock;
 public Job(Lock lock) {
 this.lock = lock;
 }
 public void run() {
 // ����2�δ�ӡ��ǰ��Thread�͵ȴ������е�Thread���ԣ�
 }
 }
 private static class ReentrantLock2 extends ReentrantLock {
 public ReentrantLock2(boolean fair) {
 super(fair);
 }
 public Collection<Thread> getQueuedThreads() {
 List<Thread> arrayList = new ArrayList<Thread>(super.
 getQueuedThreads());
 Collections.reverse(arrayList);
 return arrayList;
 }
 }
 }
 �ֱ�����fair()��unfair()�������Է��������������5-6��ʾ��
 �۲��5-6��ʾ�Ľ��������ÿ�����ִ���һ���̣߳�����ƽ����ÿ�ζ��Ǵ�ͬ�������е�
 ��һ���ڵ��ȡ���������ǹ�ƽ����������һ���߳�������ȡ���������
 Ϊʲô������߳�������ȡ��������أ��ع�nonfairTryAcquire(int acquires)��������һ
 ���߳�������ʱ��ֻҪ��ȡ��ͬ��״̬���ɹ���ȡ���������ǰ���£����ͷ������߳��ٴλ�
 ȡͬ��״̬�ļ��ʻ�ǳ���ʹ�������߳�ֻ����ͬ�������еȴ���
 �ǹ�ƽ��������ʹ�̡߳���������Ϊʲô���ֱ��趨��Ĭ�ϵ�ʵ���أ��ٴι۲��ϱ�Ľ�
 ���������ÿ�β�ͬ�̻߳�ȡ��������Ϊ1���л�����ƽ�����ڲ����н�����10���л�������
 ��ƽ����ֻ��5���л�����˵���ǹ�ƽ�����Ŀ�����С���������в������������Ի�����ubuntu
 server 14.04 i5-34708GB�����Գ�����10���̣߳�ÿ���̻߳�ȡ100000��������ͨ��vmstatͳ�Ʋ�
 ������ʱϵͳ�߳��������л��Ĵ��������н�����5-7��ʾ��
 ��5-7����ƽ�Ժͷǹ�ƽ����ϵͳ�߳��������л�����ĶԱ�
 �ڲ����й�ƽ������ǹ�ƽ������ȣ��ܺ�ʱ����94.3�������л���������133��������
 ��������ƽ������֤�����Ļ�ȡ����FIFOԭ�򣬶������ǽ��д������߳��л����ǹ�ƽ������
 Ȼ��������̡߳��������������ٵ��߳��л�����֤����������������
 5.4����д��
 ֮ǰ�ᵽ������Mutex��ReentrantLock��������������������Щ����ͬһʱ��ֻ����һ����
 �̽��з��ʣ�����д����ͬһʱ�̿������������̷߳��ʣ�������д�̷߳���ʱ�����еĶ�
 �̺߳�����д�߳̾�����������д��ά����һ������һ��������һ��д����ͨ�����������д
 ����ʹ�ò��������һ������������˺ܴ�������
 ���˱�֤д�����Զ������Ŀɼ����Լ������Ե�����֮�⣬��д���ܹ��򻯶�д������
 ���ı�̷�ʽ�������ڳ����ж���һ������������������ݽṹ�����󲿷�ʱ���ṩ������
 �������ѯ������������д����ռ�е�ʱ����٣�����д�������֮��ĸ�����Ҫ�Ժ����Ķ�
 ����ɼ���
 ��û�ж�д��֧�ֵģ�Java 5֮ǰ��ʱ�������Ҫ�������������Ҫʹ��Java�ĵȴ�֪ͨ
 ���ƣ����ǵ�д������ʼʱ����������д�����Ķ������������ȴ�״̬��ֻ��д������ɲ�
 ����֪֮ͨ�����еȴ��Ķ��������ܼ���ִ�У�д����֮������synchronized�ؼ�����ͬ
 ��������������Ŀ����ʹ�������ܶ�ȡ����ȷ�����ݣ����������������ö�д��ʵ��������
 �ܣ�ֻ��Ҫ�ڶ�����ʱ��ȡ������д����ʱ��ȡд�����ɡ���д������ȡ��ʱ���������ǵ�ǰд
 �����̣߳��Ķ�д�������ᱻ������д���ͷ�֮�����в�������ִ�У���̷�ʽ�����ʹ��
 �ȴ�֪ͨ���Ƶ�ʵ�ַ�ʽ���ԣ���ü����ˡ�
 һ������£���д�������ܶ�����������ã���Ϊ������������Ƕ���д�ġ��ڶ�����д
 ������£���д���ܹ��ṩ�����������õĲ����Ժ���������Java�������ṩ��д����ʵ����
 ReentrantReadWriteLock�����ṩ���������5-8��ʾ��
 ��5-8��ReentrantReadWriteLock������
 ��ƽ��ѡ��:֧�ַǹ�ƽ(Ĭ��)�͹�ƽ������ȡ��ʽ,���������Ƿǹ�ƽ���ڹ�ƽ
 �ؽ���:����֧���ؽ���,������д�����ѻ�õ���������ٴλ�ȡ
 ������:��ѭ��ȡд��,��ȡ�������ͷ�д���Ĵ���,д���ܹ�������Ϊ����

 �����嵥5-16��Cache.java
 */
class Cache {
    static Map<String, Object> map = new HashMap<String, Object>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock r = rwl.readLock();
    static Lock w = rwl.writeLock();
    // ��ȡһ��key��Ӧ��value
    public static final Object get(String key) {
        r.lock();
        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }
    // ����key��Ӧ��value�������ؾɵ�value
    public static final Object put(String key, Object value) {
        w.lock();
        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }
    // ������е�����
    public static final void clear() {
        w.lock();
        try {
            map.clear();
        } finally {
            w.unlock();
        }
    }
}
/**
 put�����ڸ��»��������ǰ������ǰ��ȡд��������ȡд��֮�������̶߳��ڶ�����д���Ļ�ȡ����������
 ֻ��д���ͷź��������������ܼ�������get�����У���Ҫ��ȡ����������ʱ�����߳̾��ɷ��ʸ÷���������������
 5.4.2����д����ʵ�ַ���
 ��д��ͬ������ͬ����ʵ�����Ĺ��ܣ���ReetrantLock�У�ͬ��״̬��ʾ����һ���߳��ظ���ȡ�Ĵ�����
 ����д�����Զ���ͬ������Ҫ��ͬ��״̬��ά��������̺߳�һ��д�̵߳�״̬��
 �������һ�����ͱ�����ά������һ��״̬����ô���ð�λ�ָ�ķ�ʽ��һ�������ѡ��
 ��һ��������Ϊ�������֣���16λ��ʾ������16λ��ʾд
 2.д���Ļ�ȡ���ͷ�
 д����һ��֧���ؽ�����������������ǰ�߳��Ѿ���ȡ��д����������д״̬�������
 ǰ�߳��ڻ�ȡд��ʱ�������Ѿ�����ȡ����״̬��Ϊ0�����߸��̲߳����Ѿ���ȡд�����̣߳�
 ��ǰ�߳̽���ȴ�״̬
 д���Ļ�ȡ��ReentrantReadWriteLock��tryAcquire����
 ���������ж϶�д״̬,�����Ϊ0�Ҵ��ڶ��������Ѵ��ڵ�д�����ǵ�ǰ�̻߳�ȡ��,��д�����ܻ�ȡ,
 ֻ�ܵȴ������̶߳��ͷ��˶���,д�����ܱ���ǰ�̻߳�ȡ
 3.�����Ļ�ȡ���ͷ�
 ������һ��֧���ؽ���Ĺ����������ܹ�������߳�ͬʱ��ȡ����û����д���̷߳���ʱ�������ܻᱻ�ɹ��ػ�ȡ��
 �����ǰ�߳��Ѿ���ȡ�˶����������Ӷ�״̬�������ȡ����ʱд���Ѿ��������̻߳�ȡ�������ȴ�״̬��
 �����Ļ�ȡ�������ڲ�ͬ����Sync��tryAcquireShared������
 ��������߳��Ѿ���ȡ��д������ǰ�̻߳�ȡ����ʧ�ܣ�����ȴ�״̬�������ǰ�̻߳�ȡ��д������д��δ����ȡ��
 ��ǰ�̣߳��̰߳�ȫ������CAS��֤�����Ӷ�״̬���ɹ���ȡ������
 4.������
 ��������ָ�ѳ�ס����ǰӵ�еģ�д�����ٻ�ȡ������������ͷţ���ǰӵ�еģ�д���Ĺ��̡�
 public void processData() {
     readLock.lock();
     if (!update) { //update //bool����volatile����
         // �������ͷŶ���
         readLock.unlock();
         // ��������д����ȡ����ʼ
         writeLock.lock();
         try {
             if (!update) {
                 // ׼�����ݵ����̣��ԣ�
                update = true;
             }
             readLock.lock();
         } finally {
            writeLock.unlock();
         }
         // ��������ɣ�д������Ϊ����
     }
     try {
        // ʹ�����ݵ����̣��ԣ�
     } finally {
         readLock.unlock();
     }
 }
 ����ʾ���У������ݷ��������update����������������volatile���Σ�������Ϊfalse����
 ʱ���з���processData()�������̶߳��ܹ���֪���仯����ֻ��һ���߳��ܹ���ȡ��д������
 ���̻߳ᱻ�����ڶ�����д����lock()�����ϡ���ǰ�̻߳�ȡд���������׼��֮���ٻ�ȡ
 ����������ͷ�д���������������
 RentrantReadWriteLock��֧�����������ѳֶ�������ȡд��������ͷŶ����Ĺ��̣���Ŀ��
 Ҳ�Ǳ�֤���ݿɼ��ԣ���������ѱ�����̻߳�ȡ�����������̳߳ɹ���ȡ��д����������
 ���ݣ�������¶�������ȡ���������߳��ǲ��ɼ��ġ�
 5.5��LockSupport����
 ����Ҫ��������һ���̵߳�ʱ�򣬶���ʹ��LockSupport�������������Ӧ������
 LockSupport������һ��Ĺ�����̬��������Щ�����ṩ����������߳������ͻ��ѹ���
 park() ������ǰ�߳�,�������unpark�������ߵ�ǰ�̱߳��ж�,���ܴ�park��������
 parkNanos(long) park�����ϼӳ�ʱ����
 parkUntil(long) ������ǰ�߳�,ֱ��ĳʱ��
 unpark ���Ѵ�������״̬���߳�
 5.6��Condition�ӿ�
 Condition�ӿ��ṩ������Object�ļ�����(wait,notify)��������Lock��Ͽ���ʵ�ֵȴ�/֪ͨģʽ
 5.6.1��Condition�ӿ���ʾ��
 Condition�����˵ȴ�/֪ͨ�������͵ķ�������ǰ�̵߳�����Щ����ʱ����Ҫ��ǰ��ȡ��
 Condition�������������Condition��������Lock���󣨵���Lock�����newCondition()��������
 �������ģ����仰˵��Condition������Lock����ġ�
 Condition��ʹ�÷�ʽ�Ƚϼ򵥣���Ҫע���ڵ��÷���ǰ��ȡ����ʹ�÷�ʽ������嵥5-20
 ��ʾ��
 �����嵥5-20��ConditionUseCase.java
 Lock lock = new ReentrantLock();
 Condition condition = lock.newCondition();
 public void conditionWait() throws InterruptedException {
     lock.lock();
     try {
        condition.await();
     } finally {
         lock.unlock();
     }
 }
 public void conditionSignal() throws InterruptedException {
     lock.lock();
     try {
        condition.signal();
     } finally {
         lock.unlock();
     }
 }
 Condition����ģ����֣������Լ��������5-13��ʾ��
 await() throws InterruptedExeption:��ǰ�߳̽���ȴ�״ֱ̬����֪ͨ(signal)���ж�,��ǰ�߳̽���������״̬��
                                    ��await()�������ص����,����:
                                    �����̵߳��ø�Condition��signal()��signalAll()����,����ǰ�̱߳�ѡ����
                                    1,�����߳�(����interrupt����)�жϵ�ǰ�߳�
                                    2,�����ǰ�ȴ��̴߳�await()��������,��ô�������߳��Ѿ���ȡ��Condition����
                                    ����Ӧ����
 signal() ����һ���ȴ���Condition�ϵ��߳�,���̴߳ӵȴ��ȴ���������ǰ��������Condition��ص���
 ��ȡһ��Condition����ͨ��Lock��newCondition()����������ͨ��һ���н���е�ʾ����
 �����˽�Condition��ʹ�÷�ʽ���н������һ������Ķ��У�������Ϊ��ʱ�����еĻ�ȡ����
 ����������ȡ�̣߳�ֱ��������������Ԫ�أ�����������ʱ�����еĲ��������������������
 �̣�ֱ�����г��֡���λ����������嵥5-21��ʾ��
 */
class BoundedQueue<T> {
    private Object[] items;
    // ��ӵ��±꣬ɾ�����±�����鵱ǰ����
    private int addIndex, removeIndex, count;
    private Lock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();
    public BoundedQueue(int size) {
        items = new Object[size];
    }
    // ���һ��Ԫ�أ������������������߳̽���ȴ�״̬��ֱ����"��λ"
    public void add(T t) throws InterruptedException {
        lock.lock(); //��ȡ��,��֤�����޸Ŀɼ��Ժ�������
        try {
            while (count == items.length)
                notFull.await();
            items[addIndex] = t;
            if (++addIndex == items.length)
                addIndex = 0;
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    // ��ͷ��ɾ��һ��Ԫ�أ��������գ���ɾ���߳̽���ȴ�״̬��ֱ���������Ԫ��
    @SuppressWarnings("unchecked")
    public T remove() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0)
                notEmpty.await();
            Object x = items[removeIndex];
            if (++removeIndex == items.length)
                removeIndex = 0;
            --count;
            notFull.signal();
            return (T) x;
        } finally {
            lock.unlock();
        }
    }
}
/**
 5.6.2��Condition��ʵ�ַ���
 ConditionObject��ͬ����AbstractQueuedSynchronizer���ڲ��࣬��ΪCondition�Ĳ�����Ҫ
 ��ȡ�����������������Ϊͬ�������ڲ���Ҳ��Ϊ����ÿ��Condition���󶼰�����һ����
 �У����³�Ϊ�ȴ����У����ö�����Condition����ʵ�ֵȴ�/֪ͨ���ܵĹؼ���
 ���潫����Condition��ʵ�֣���Ҫ�������ȴ����С��ȴ���֪ͨ�������ᵽ��Condition��
 ������˵����ָ����ConditionObject��
 1.�ȴ�����
 �ȴ�������һ��FIFO�Ķ��У��ڶ����е�ÿ���ڵ㶼������һ���߳����ã����߳̾���
 ��Condition�����ϵȴ����̣߳����һ���̵߳�����Condition.await()��������ô���߳̽���
 �ͷ���������ɽڵ����ȴ����в�����ȴ�״̬����ʵ�ϣ��ڵ�Ķ��帴����ͬ�����нڵ�
 �Ķ��壬Ҳ����˵��ͬ�����к͵ȴ������нڵ����Ͷ���ͬ�����ľ�̬�ڲ���
 AbstractQueuedSynchronizer.Node��
 һ��Condition����һ���ȴ����У�Conditionӵ���׽ڵ㣨firstWaiter����β�ڵ�
 ��lastWaiter������ǰ�̵߳���Condition.await()�����������Ե�ǰ�̹߳���ڵ㣬�����ڵ��β��
 ����ȴ����У��ȴ����еĻ����ṹ��ͼ5-9��ʾ��
 ��Object�ļ�����ģ���ϣ�һ������ӵ��һ��ͬ�����к͵ȴ����У����������е�
 Lock����ȷ�е�˵��ͬ������ӵ��һ��ͬ�����кͶ���ȴ����У����Ӧ��ϵ��ͼ5-10��ʾ��
 2.�ȴ�
 ����Condition��await()������������await��ͷ�ķ���������ʹ��ǰ�߳̽���ȴ����в���
 ������ͬʱ�߳�״̬��Ϊ�ȴ�״̬������await()��������ʱ����ǰ�߳�һ����ȡ��Condition��
 ����������
 ����Ӷ��У�ͬ�����к͵ȴ����У��ĽǶȿ�await()������������await()����ʱ���൱��ͬ
 �����е��׽ڵ㣨��ȡ�����Ľڵ㣩�ƶ���Condition�ĵȴ������С�
 3.֪ͨ
 ����Condition��signal()���������ỽ���ڵȴ������еȴ�ʱ����Ľڵ㣨�׽ڵ㣩����
 ���ѽڵ�֮ǰ���Ὣ�ڵ��Ƶ�ͬ�������С�
 */