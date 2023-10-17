package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public class PlayCommand extends BotCommand {
    private AudioPlayerManager audioPlayerManager;
    public PlayCommand() {
        super(ApplicationCommandRequest.builder()
                .name("play")
                .description("Play a song")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("url")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .description("Song url to play")
                        .required(true)
                        .build())
                .build());
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        //TODO not safe
        String identifier = event.getInteraction().getCommandInteraction().get().getOption("url").get().getValue().get().asString();
        audioPlayerManager.loadItem(identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                event.reply("Loaded " + audioTrack.getInfo().title);
                System.out.println("Loaded " + audioTrack.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
        return event.reply("Playing");
    }
}
