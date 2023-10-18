package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RegisterCommand
public class PlayCommand extends BotCommand {
    private final AudioPlayerManager audioPlayerManager;
    public PlayCommand(AudioPlayerManager audioPlayerManager) {
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
        this.audioPlayerManager = audioPlayerManager;
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        //TODO not safe
        String identifier = event.getInteraction().getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("url"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString).orElseThrow();//TODO throw exception
        return event.deferReply().then(handleIdentifier(event, identifier)).then();
    }

    private Mono<Message> handleIdentifier(ChatInputInteractionEvent event, String identifier) {
        ResultHandler resultHandler = new ResultHandler(event);
        try {
            audioPlayerManager.loadItem(identifier, resultHandler).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return resultHandler.response;
    }

    private static class ResultHandler implements AudioLoadResultHandler {

        private Mono<Message> response;
        private final ChatInputInteractionEvent event;

        public ResultHandler(ChatInputInteractionEvent event) {
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            response = event.editReply("Loaded " + audioTrack.getInfo().title);
            //Add to queue
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            response = event.editReply("Loaded playlist " + audioPlaylist.getTracks()
                    .stream()
                    .map(audioTrack -> audioTrack.getInfo().title)
                    .reduce("", (s, s2) -> s + "\n" + s2));
        }

        @Override
        public void noMatches() {
            response = event.editReply("No match found");
        }

        @Override
        public void loadFailed(FriendlyException e) {
            response = event.editReply("Load failed: " + e.getMessage());
        }
    }
}
