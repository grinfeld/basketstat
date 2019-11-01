package com.mikerusoft.euroleague.model.exceptions;

import lombok.Getter;

public class AssertResultException extends RuntimeException {
    @Getter private String fromPage;

    public AssertResultException(String message, String fromPage) {
        super(message);
        this.fromPage = fromPage;
    }
}
