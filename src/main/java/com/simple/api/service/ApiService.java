package com.simple.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.api.error.ErrorResponse;
import com.simple.api.error.RemoteServiceException;
import com.simple.simpleLib.dto.ExtendedEventDto;
import com.simple.simpleLib.dto.SimpleEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
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

    public Long pay(Long eventId, String seatId, String cardId, String userToken) throws JsonProcessingException {
        validateUserToken(cardId, userToken);

        return reserveSeatAndPay(eventId, seatId, cardId);
    }

    private Long reserveSeatAndPay(Long eventId, String seatId, String cardId) throws JsonProcessingException {
        log.info("Sending request to core module for reserving event");
        Long reservationId;

        try {
            reservationId = coreWebClient.post()
                    .uri("core/reserve/{eventId}/{seatId}/{cardId}", eventId, seatId, cardId)
                    .retrieve()
                    .bodyToMono(Long.class).block();
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new RemoteServiceException(response.getErrorMessage(), response.getErrorCode());
        }

        log.info("Seat successfully reserved with reservationId: {}", reservationId);
        return reservationId;
    }

    private void validateUserToken(String cardId, String userToken) throws JsonProcessingException {
        log.info("Sending request to core module for user token and card validation");
        boolean validationResult;
        
        try {
            validationResult = Boolean.TRUE.equals(coreWebClient.get()
                    .uri("core/validate/{userToken}/{cardId}", userToken, cardId)
                    .retrieve()
                    .bodyToMono(Boolean.class).block());
        } catch (WebClientResponseException e) {
            ErrorResponse response = objectMapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
            throw new RemoteServiceException(response.getErrorMessage(), response.getErrorCode());
        }

        if (!validationResult) {
            throw new RuntimeException();
        }
        log.info("User token with cardId successfully validated");
    }
}
