package com.febfes.fftmback.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class MyHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        log.info("session with message: {}", new String(message.asBytes()));
    }

}
