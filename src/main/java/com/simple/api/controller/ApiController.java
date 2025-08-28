package com.simple.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simple.api.service.ApiService;
import com.simple.simpleLib.dto.ExtendedEventDto;
import com.simple.simpleLib.dto.SimpleEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController("api")
public class ApiController {

    @Autowired
    private ApiService service;

    /**
     * Requests events from Partner microservice
     *
     * @return returns a list of events
     * @throws JsonProcessingException if response is erroneous
     */
    @GetMapping("/getEvents")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ExtendedEventDto> getEvents() {
        return service.getEvents();
    }

    /**
     * Requests event by id from Ticket microservice
     *
     * @param eventId id of event to be requested
     * @return dto of event or exception if not found
     * @throws JsonProcessingException if response is erroneous
     */
    @GetMapping("/getEvent/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SimpleEventDto> getEventById(@PathVariable Long eventId) {
        return service.getEventById(eventId);
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
    @PostMapping("/pay/{eventId}/{seatId}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Long pay(@PathVariable Long eventId, @PathVariable String seatId, @PathVariable String cardId, @RequestHeader("User-Token") String userToken) throws JsonProcessingException {
        return service.pay(eventId, seatId, cardId, userToken);
    }
}
