package com.aw.theArtOfJavaConcurrencyProgramming;


public class Chapter06 {
    public static void main(String[] args) {
        System.out.println("java并发容器和框架");
    }
}
/**
 6.1　ConcurrentHashMap的实现原理与使用
 ConcurrentHashMap所使用的锁分段技术。首先将数据分成一段一段地存
 储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数
 据也能被其他线程访问。
 6.1.2　ConcurrentHashMap的结构
 ConcurrentHashMap是由Segment数组结构和HashEntry数组结构组成。Segment是一种可重
 入锁（ReentrantLock），在ConcurrentHashMap里扮演锁的角色；HashEntry则用于存储键值对数
 据。一个ConcurrentHashMap里包含一个Segment数组。Segment的结构和HashMap类似，是一种
 数组和链表结构。一个Segment里包含一个HashEntry数组，每个HashEntry是一个链表结构的元
 素，每个Segment守护着一个HashEntry数组里的元素，当对HashEntry数组的数据进行修改时，
 必须首先获得与它对应的Segment锁
 6.1.3　ConcurrentHashMap的初始化
 ConcurrentHashMap初始化方法是通过initialCapacity、loadFactor和concurrencyLevel等几个
 参数来初始化segment数组、段偏移量segmentShift、段掩码segmentMask和每个segment里的
 HashEntry数组来实现的。
 6.1.4　定位Segment
 既然ConcurrentHashMap使用分段锁Segment来保护不同段的数据，那么在插入和获取元素
 的时候，必须先通过散列算法定位到Segment。可以看到ConcurrentHashMap会首先使用
 Wang/Jenkins hash的变种算法对元素的hashCode进行一次再散列。
 private static int hash(int h) {
 h += (h << 15) ^ 0xffffcd7d;
 h ^= (h >>> 10);
 h += (h << 3);
 h ^= (h >>> 6);
 h += (h << 2) + (h << 14);
 return h ^ (h >>> 16);
 }
 6.1.5　ConcurrentHashMap的操作
 1.get操作
 整个get过程不需要加锁，除非读到的值是空才会加锁重读
 原因是它的get方法里将要使用的共享变量都定义成volatile类型,
 定义成volatile的变量，能够在线
 程之间保持可见性，能够被多线程同时读，并且保证不会读到过期的值，但是只能被单线程写
 （有一种情况可以被多线程写，就是写入的值不依赖于原值），在get操作里只需要读不需要写
 共享变量count和value，所以可以不用加锁。之所以不会读到过期的值，是因为根据Java内存模
 型的happen before原则，对volatile字段的写入操作先于读操作，即使两个线程同时修改和获取
 volatile变量，get操作也能拿到最新的值，这是用volatile替换锁的经典应用场景。
 2.put操作
 3.size操作
 ConcurrentHashMap的做法是先尝试2次通过不锁住Segment的方式来统计各个Segment大小，如
 果统计的过程中，容器的count发生了变化，则再采用加锁的方式来统计所有Segment的大小。
 6.2　ConcurrentLinkedQueue
 ConcurrentLinkedQueue是一个基于链接节点的无界线程安全队列，它采用先进先出的规
 则对节点进行排序，当我们添加一个元素的时候，它会添加到队列的尾部；当我们获取一个元
 素时，它会返回队列头部的元素。它采用了“wait-free”算法（即CAS算法）来实现，该算法在
 Michael&Scott算法上进行了一些修改。
 6.2.1　ConcurrentLinkedQueue的结构
 ConcurrentLinkedQueue由head节点和tail节点组成，每个节点（Node）由节点元素（item）和
 指向下一个节点（next）的引用组成，节点与节点之间就是通过这个next关联起来，从而组成一
 张链表结构的队列。默认情况下head节点存储的元素为空，tail节点等于head节点。
 1.入队列的过程
 多线程下,如果有一个线程正在
 入队，那么它必须先获取尾节点，然后设置尾节点的下一个节点为入队节点，但这时可能有另
 外一个线程插队了，那么队列的尾节点就会发生变化，这时当前线程要暂停入队操作，然后重
 新获取尾节点。使用CAS算法将入队节点设置成尾节点的next节点，如不成功则重试。
 2.定位尾节点
 tail节点并不总是尾节点，所以每次入队都必须先通过tail节点来找到尾节点。
 这么设计的原因是减少cas更新tail节点的次数.
 6.2.3　出队列
 出队列的就是从队列里返回一个节点元素，并清空该节点对元素的引用。
 并不是每次出队时都更新head节点，当head节点里有元素时，直接弹出head
 节点里的元素，而不会更新head节点。只有当head节点里没有元素时，出队操作才会更新head
 节点。这种做法也是通过hops变量来减少使用CAS更新head节点的消耗，从而提高出队效率。
 6.3　Java中的阻塞队列\
 6.3.1　什么是阻塞队列
 阻塞队列（BlockingQueue）是一个支持两个附加操作的队列。这两个附加的操作支持阻塞
 的插入和移除方法。
 1）支持阻塞的插入方法：意思是当队列满时，队列会阻塞插入元素的线程，直到队列不
 满。
 2）支持阻塞的移除方法：意思是在队列为空时，获取元素的线程会等待队列变为非空。
 阻塞队列常用于生产者和消费者的场景，生产者是向队列里添加元素的线程，消费者是
 从队列里取元素的线程。阻塞队列就是生产者用来存放元素、消费者用来获取元素的容器。
 在阻塞队列不可用时，这两个附加操作提供了4种处理方式，如表6-1所示。
 表6-1　插入和移除操作的4中处理方式
 方法/处理方式 | 抛出异常 | 返回特殊值 | 一直阻塞 | 超时退出
 插入方法      | add(e)   | offer(e)   |  put(e)  | offer(e,time,unit)
 移除方法      | remove() | poll()     |  take()  | poll(time,unit)
 检查方法      | element()| peek()     |  不可用  | 不可用

 ·抛出异常：当队列满时，如果再往队列里插入元素，会抛出IllegalStateException（"Queue
 full"）异常。当队列空时，从队列里获取元素会抛出NoSuchElementException异常。
 ·返回特殊值：当往队列插入元素时，会返回元素是否插入成功，成功返回true。如果是移
 除方法，则是从队列里取出一个元素，如果没有则返回null。
 ·一直阻塞：当阻塞队列满时，如果生产者线程往队列里put元素，队列会一直阻塞生产者
 线程，直到队列可用或者响应中断退出。当队列空时，如果消费者线程从队列里take元素，队
 列会阻塞住消费者线程，直到队列不为空。
 ·超时退出：当阻塞队列满时，如果生产者线程往队列里插入元素，队列会阻塞生产者线程
 一段时间，如果超过了指定的时间，生产者线程就会退出。
 6.3.2　Java里的阻塞队列
 JDK 7提供了7个阻塞队列，如下。
 ·ArrayBlockingQueue：一个由数组结构组成的有界阻塞队列。
 ·LinkedBlockingQueue：一个由链表结构组成的有界阻塞队列。
 ·PriorityBlockingQueue：一个支持优先级排序的无界阻塞队列。
 ·DelayQueue：一个使用优先级队列实现的无界阻塞队列。
 ·SynchronousQueue：一个不存储元素的阻塞队列。
 ·LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
 ·LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。
 1.ArrayBlockingQueue
 ArrayBlockingQueue是一个用数组实现的有界阻塞队列。此队列按照先进先出（FIFO）的原
 则对元素进行排序。
 默认情况下不保证线程公平的访问队列，所谓公平访问队列是指阻塞的线程，可以按照
 阻塞的先后顺序访问队列，即先阻塞线程先访问队列。非公平性是对先等待的线程是非公平
 的，当队列可用时，阻塞的线程都可以争夺访问队列的资格，有可能先阻塞的线程最后才访问
 队列。为了保证公平性，通常会降低吞吐量。我们可以使用以下代码创建一个公平的阻塞队
 列。
 ArrayBlockingQueue fairQueue = new ArrayBlockingQueue(1000,true);
 访问者的公平性是使用可重入锁实现的
 2.LinkedBlockingQueue
 LinkedBlockingQueue是一个用链表实现的有界阻塞队列。此队列的默认和最大长度为
 Integer.MAX_VALUE。此队列按照先进先出的原则对元素进行排序。
 3.PriorityBlockingQueue
 PriorityBlockingQueue是一个支持优先级的无界阻塞队列。默认情况下元素采取自然顺序
 升序排列。也可以自定义类实现compareTo()方法来指定元素排序规则，或者初始化
 PriorityBlockingQueue时，指定构造参数Comparator来对元素进行排序。需要注意的是不能保证
 同优先级元素的顺序。
 4.DelayQueue
 DelayQueue是一个支持延时获取元素的无界阻塞队列。队列使用PriorityQueue来实现。队
 列中的元素必须实现Delayed接口，在创建元素时可以指定多久才能从队列中获取当前元素。
 只有在延迟期满时才能从队列中提取元素。
 5.SynchronousQueue
 SynchronousQueue是一个不存储元素的阻塞队列。每一个put操作必须等待一个take操作，
 否则不能继续添加元素。
 它支持公平访问队列。默认情况下线程采用非公平性策略访问队列。使用以下构造方法
 可以创建公平性访问的SynchronousQueue，如果设置为true，则等待的线程会采用先进先出的
 顺序访问队列。
 public SynchronousQueue(boolean fair) {
 transferer = fair new TransferQueue() : new TransferStack();
 }
 SynchronousQueue可以看成是一个传球手，负责把生产者线程处理的数据直接传递给消费
 者线程。队列本身并不存储任何元素，非常适合传递性场景。SynchronousQueue的吞吐量高于
 LinkedBlockingQueue和ArrayBlockingQueue。
 6.LinkedTransferQueue
 LinkedTransferQueue是一个由链表结构组成的无界阻塞TransferQueue队列。相对于其他阻
 塞队列，LinkedTransferQueue多了tryTransfer和transfer方法。
 （1）transfer方法
 如果当前有消费者正在等待接收元素（消费者使用take()方法或带时间限制的poll()方法
 时），transfer方法可以把生产者传入的元素立刻transfer（传输）给消费者。如果没有消费者在等
 待接收元素，transfer方法会将元素存放在队列的tail节点，并等到该元素被消费者消费了才返
 回。transfer方法的关键代码如下。
 Node pred = tryAppend(s, haveData);
 return awaitMatch(s, pred, e, (how == TIMED), nanos);
 第一行代码是试图把存放当前元素的s节点作为tail节点。第二行代码是让CPU自旋等待
 消费者消费元素。因为自旋会消耗CPU，所以自旋一定的次数后使用Thread.yield()方法来暂停
 当前正在执行的线程，并执行其他线程。
 （2）tryTransfer方法
 tryTransfer方法是用来试探生产者传入的元素是否能直接传给消费者。如果没有消费者等
 待接收元素，则返回false。和transfer方法的区别是tryTransfer方法无论消费者是否接收，方法
 立即返回，而transfer方法是必须等到消费者消费了才返回。
 对于带有时间限制的tryTransfer（E e，long timeout，TimeUnit unit）方法，试图把生产者传入
 的元素直接传给消费者，但是如果没有消费者消费该元素则等待指定的时间再返回，如果超
 时还没消费元素，则返回false，如果在超时时间内消费了元素，则返回true。
 7.LinkedBlockingDeque
 LinkedBlockingDeque是一个由链表结构组成的双向阻塞队列。所谓双向队列指的是可以
 从队列的两端插入和移出元素。双向队列因为多了一个操作队列的入口，在多线程同时入队
 时，也就减少了一半的竞争。相比其他的阻塞队列，LinkedBlockingDeque多了addFirst、
 addLast、offerFirst、offerLast、peekFirst和peekLast等方法，以First单词结尾的方法，表示插入、
 获取（peek）或移除双端队列的第一个元素。以Last单词结尾的方法，表示插入、获取或移除双
 端队列的最后一个元素。
 在初始化LinkedBlockingDeque时可以设置容量防止其过度膨胀。另外，双向阻塞队列可以
 运用在“工作窃取”模式中。
 6.3.3　阻塞队列的实现原理
 如果队列是空的，消费者会一直等待，当生产者添加元素时，消费者是如何知道当前队列
 有元素的呢？
 使用通知模式实现。所谓通知模式，就是当生产者往满的队列里添加元素时会阻塞住生
 产者，当消费者消费了一个队列中的元素后，会通知生产者当前队列可用。通过查看JDK源码
 发现ArrayBlockingQueue使用了Condition来实现，
 继续进入源码，发现调用setBlocker先保存一下将要阻塞的线程，然后调用unsafe.park阻塞
 当前线程。unsafe.park本地方法参看书
 当线程被阻塞队列阻塞时，线程会进入WAITING（parking）状态。我们可以使用jstack dump
 阻塞的生产者线程看到这点，如下。
 6.4　Fork/Join框架
 6.4.1　什么是Fork/Join框架
 Fork/Join框架是Java 7提供的一个用于并行执行任务的框架，是一个把大任务分割成若干
 个小任务，最终汇总每个小任务结果后得到大任务结果的框架。
 我们再通过Fork和Join这两个单词来理解一下Fork/Join框架。Fork就是把一个大任务切分
 为若干子任务并行的执行，Join就是合并这些子任务的执行结果，最后得到这个大任务的结
 果。比如计算1+2+…+10000，可以分割成10个子任务，每个子任务分别对1000个数进行求和，
 最终汇总这10个子任务的结果。Fork/Join的运行流程如图6-6所示。
 6.4.2　工作窃取算法
 工作窃取（work-stealing）算法是指某个线程从其他队列里窃取任务来执行。那么，为什么
 需要使用工作窃取算法呢？假如我们需要做一个比较大的任务，可以把这个任务分割为若干
 互不依赖的子任务，为了减少线程间的竞争，把这些子任务分别放到不同的队列里，并为每个
 队列创建一个单独的线程来执行队列里的任务，线程和队列一一对应。比如A线程负责处理A
 队列里的任务。但是，有的线程会先把自己队列里的任务干完，而其他线程对应的队列里还有
 任务等待处理。干完活的线程与其等着，不如去帮其他线程干活，于是它就去其他线程的队列
 里窃取一个任务来执行。而在这时它们会访问同一个队列，所以为了减少窃取任务线程和被
 窃取任务线程之间的竞争，通常会使用双端队列，被窃取任务线程永远从双端队列的头部拿
 任务执行，而窃取任务的线程永远从双端队列的尾部拿任务执行。
 Fork/Join框架也使用了工作窃取算法
 6.4.3
 ForkJoinTask代表任务,子类:·RecursiveAction：用于没有返回结果的任务。
 ·RecursiveTask：用于有返回结果的任务。
 ForkJoinPool：ForkJoinTask需要通过ForkJoinPool来执行。
 ForkJoinTask.isCompletedAbnormally()方法来检查任务是否已经抛出异常或已经被取消了
 ForkJoinTask的getException方法获取异常
 */
