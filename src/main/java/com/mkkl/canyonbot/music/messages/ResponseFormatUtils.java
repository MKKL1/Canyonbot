package com.mkkl.canyonbot.music.messages;

import java.time.Duration;

public class ResponseFormatUtils {
    public static String formatDuration(long duration) {
        Duration trackDuration = Duration.ofMillis(duration);
        return String.format("%02d:%02d:%02d", trackDuration.toHours(), trackDuration.toMinutes() % 60, trackDuration.getSeconds() % 60);
    }
}
