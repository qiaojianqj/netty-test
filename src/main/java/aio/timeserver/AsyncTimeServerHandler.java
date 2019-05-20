package aio.timeserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class AsyncTimeServerHandler implements Runnable {
    private int port;
    public CountDownLatch latch;
    public AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open ();
            asynchronousServerSocketChannel.bind ( new InetSocketAddress ( port ) );
            System.out.println ( "Time Server start on port " + port);
        } catch (IOException e) {
            e.printStackTrace ();
            System.exit ( 1 );
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch ( 1 );
        doAccept();
        try {
            latch.await ();
        } catch (InterruptedException e1) {
            e1.printStackTrace ();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept (this, new AcceptCompletionHandler());
    }
}
