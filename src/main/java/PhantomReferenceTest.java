import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 *
 */
public class PhantomReferenceTest {
    public static void main(String[] args) {
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<> ();
        PhantomReference<Object> phantomReference = new PhantomReference<> ( new Object (), referenceQueue );

        System.out.println ( "Before GC" );
        System.out.println ( "phantom reference " + phantomReference + " get: " + phantomReference.get () );
        System.out.println ( "reference queue poll: " + referenceQueue.poll () );

        System.gc ();
        //DirectByteBuffer中是用虚引用的子类Cleaner.java来实现堆外内存回收的
        //

        System.out.println ( "After GC" );
        System.out.println ( "phantom reference " + phantomReference + " get: " + phantomReference.get () );
        System.out.println ( "reference queue poll: " + referenceQueue.poll () );
    }
}
