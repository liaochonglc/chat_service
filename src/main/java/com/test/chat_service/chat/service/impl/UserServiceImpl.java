package com.test.chat_service.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.test.chat_service.chat.ResponseEnum;
import com.test.chat_service.chat.bean.ServerPushModel;
import com.test.chat_service.chat.bean.User;
import com.test.chat_service.chat.cache.UserChannelCache;
import com.test.chat_service.chat.cache.UserDatas;
import com.test.chat_service.chat.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public void login(FullHttpRequest req, ChannelHandlerContext ctx) throws Exception{

        Map<String,String> params = parseHttpParams(req);

        String username = params.get("username");
        String password = params.get("password");

        User user = null;

        //模拟查询数据库用户表
        for(User u:UserDatas.userList){
            if(u.getUsername().equals(username) && u.getPassword().equals(password)){
                user = u;
                break;
            }
        }

        if(user != null){
            //保存channel上下文 返回登录成功
            UserChannelCache.addCtxToCache(user.getUserId(), ctx);
            UserChannelCache.addCtxToUserIdCache(user.getUserId(), ctx);

            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.OK.getCode());
                setMsg(ResponseEnum.OK.getMsg());
            }})));
        }else{
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.LOGIN_FAIL.getCode());
                setMsg(ResponseEnum.LOGIN_FAIL.getMsg());
            }})));
            ctx.close();
        }

    }

    //握手参数解析(注：正常情况下应该只支持post的，不开放get)
    private Map<String, String> parseHttpParams(FullHttpRequest req) throws Exception {
        HttpMethod method = req.method();

        Map<String, String> parmMap = new HashMap<>();

        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
        } else if (HttpMethod.POST == method) {
            // 是POST请求
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
            decoder.offer(req);

            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

            for (InterfaceHttpData parm : parmList) {

                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());
            }

        } else {
            // 不支持其它方法
//            throw new MethodNotSupportedException(""); // 这是个自定义的异常, 可删掉这一行
            throw new Exception("MethodNotSupported");
        }

        return parmMap;
    }


    @Override
    public void logout(ChannelHandlerContext ctx) throws Exception {

        Integer userId = UserChannelCache.getUserIdFromCacheByChannel(ctx);

        UserChannelCache.removeCtxFromCache(userId);
        UserChannelCache.removeCtxToUserIdCache(ctx);

        ctx.close().sync();

    }
}
