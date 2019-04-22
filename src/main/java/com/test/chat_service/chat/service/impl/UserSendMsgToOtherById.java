package com.test.chat_service.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.chat_service.chat.ResponseEnum;
import com.test.chat_service.chat.SimpleServiceEnum;
import com.test.chat_service.chat.bean.ServerPushModel;
import com.test.chat_service.chat.cache.SimpleServiceImplCache;
import com.test.chat_service.chat.cache.UserChannelCache;
import com.test.chat_service.chat.service.SimpleService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

@Service
public class UserSendMsgToOtherById implements SimpleService {

    public UserSendMsgToOtherById(){
        SimpleServiceImplCache.simpleServiceMap.put(SimpleServiceEnum.USER_SEND_MSG_TO_OTHER_BY_ID.getServiceName(),this);
    }

    @Override
    public void execute(ChannelHandlerContext ctx, JSONObject params) {

        if(params == null){
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.INVALID_REQUEST.getCode());
                setMsg(ResponseEnum.INVALID_REQUEST.getMsg());
            }})));
            return;
        }

        //解析参数
        Params params1 = null;
        try {
            params1 = JSON.toJavaObject(params, Params.class);
        }catch (Exception e){
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.INVALID_PARAMS.getCode());
                setMsg(ResponseEnum.INVALID_PARAMS.getMsg());
            }})));
            return;
        }

        //接收方的channel上下文
        ChannelHandlerContext targetCtx = UserChannelCache.getCtxFromCacheByUserId(params1.getReceiver());

        if(targetCtx == null){
            //接收方未登录，不能发送  (如果想做离线消息后面再改)
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(new ServerPushModel(){{
                setCode(ResponseEnum.NO_SUCH_RECEIVER.getCode());
                setMsg(ResponseEnum.NO_SUCH_RECEIVER.getMsg());
            }})));
        }else{

            ServerPushModel dataToReceiver = new ServerPushModel();

            dataToReceiver.setCode(ResponseEnum.OK.getCode());
            dataToReceiver.setMsg(ResponseEnum.OK.getMsg());

            //组装data

            long ts = System.currentTimeMillis();

            Data data = new Data();
            data.setFromUser(UserChannelCache.getUserIdFromCacheByChannel(ctx));
            data.setMessage(params1.getMessage());
            data.setTimestamp(ts);

            dataToReceiver.setData(data);

            //接收方channel推送
            targetCtx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataToReceiver)));

            //实际情况要更为复杂一些 需要给发送方一条发送成功的回执信息，而且数据来往格式可能跟这个demo的模型不太一样 需要全局规范一下

        }

    }

    private static final class Params{
        private int receiver;
        private String message;

        public int getReceiver() {
            return receiver;
        }

        public void setReceiver(int receiver) {
            this.receiver = receiver;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private static final class Data{
        private int fromUser;
        private String message;
        private long timestamp;

        public int getFromUser() {
            return fromUser;
        }

        public void setFromUser(int fromUser) {
            this.fromUser = fromUser;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
