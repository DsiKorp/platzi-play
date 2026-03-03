package com.platzi.play.domain.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(long id) {
        super("Pelicula con el id " + id + " no encontrada!");
    }
}