package netty.tcp_stick_package_resolve.timeclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 *
 */
public class TimeClient {
    private void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup (  );
        try {
            Bootstrap b = new Bootstrap ();
            b.group ( group )
                    .channel ( NioSocketChannel.class )
                    .option ( ChannelOption.TCP_NODELAY, true )
                    .handler ( new ChannelInitializer<SocketChannel> () {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            //以换行分隔符来拆接收到到的TCP包
                            ch.pipeline ().addLast ( new LineBasedFrameDecoder ( 1024 ) );
                            ch.pipeline ().addLast ( new StringDecoder (  ) );
                            ch.pipeline ().addLast ( new TimeClientHandler () );
                        }
                    } );
            ChannelFuture f = b.connect (host, port).sync ();
            f.channel ().closeFuture ().sync ();
        } finally {
            group.shutdownGracefully ();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 19999;
        new TimeClient ().connect(port, "localhost");
    }
}
