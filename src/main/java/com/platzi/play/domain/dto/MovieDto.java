package com.platzi.play.domain.dto;

import java.time.LocalDate;

import com.platzi.play.domain.Genre;

// record define un objeto inmutable,
// autogenera los constructores, getters, equals, hashCode y toString
public record MovieDto(
        Long id,
        String title,
        Integer duration,
        Genre genre,
        LocalDate releaseDate,
        Double rating,
        Boolean state
        ) {

}
