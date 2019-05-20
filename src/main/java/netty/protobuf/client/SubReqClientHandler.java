package netty.protobuf.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.pb.SubscribeReqProto;

/**
 *
 */
public class SubReqClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 1; i <= 10; i++) {
            ctx.write ( subReq(i) );
        }
        ctx.flush ();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println ( "Client receive resp: " + msg  );
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush ();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace ();
        ctx.close ();
    }

    private SubscribeReqProto.SubscribeReq subReq(int subReqId) {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder ();
        builder.setSubReqId ( subReqId );
        builder.setAddress ( "WeiSheng Building ShenNan Road No.xxoo Nanshan District Shenzhen Guangdong China" );
        builder.setUserName ( "QiaoJian" );
        builder.setProductname ( "Netty in action" );
        return builder.build ();
    }
}
