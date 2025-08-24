package com.simple.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.simpleLib.dto.ExtendedEventDto;
import com.simple.simpleLib.dto.SimpleEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    private final WebClient partnerWebClient;
    private final WebClient coreWebClient;
    @Autowired
    ObjectMapper objectMapper;

    public ApiService(WebClient.Builder builder,
                      @Value("${partner.module.host}") String partnerHost,
                      @Value("${partner.module.port}") String partnerPort,
                      @Value("${core.module.host}") String coreHost,
                      @Value("${core.module.port}") String corePort) {
        this.partnerWebClient = builder.baseUrl("http://" + partnerHost + ":" + partnerPort).build();
        this.coreWebClient = builder.baseUrl("http://" + coreHost + ":" + corePort).build();
    }

    public Mono<ExtendedEventDto> getEvents() {
        return partnerWebClient.get()
                .uri("/getEvents")
                .retrieve()
                .bodyToMono(ExtendedEventDto.class);

    }

    public Mono<SimpleEventDto> getEventById(Long eventId) {
        return partnerWebClient.get()
                .uri("/getEvent/{id}", eventId)
                .retrieve()
                .bodyToMono(SimpleEventDto.class);
    }

    public Long pay(Long eventId, Long seatId, Long cardId, String userToken) {
        validateUserToken(cardId, userToken);

        return 1L;
    }

    private void validateUserToken(Long cardId, String userToken) {
        coreWebClient.get()
                .uri("/validate/{userToken}/{cardId}", userToken, cardId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
