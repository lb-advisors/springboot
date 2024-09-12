package com.lbadvisors.pffc.exception;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusMessage {
    private int statusCode;
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC", shape = JsonFormat.Shape.STRING)
    private Instant timestamp;
    private String message;
    private String description;

    public StatusMessage(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
        this.message = message;
        this.description = description;
    }
}