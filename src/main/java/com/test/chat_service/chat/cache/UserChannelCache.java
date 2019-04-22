package com.test.chat_service.chat.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户channel上下文池
 */
public class UserChannelCache {

    private static Map<Integer, ChannelHandlerContext> CTX_MAP_BY_USERID = new HashMap<>();

    private static Map<String, Integer> CTX_TO_USERID_MAP = new HashMap<>();

    //建立(userId -> channel控制器上下文)的键值对
    public static void addCtxToCache(int userId,ChannelHandlerContext ctx){
        CTX_MAP_BY_USERID.put(userId,ctx);
    }

    //建立(channelId -> userId)的键值对
    public static void addCtxToUserIdCache(int userId,ChannelHandlerContext ctx){
        CTX_TO_USERID_MAP.put(ctx.channel().id().toString(),userId);
    }

    //删除(userId -> channel控制器上下文)的键值对
    public static void removeCtxFromCache(int userId){
        CTX_MAP_BY_USERID.remove(userId);
    }

    //删除(channelId -> userId)的键值对
    public static void removeCtxToUserIdCache(ChannelHandlerContext ctx){
        CTX_TO_USERID_MAP.remove(ctx.channel().id().toString());
    }

    //通过userId获取channel控制器上下文
    public static ChannelHandlerContext getCtxFromCacheByUserId(int userId){
        return CTX_MAP_BY_USERID.get(userId);
    }

    //通过channel获取userId
    public static Integer getUserIdFromCacheByChannel(ChannelHandlerContext ctx){
        return CTX_TO_USERID_MAP.get(ctx.channel().id().toString());
    }

}
