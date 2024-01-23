package com.Pf_Artis.exception;

public class EntityAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntityAlreadyExistsException() {
        super();
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}