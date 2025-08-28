package com.simple.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.api.error.ErrorResponse;
import com.simple.api.error.RemoteServiceException;
import com.simple.api.error.TokenExpiredException;
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

    /**
     * Requests events from Partner microservice
     *
     * @return returns a list of events
     * @throws JsonProcessingException if response is erroneous
     */
    public Mono<ExtendedEventDto> getEvents() {
        log.info("Getting event list from partner service");
        return partnerWebClient.get()
                .uri("/getEvents")
                .retrieve()
                .bodyToMono(ExtendedEventDto.class);

    }

    /**
     * Requests event by id from Ticket microservice
     *
     * @param eventId id of event to be requested
     * @return dto of event or exception if not found
     * @throws JsonProcessingException if response is erroneous
     */
    public Mono<SimpleEventDto> getEventById(Long eventId) {
        log.info("Getting event by id {} from partner service", eventId);
        return partnerWebClient.get()
                .uri("/getEvent/{id}", eventId)
                .retrieve()
                .bodyToMono(SimpleEventDto.class);
    }

    /**
     * Validates user and card connection, then tries to reserve given seat on given event
     *
     * @param eventId   id of event for reservation
     * @param seatId    id of seat to be reserved
     * @param cardId    user's card id for payment
     * @param userToken user token to be validated
     * @return reservation id if successful, exception otherwise
     * @throws JsonProcessingException
     */
    public Long pay(Long eventId, String seatId, String cardId, String userToken) throws JsonProcessingException {
        validateUserToken(cardId, userToken);

        return reserveSeatAndPay(eventId, seatId, cardId);
    }

    /**
     * Tries to reserve a seat on an event.
     *
     * @param eventId id of event for reservation
     * @param seatId  id of seat to be reserved
     * @param cardId  user's card id for payment
     * @return success, and if so, reservationId. Otherwise exception
     * @throws JsonProcessingException if response is erroneous
     */
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

    /**
     * Validates that the user token is connected to give card
     * Throws exception if not connected
     *
     * @param userToken user token to be validated
     * @param cardId    card id subject to validation
     */
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
            throw new TokenExpiredException();
        }
        log.info("User token with cardId successfully validated");
    }
}
