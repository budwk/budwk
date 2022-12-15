package com.budwk.starter.websocket;

import java.util.Set;

public interface WsRoomProvider {

    // room = RedisConstant.WS_TOKEN + userId + ":" + token
    Set<String> wsids(String room);

    void join(String userId, String token, String wsid);

    void left(String userId, String token, String wsid);
}
