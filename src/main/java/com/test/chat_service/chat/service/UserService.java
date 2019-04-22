package com.test.chat_service.chat.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface UserService {

    public void login(FullHttpRequest req, ChannelHandlerContext ctx)  throws Exception;

    public void logout(ChannelHandlerContext ctx)  throws Exception;

}
