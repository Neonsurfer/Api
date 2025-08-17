package com.simple.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SimpleEventDto extends BaseDto {
    Long eventId;
    List<SeatDto> seats;

    public SimpleEventDto(Boolean success) {
        this.success = success;
    }
}
