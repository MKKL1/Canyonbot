package com.mkkl.canyonbot.discord;

import discord4j.discordjson.possible.Possible;

import java.util.Optional;
import java.util.function.Function;

//Used for compability with discord4j's Possible
public class PossibleUtil {
    public static <T> Possible<T> toPossible(Optional<T> value) {
        return value.map(Possible::of).orElseGet(Possible::absent);
    }
    public static <T> Possible<T> toPossible(T value) {
        return toPossible(Optional.ofNullable(value));
    }
    public static <T, R> Possible<R> mapPossible(Possible<T> value, Function<? super T, ? extends R> mapper) {
        return value.isAbsent() ? Possible.absent() : Possible.of(mapper.apply(value.get()));
    }
}
