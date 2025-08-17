package com.simple.api.controller;

import com.simple.api.dto.ExtendedEventDto;
import com.simple.api.dto.SimpleEventDto;
import com.simple.api.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
}
