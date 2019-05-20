import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 *
 */
public class WeakReferenceTest {
    public static void main(String[] args) {
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<> ();
        WeakReference<Object> weakReference = new WeakReference<> ( new Object (), referenceQueue );

        System.out.println ( "Before GC" );
        System.out.println ( "weak reference " + weakReference +  " get: " + weakReference.get () );
        System.out.println ( "reference queue poll: " + referenceQueue.poll () );

        System.gc ();
        //JVM在对obj进行了回收之后，将weakReference插入到referenceQueue队列中

        System.out.println ( "After GC" );
        System.out.println ( "weak reference " + weakReference + " get: " + weakReference.get () );
        System.out.println ( "reference queue poll: " + referenceQueue.poll () );

        //字符串常量池作用
        String a1 = "a str";
        String a2 = "a str";
        String aNew1 = new String ( "a str" ); //仅仅内部char[]指向和a1一样的字符串常量池对象
        String aNew2 = new String ( a1 );
        //String a3 = aNew1.intern ();
        String a3 = aNew2.intern (); //返回字符串常量池中的对象
        String a4 = a1 + 1;
        System.out.println ( a1 == a2 );
        System.out.println ( a1 == a3 );
        System.out.println ( a1 == aNew1 );
        System.out.println ( a1 == aNew2 );
        System.out.println ( a1 == a4);
    }

}
