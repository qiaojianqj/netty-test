package netty.tcp_stick_package_resolve.timeclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private byte[] req;
    private int counter;
    private Set<Channel> channels = new HashSet<Channel> ( );

    public TimeClientHandler() {
        req = "QUERY TIME ORDER\n".getBytes ();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = null;
        for (int i = 0; i < 100; i++) {
            msg = Unpooled.buffer ( req.length );
            msg.writeBytes ( req );
            ctx.writeAndFlush ( msg );
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channels.add ( ctx.channel () );
        String body = (String)msg;
        System.out.println ( "Now is: " + body + "\n Counter is: " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close ();
    }
}
