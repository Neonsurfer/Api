package com.simple.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ReserveDto extends BaseDto {
    Long reserverId;
    Integer errorCode;

    public ReserveDto(Boolean success) {
        this.success = success;
    }

    public ReserveDto(Boolean success, Long reserverId) {
        this.success = success;
        this.reserverId = reserverId;
    }

    public ReserveDto(Boolean success, Integer errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }
}
