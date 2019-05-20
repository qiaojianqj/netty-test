package netty.websocket.timeserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;

/**
 *
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush ();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.getDecoderResult ().isSuccess () || (!"websocket".equals ( req.headers ().get ( "Upgrade" ) ))) {
           sendHttpResponse(ctx, req, new DefaultFullHttpResponse ( HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST ));
           return;
        }
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory ( "ws://localhost:8080/websocket", null, false );
        handshaker = handshakerFactory.newHandshaker ( req );
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse ( ctx.channel () );
        } else {
            handshaker.handshake ( ctx.channel (), req );
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest request, FullHttpResponse response) {
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            setContentLength(response, response.content().readableBytes());
        }
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (!isKeepAlive(request) || response.status().code() != 200) {
            channelFuture.addListener( ChannelFutureListener.CLOSE);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close ( ctx.channel (), (CloseWebSocketFrame) frame.retain () );
            return;
        }

        if (frame instanceof PingWebSocketFrame) {
            //目前还没有浏览器或JS自带的ping-pong机制
            System.out.println ( "receive ping frame: " + frame.content () );
            ctx.channel ().write ( new PongWebSocketFrame ( frame.content ().retain () ) );
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException ( String.format ( "%s frame types not supported", frame.getClass ().getName () ) );
        }

        String req = ((TextWebSocketFrame) frame).text ();
        System.out.println ( "Server " + ctx.channel () + " receive " + req );
        ctx.channel ().write ( new TextWebSocketFrame ( req + ", Welcome, Now Time is: " + new Date ().toString () ) );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace ();
        ctx.close ();
    }
}
