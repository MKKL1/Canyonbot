package com.mkkl.canyonbot.music.search.internal.sources;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterSource {
    int priority() default 0;
}
