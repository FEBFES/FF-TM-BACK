package com.febfes.fftmback.controller.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionData {

    private String username;

    private FluxSink<ServerSentEvent<?>> fluxSink;
}
