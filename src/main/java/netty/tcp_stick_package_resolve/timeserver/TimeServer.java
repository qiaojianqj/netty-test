package netty.tcp_stick_package_resolve.timeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 *
 */
public class TimeServer {
    private void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup (  );
        EventLoopGroup workGroup = new NioEventLoopGroup ( 16 );
        try {
            ServerBootstrap b = new ServerBootstrap ();
            b.group ( bossGroup, workGroup )
                    .channel ( NioServerSocketChannel.class )
                    .option ( ChannelOption.SO_BACKLOG, 1024 )
                    .childHandler ( new ChannelInitializer<SocketChannel> () {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //以换行分隔符来拆接收到到的TCP包
                            ch.pipeline ().addLast ( new LineBasedFrameDecoder ( 1024 ) );
                            ch.pipeline ().addLast ( new StringDecoder (  ) );
                            ch.pipeline ().addLast ( new TimeServerHandler() );
                        }
                    } );
            ChannelFuture f = b.bind (port).sync ();
            f.channel ().closeFuture ().sync ();
        } finally {
            bossGroup.shutdownGracefully ();
            workGroup.shutdownGracefully ();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 19999;
        new TimeServer ().bind(port);
    }
}
