import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *ThreadLocal:线程独享变量
 *
 */

/*
1.Thread内部有一个ThreadLocal.ThreadLocalMap，key值是ThreadLocal，value值是指定的变量值；

2.ThreadLocalMap内部有一个Entry数组，用来存储K-V值，之所以是数组，而不是一个Entry，是因为一个线程可能对应有多个ThreadLocal；

3.ThreadLocal对象在线程外生成，多线程共享一个ThreadLocal对象，生成时需指定数据类型，每个ThreadLocal对象都自定义了不同的threadLocalHashCode；

4.ThreadLocal.set 首先根据当前线程Thread找到对应的ThreadLocalMap，然后将ThreadLocal的threadLocalHashCode转换为ThreadLocalMap里的Entry数组下标，并存放数据于Entry[]中；

5.ThreadLocal.get 首先根据当前线程Thread找到对应的ThreadLocalMap，然后将ThreadLocal的threadLocalHashCode转换为ThreadLocalMap里的Entry数组下标，根据下标从Entry[]中取出对应的数据；

6.由于Thread内部的ThreadLocal.ThreadLocalMap对象是每个线程私有的，所以做到了数据独立。
 */


/*
但是问题来了，如果线程数很多，一直往ThreadLocalMap中存值，那内存岂不是要撑死了？

当然不是，设计者使用了弱引用来解决这个问题:
static class Entry extends WeakReference<ThreadLocal<?>> {
 Object value;
 Entry(ThreadLocal<?> k, Object v) {
 super(k);
 value = v;
 }
}

不过这里的弱引用只是针对key。每个key都弱引用指向ThreadLocal。

当把ThreadLocal实例置为null以后，没有任何强引用指向ThreadLocal实例，所以ThreadLocal将会被GC回收。

然而，value不能被回收，因为当前线程存在对value的强引用。只有当前线程结束销毁后，强引用断开，所有值才将全部被GC回收。

由此可推断出，只有这个线程被回收了，value才会真正被回收。

如果我们使用线程池，常驻线程不会被销毁。这就完蛋了，value永远无法被GC回收，造成内存泄漏那是必然的。

而我们的请求进入到系统时，并不是一个请求生成一个线程，而是请求先进入到线程池，再由线程池调配出一个线程进行执行，执行完毕后放回线程池。

这样就会存在一个线程多次被复用的情况，这就产生了这个线程此次操作中获取到了上次操作的值。

解决办法就是每次使用完ThreadLocal对象后，都要调用其remove方法，清除ThreadLocal中的内容。
 */
public class ThreadLocalTest {
    static ThreadLocal<AtomicInteger> sequencer = ThreadLocal.withInitial ( () -> new AtomicInteger ( 0 ) );
    static class Task implements Runnable {
        @Override
        public void run() {
            int value = sequencer.get ().getAndIncrement ();
            System.out.println ( Thread.currentThread ().getName () + ";" + value);
            sequencer.remove (); //重点在此
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool ( 2 );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.execute ( new Task () );
        executorService.shutdown ();
    }
}
