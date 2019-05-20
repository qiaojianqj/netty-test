package netty.tcp_stick_package.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 *
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes ()];
        buf.readBytes ( req );
        String body = new String ( req );
        System.out.println ( "TimeServer receive order: " + body + "\nCounter is: " + ++counter );
        String currentTime = "QUERY TIME ORDER\n".equalsIgnoreCase ( body ) ?
                new Date ( System.currentTimeMillis () ).toString () :
                "BAD ORDER";
        //currentTime = currentTime + "\n";
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
