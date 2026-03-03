package com.platzi.play.persistence.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.platzi.play.domain.dto.MovieDto;
import com.platzi.play.domain.dto.UpdateMovieDto;
import com.platzi.play.persistence.entity.MovieEntity;

// componentModel = "spring" para que Spring pueda inyectar esta interfaz como un bean, 
// uses = {GenreMapper.class, StateMapper.class} para indicar que se utilizan otros mappers para mapear campos específicos
@Mapper(componentModel = "spring", uses = {GenreMapper.class, StateMapper.class})
public interface MovieMapper {

    @Mapping(source = "titulo", target = "title")
    @Mapping(source = "duracion", target = "duration")
    // stringToGenre es un método estático en GenreMapper que convierte un String a un Genre
    @Mapping(source = "genero", target = "genre", qualifiedByName = "stringToGenre")
    @Mapping(source = "fechaEstreno", target = "releaseDate")
    @Mapping(source = "clasificacion", target = "rating")
    // stringToBoolean es un método estático en StateMapper que convierte un String a un Boolean
    @Mapping(source = "estado", target = "state", qualifiedByName = "stringToBoolean")
    MovieDto toDto(MovieEntity entity);

    List<MovieDto> toDto(Iterable<MovieEntity> entities);

    @InheritInverseConfiguration
    @Mapping(source = "genre", target = "genero", qualifiedByName = "genreToString")
    @Mapping(source = "state", target = "estado", qualifiedByName = "booleanToString")
    MovieEntity toEntity(MovieDto dto);

    @Mapping(source = "title", target = "titulo")
    @Mapping(source = "releaseDate", target = "fechaEstreno")
    @Mapping(source = "rating", target = "clasificacion")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "duracion", ignore = true)
    @Mapping(target = "genero", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromDto(UpdateMovieDto updateMovieDto, @MappingTarget MovieEntity movieEntity);

}
