package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkPlayer;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.client.loadbalancing.VoiceRegion;
import dev.arbjerg.lavalink.protocol.v4.PlayerState;
import dev.arbjerg.lavalink.protocol.v4.VoiceState;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
public class VoiceUpdateHandler {


    public static Mono<?> install(GatewayDiscordClient gatewayDiscordClient, LavalinkClient lavalinkClient) {
        Mono<Void> voiceStateUpdate = gatewayDiscordClient.on(VoiceStateUpdateEvent.class)
        .flatMap(event -> {
            discord4j.core.object.VoiceState update = event.getCurrent();
            if (!update.getUserId().equals(update.getClient().getSelfId()))
                return Mono.empty();

            long guildId = update.getGuildId().asLong();

            Link link = lavalinkClient.getLinkIfCached(guildId);
            if(link == null)
                return Mono.error(new VoiceUpdateHandlerException(new NullPointerException("Link was undefined"), "Link was expected to be cached", guildId));

            LavalinkPlayer player = link.getNode().getCachedPlayer(guildId);
            if(player == null)
                return Mono.error(new VoiceUpdateHandlerException(new NullPointerException("LavalinkPlayer was undefined"), "Player was expected to be cached", guildId));

            PlayerState playerState = player.getState();

            if (update.getChannelId().isEmpty() && playerState.getConnected()) {
                link.setState$lavalink_client(LinkState.DISCONNECTED);
                return link.destroy();
            } else {
                link.setState$lavalink_client(LinkState.CONNECTED);
                return Mono.empty();
            }
        }).then();

        Mono<Void> voiceServerUpdate = gatewayDiscordClient.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .next()
                .flatMap(event -> {
                    VoiceState voiceState = new VoiceState(
                            event.getToken(),
                            Objects.requireNonNull(event.getEndpoint()),
                            gatewayDiscordClient.getGatewayClient(event.getShardInfo().getIndex()).get().getSessionId());
                    Link link = lavalinkClient.getOrCreateLink(
                            event.getGuildId().asLong(),
                            VoiceRegion.fromEndpoint(Objects.requireNonNull(event.getEndpoint())));
                    link.onVoiceServerUpdate(voiceState);
                    return Mono.empty();
                }).then();

        return voiceStateUpdate.and(voiceServerUpdate);
    }

    @Getter
    public static class VoiceUpdateHandlerException extends BotInternalException {

        private final long guildId;
        public VoiceUpdateHandlerException(Throwable cause, String discordMessage, long guildId) {
            super(cause, discordMessage);
            this.guildId = guildId;
        }
    }
}
