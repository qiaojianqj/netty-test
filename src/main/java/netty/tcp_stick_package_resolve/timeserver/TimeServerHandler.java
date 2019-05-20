package netty.tcp_stick_package_resolve.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

/**
 *
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    private int counter;
    private static Set<Channel> channels = new HashSet<Channel> ( );

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println ( "channelActive: " + Thread.currentThread ().getName () );
        //channels.add (ctx.channel () );
        //    TableFrame tableFrame = new TableFrame ();
        //    tableFrame.setChannelHandlerContext ( ctx );
        //    tableFrame.SetGameTimerWhileTrue ();
        //    tableFrame.SetGameTimer (  + 10, 1 * 1000 + 1000, 0xffff, 0 );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = (String)msg;
        System.out.println ( "TimeServer receive order: " + body + "\nCounter is: " + ++counter );
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase ( body ) ?
                new Date ( System.currentTimeMillis () ).toString () :
                "BAD ORDER";
        currentTime = currentTime + "\n";
        ByteBuf resp = Unpooled.copiedBuffer ( currentTime.getBytes () );
        ctx.write ( resp );
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush ();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close ();
    }
}
