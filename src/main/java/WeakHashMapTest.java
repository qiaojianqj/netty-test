import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 *
 */
public class WeakHashMapTest {

    //1. WeakHashMap - key 是弱引用，value是强引用
    //所以在gc的时候只有key会被回收，value不会被回收
    //2. 在gc的时候，key被回收，然后将弱引用本身(Entry)放入ReferenceQueue中去
    //3. 在WeakHashMap的大多数操作（put，get，size等）中调用expungeStaleEntries，会去ReferenceQueue队列中取弱引用，
    //然后将对应的Entry.value 回收掉（置为null），因此WeakHashMap的自动内存回收是依赖于后续对map的操作的。
    //如果，只是new一个WeakHashMap，put值后，再也不用，则不会触发WeakHashMap的自动内存回收机制

    public static void main(String[] args) {
        List<WeakHashMap<byte[][], Object>> list = new ArrayList<> ();
        for (int i = 0; i < 1000; i++) {
            WeakHashMap<byte[][], Object> d = new WeakHashMap<>();
            //d.put(new byte[10000][10000], new Object ());
            d.put(new byte[10000][10000], new Long[10000][10000]);
            list.add(d);
            System.gc(); //仅仅提醒JVM进行GC，JVM进行尽力而为的GC，并不定马上就会进行GC
            System.out.println(i);
            //添加以下代码，才能触发WeakHashMap的自动内存回收机制(调用expungeStaleEntries)
            for (int j = 0; j < i; j++) {
                list.get ( j ).size ();
            }
        }
    }

}
