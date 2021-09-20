package com.clane.app.core.exception;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApplicationException extends RuntimeException {

    private List<String> errorMsgs = new ArrayList<>();

    public ApplicationException(List<String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public ApplicationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        if (ObjectUtils.isEmpty(errorMsgs)){
            return super.getMessage();
        }
        return this.errorMsgs.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));
    }

}
