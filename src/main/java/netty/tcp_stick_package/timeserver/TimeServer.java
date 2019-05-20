package netty.tcp_stick_package.timeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 *
 */
public class TimeServer {
    private void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup (  );
        EventLoopGroup workGroup = new NioEventLoopGroup (  );
        try {
            ServerBootstrap b = new ServerBootstrap ();
            b.group ( bossGroup, workGroup )
                    .channel ( NioServerSocketChannel.class )
                    .option ( ChannelOption.SO_BACKLOG, 1024 )
                    .childHandler ( new ChannelInitializer<SocketChannel> () {
                        @Override
                        protected void initChannel(SocketChannel ch) {
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
