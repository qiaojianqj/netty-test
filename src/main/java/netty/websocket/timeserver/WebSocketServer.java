package netty.websocket.timeserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 */
public class WebSocketServer {
    private void bind(int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup ( );
        EventLoopGroup worker = new NioEventLoopGroup ( );
        try {
            ServerBootstrap b = new ServerBootstrap ();
            b.group ( boss, worker )
                    .channel ( NioServerSocketChannel.class )
                    .childHandler ( new ChannelInitializer<SocketChannel> () {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline ().addLast ( "http-codec", new HttpServerCodec (  ) );
                            ch.pipeline ().addLast ( "aggregator", new HttpObjectAggregator ( 65535 ) );
                            ch.pipeline ().addLast ( "http-chunked", new ChunkedWriteHandler (  ) );
                            ch.pipeline ().addLast ( "handler", new WebSocketServerHandler() );
                        }
                    } );
            ChannelFuture f = b.bind ( port ).sync ();
            f.channel ().closeFuture ().sync ();
        } finally {
            boss.shutdownGracefully ();
            worker.shutdownGracefully ();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf ( args[0] );
            } catch (Exception e) {

            }
        }

        new WebSocketServer ().bind(port);
    }
}
