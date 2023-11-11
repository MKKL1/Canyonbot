package com.mkkl.canyonbot.music.player.queue;

import com.mkkl.canyonbot.music.player.MusicPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

import java.util.function.Function;
import java.util.function.Predicate;

public class TrackScheduler {
    public TrackScheduler(MusicPlayer musicPlayer) {
        //TODO: Implement TrackScheduler
//        musicPlayer.getEventFlux().flatMap(new Function<AudioEvent, Publisher<?>>() {
//            @Override
//            public Publisher<?> apply(AudioEvent audioEvent) {
//                return audioEvent;
//            }
//        }).subscribe();
    }
}
