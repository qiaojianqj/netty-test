package aio.timeclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class AsyncTimeClientHandler implements Runnable, CompletionHandler<Void, AsyncTimeClientHandler> {
    private String host;
    private int port;
    private AsynchronousSocketChannel client;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.client = AsynchronousSocketChannel.open ();
        } catch (IOException e) {
            e.printStackTrace ();
            System.exit ( 1 );
        }
    }
    @Override
    public void run() {
        latch = new CountDownLatch ( 1 );
        this.client.connect ( new InetSocketAddress ( this.host, this.port ),  this, this );
        try {
            latch.await ();
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes ();
        ByteBuffer writeBuffer = ByteBuffer.allocate ( req.length );
        writeBuffer.put ( req );
        writeBuffer.flip ();
        this.client.write ( writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer> () {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining ()) {
                    client.write ( attachment, attachment, this );
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate ( 1024 );
                    client.read ( readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer> () {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip ();
                            byte[] bytes = new byte[attachment.remaining ()];
                            attachment.get ( bytes );
                            String body = null;
                            try {
                                body = new String(bytes, "utf-8");
                                System.out.println ( "Now is " + body );
                                latch.countDown ();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace ();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                client.close ();
                            } catch (IOException e) {

                            }
                        }
                    } );
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    client.close ();
                } catch (IOException e) {

                }
            }
        } );
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace ();
        try {
            client.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
