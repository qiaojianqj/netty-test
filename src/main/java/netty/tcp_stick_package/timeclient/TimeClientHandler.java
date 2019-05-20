package netty.tcp_stick_package.timeclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private byte[] req;
    private int counter;

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
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes ()];
        buf.readBytes ( req );
        String body = new String(req);
        System.out.println ( "Now is: " + body + "\n Counter is: " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close ();
    }
}
