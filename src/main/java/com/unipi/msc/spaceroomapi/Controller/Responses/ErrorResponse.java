package com.unipi.msc.spaceroomapi.Controller.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse implements Serializable {
    private Boolean success;
    private String msg;
}
