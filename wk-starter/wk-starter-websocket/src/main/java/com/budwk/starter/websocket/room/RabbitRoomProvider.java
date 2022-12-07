package com.budwk.starter.websocket.room;

import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

import java.util.Set;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class RabbitRoomProvider implements WsRoomProvider {

    @Override
    public Set<String> wsids(String s) {
        return null;
    }

    @Override
    public void join(String s, String s1) {

    }

    @Override
    public void left(String s, String s1) {

    }
}
