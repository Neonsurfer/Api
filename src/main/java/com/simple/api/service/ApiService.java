package com.simple.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.api.dto.ExtendedEventDto;
import com.simple.api.dto.SimpleEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {

    private final WebClient webClient;
    @Autowired
    ObjectMapper objectMapper;

    public ApiService(WebClient.Builder builder, @Value("${partner.module.url}") String partnerUrl) {
        this.webClient = builder.baseUrl("partnerUrl").build();
    }

    public Mono<ExtendedEventDto> getEvents() {
        return webClient.get()
                .uri("/getEvents")
                .retrieve()
                .bodyToMono(ExtendedEventDto.class);

    }

    public Mono<SimpleEventDto> getEventById(Long eventId) {
        return webClient.get()
                .uri("/getEvent/{id}", eventId)
                .retrieve()
                .bodyToMono(SimpleEventDto.class);
    }

}
