package com.test.chat_service.chat.server;

import com.alibaba.fastjson.JSON;
import com.test.chat_service.chat.ResponseEnum;
import com.test.chat_service.chat.bean.ServerPushModel;
import com.test.chat_service.chat.bean.SimpleServiceMsgModel;
import com.test.chat_service.chat.cache.SimpleServiceImplCache;
import com.test.chat_service.chat.service.SimpleService;
import com.test.chat_service.chat.service.UserService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Autowired
    private UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {

        if(o instanceof FullHttpRequest){
            //握手阶段  传统http协议
            dealHandshake(ctx, (FullHttpRequest) o);
        }else if(o instanceof TextWebSocketFrame){
            //文本消息处理
            dealTextWebSocketFrame(ctx, (TextWebSocketFrame) o);
        }else if(o instanceof PingWebSocketFrame){
            //心跳消息处理

        }else if(o instanceof CloseWebSocketFrame){
            //断开处理
            try {
                dealCloseWebSocketFrame(ctx);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    //握手处理
    private void dealHandshake(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{

        // 如果HTTP解码失败，返回HHTP异常
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
                    BAD_REQUEST));
            return;
        }

//        // 构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
            userService.login(req, ctx);
        }

    }

    //握手处理返回httpResponse
    private void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, FullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    //处理文本消息
    private void dealTextWebSocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame frame){

        SimpleServiceMsgModel simpleServiceMsgModel = null;
        try {
            simpleServiceMsgModel = JSON.parseObject(frame.text(),SimpleServiceMsgModel.class);
        }catch (Exception e){
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.INVALID_REQUEST.getCode());
                setMsg(ResponseEnum.INVALID_REQUEST.getMsg());
            }})));
            return;
        }

        SimpleService simpleService = SimpleServiceImplCache.simpleServiceMap.get(simpleServiceMsgModel.getServiceName());

        if(simpleService == null){
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.INVALID_SERVICE.getCode());
                setMsg(ResponseEnum.INVALID_SERVICE.getMsg());
            }})));
        }else{
            simpleService.execute(ctx,simpleServiceMsgModel.getParams());
        }
    }

    //处理主动断开连接
    private void dealCloseWebSocketFrame(ChannelHandlerContext ctx) throws Exception{
        userService.logout(ctx);
    }

}
