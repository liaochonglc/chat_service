package com.test.chat_service.chat.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;

public interface SimpleService {

    public void execute(ChannelHandlerContext ctx, JSONObject params);

}
