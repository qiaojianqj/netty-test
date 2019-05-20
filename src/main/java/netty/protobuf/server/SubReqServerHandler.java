package netty.protobuf.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.pb.SubscribeReqProto;
import netty.protobuf.pb.SubscribeRespProto;

/**
 *
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        System.out.println ( "Server receive subscribe req: " + req.toString ());
        ctx.writeAndFlush ( resp(req.getSubReqId ()) );
    }

    private SubscribeRespProto.SubscribeResp resp(int subReqId) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder ();
        builder.setSubReqId ( subReqId );
        builder.setRespCode ( 0 );
        builder.setDesc ( "Order succeed!" );
        return builder.build ();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace ();
        ctx.close ();
    }
}
