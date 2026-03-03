package com.platzi.play.domain.exception;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException(String movieTitle) {
        super("La película " + movieTitle + " ya existe!");
    }
}
