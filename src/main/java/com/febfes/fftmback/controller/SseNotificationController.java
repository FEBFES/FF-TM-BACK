package com.febfes.fftmback.controller;

import com.febfes.fftmback.controller.data.SubscriptionData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
@Tag(name = "SSE Notification")
@RequestMapping("v1/sse")
public class SseNotificationController {

    private final Map<UUID, SubscriptionData> subscriptions = new ConcurrentHashMap<>();

    @GetMapping(path = "/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> openSseStream(@PathVariable String username) {

        return Flux.create(fluxSink -> {
            log.info("Creating SSE subscription for user {}", username);

            UUID uuid = UUID.randomUUID();

            fluxSink.onCancel(
                    () -> {
                        subscriptions.remove(uuid);
                        log.info("SSE Subscription for user {} was closed", username);
                    }
            );

            SubscriptionData subscriptionData = new SubscriptionData(username, fluxSink);
            subscriptions.put(uuid, subscriptionData);

            ServerSentEvent<String> helloEvent = ServerSentEvent.builder("Hello, " + username).build();
            fluxSink.next(helloEvent);
        });
    }

    @GetMapping(path = "/{username}/test")
    public void test(@PathVariable String username) {
        sendMessageToTheUser("TEST MESSAGE", username);
    }

    public void sendMessageToTheUser(String message, String username) {
        ServerSentEvent<String> event = ServerSentEvent
                .builder(message)
                .build();

        subscriptions.entrySet().removeIf(entry -> {
            SubscriptionData subscriptionData = entry.getValue();
            if (username.equals(subscriptionData.getUsername())) {
                try {
                    subscriptionData.getFluxSink().next(event);
                } catch (Exception e) {
                    log.warn("SSE connection lost: {}", e.getMessage());
                    return true;
                }
            }
            return false;
        });
    }
}
