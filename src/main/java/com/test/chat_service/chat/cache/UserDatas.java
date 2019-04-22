package com.test.chat_service.chat.cache;

import com.test.chat_service.chat.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里模拟数据库用户表
 */
public class UserDatas {

    public static List<User> userList = new ArrayList<User>(){{

        add(new User(){{
            setUserId(1);
            setUsername("user1");
            setPassword("123456");
        }});

        add(new User(){{
            setUserId(2);
            setUsername("user2");
            setPassword("123456");
        }});

        add(new User(){{
            setUserId(3);
            setUsername("user3");
            setPassword("123456");
        }});

    }};

}
