package com.simple.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ExtendedEventDto extends BaseDto {
    List<EventDto> data;

    public ExtendedEventDto(Boolean success) {
        this.success = success;
    }
}
