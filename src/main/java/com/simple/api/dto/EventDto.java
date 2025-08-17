package com.simple.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EventDto {
    Long eventId;
    String title;
    String location;
    String startTimeStamp;
    String endTimeStamp;

}
