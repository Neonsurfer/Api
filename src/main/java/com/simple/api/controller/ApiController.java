package com.simple.api.controller;

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

    @GetMapping("/getEvents")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ExtendedEventDto> getEvents() {
        return service.getEvents();
    }

    @GetMapping("/getEvent/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SimpleEventDto> getEventById(@PathVariable Long eventId) {
        return service.getEventById(eventId);
    }

    @PostMapping("/pay/{eventId}/{seatId}/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public Long pay(@PathVariable Long eventId, @PathVariable Long seatId, @PathVariable Long cardId, @RequestHeader("User-Token") String userToken) {
        return service.pay(eventId, seatId, cardId, userToken);
    }
}
