package com.febfes.fftmback.controller;

import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

//@ServerEndpoint(value = "/test-ws", configurator = SpringConfigurator.class)
@ServerEndpoint(value = "/test-ws") // SHOULD BE SINGLETON
@Slf4j
public class TestWSController {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        log.info("OPENED");
        session.getBasicRemote().sendText("RECEIVED!!!");
    }

//    @OnMessage
//    public void onMessage(Session session, Object message) throws IOException {
//        log.info("MESSAGE: {}", message);
//        session.getBasicRemote().sendText("MESSAGE RECEIVED!!!");
//    }
}
