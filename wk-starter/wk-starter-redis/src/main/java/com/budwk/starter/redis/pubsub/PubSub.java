package com.budwk.starter.redis.pubsub;

public interface PubSub {
    void onMessage(String channel, String message);
}
