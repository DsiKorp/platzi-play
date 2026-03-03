package com.platzi.play.persistence.mapper;

import org.mapstruct.Named;

public class StateMapper {

    @Named("stringToBoolean")
    public static Boolean stringToBoolean(String estado) {
        if (estado == null) {
            return null;
        }

        return switch (estado) {
            case "D" ->
                true;
            case "N" ->
                false;
            default ->
                null;
        };
    }

    @Named("booleanToString")
    public static String booleanToString(Boolean state) {
        return state == null ? null : (state ? "D" : "N");
    }
}
