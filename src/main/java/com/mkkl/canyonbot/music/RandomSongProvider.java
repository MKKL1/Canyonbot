package com.mkkl.canyonbot.music;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class RandomSongProvider implements InitializingBean {

    // Reads property from environment/application properties.
    // If not provided, defaults to "random-songs.txt".
    @Value("${songs:songs.txt}")
    private String randomSongsPath;
    @Getter
    @Value("${rnd:0.2}")
    private float chance;

    private List<String> randomLinks;
    private final Random random = new Random();

    @Override
    public void afterPropertiesSet() throws Exception {
        // Create a path from the provided configuration.
        Path path = Paths.get(randomSongsPath);
        if (!Files.exists(path)) {
            throw new IllegalStateException("Random songs file not found at: " + randomSongsPath);
        }
        randomLinks = Files.readAllLines(path);
        // Remove blank lines if any.
        randomLinks.removeIf(String::isBlank);

        log.info("Loaded {} songs, chance {}", randomLinks.size(), chance);
    }

    public String getRandomSong() {
        if (randomLinks == null || randomLinks.isEmpty()) {
            return null;
        }
        int idx = random.nextInt(randomLinks.size());
        return randomLinks.get(idx);
    }
}
