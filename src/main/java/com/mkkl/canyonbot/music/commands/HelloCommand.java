package com.mkkl.canyonbot.music.commands;

import com.mkkl.canyonbot.commands.BotCommand;
import com.mkkl.canyonbot.commands.DefaultErrorHandler;
import com.mkkl.canyonbot.commands.RegisterCommand;
import com.mkkl.canyonbot.discord.interaction.CustomButton;
import com.mkkl.canyonbot.discord.interaction.ImmutableCustomButton;
import com.mkkl.canyonbot.discord.response.Response;
import com.mkkl.canyonbot.discord.response.ResponseInteraction;
import com.mkkl.canyonbot.discord.response.TestChannel;
import com.mkkl.canyonbot.music.services.PlayTrackService;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@RegisterCommand
public class HelloCommand extends BotCommand {
    public HelloCommand(DefaultErrorHandler errorHandler) {
        super(ApplicationCommandRequest.builder()
                .name("hello")
                .description("Say hello")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("channel")
                        .type(ApplicationCommandOption.Type.CHANNEL.getValue())
                        .description("Channel to play in")
                        .required(false)
                        .build())
                .build(), errorHandler);
    }

    static Mono<VoiceServerUpdateEvent> onVoiceServerUpdate(GatewayDiscordClient gateway, Snowflake guildId) {
        return gateway.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .filter(vsu -> vsu.getGuildId().equals(guildId) && vsu.getEndpoint() != null)
                .next();
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        Mono<Channel> channel = event.getInteraction()
                .getCommandInteraction()
                .flatMap(commandInteraction -> commandInteraction.getOption("channel"))
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asChannel)
                .orElse(Mono.empty());

        return channel.flatMap(channel1 -> {
                    System.out.println("STARTKURWA");
                    if(channel1 instanceof AudioChannel) {
//                        return TestChannel.builder().build().asRequest()
//                                .apply((AudioChannel) channel1);
                        AudioChannel voiceChannel = (AudioChannel) channel1;
                        final Snowflake guildId = voiceChannel.getGuildId();

                        Mono<Void> newConnection =
                                voiceChannel.sendConnectVoiceState(false, false)
                                .then(onVoiceServerUpdate(voiceChannel.getClient(), guildId))
                                .flatMap(voiceServer -> {
                                    System.out.println("KURWAAAAAAAAAAAAAAAAAAA " + voiceServer);
                                    return Mono.empty();
                                })
                                .then();
                        return newConnection;
                    }
                    return Mono.empty();
                }).then(event.reply("siema"));
    }
}
