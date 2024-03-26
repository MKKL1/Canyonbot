package com.mkkl.canyonbot.music;

import com.mkkl.canyonbot.commands.exceptions.BotInternalException;
import com.mkkl.canyonbot.music.player.LinkContext;
import com.mkkl.canyonbot.music.player.LinkContextRegistry;
import com.mkkl.canyonbot.music.services.PlayerService;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkPlayer;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.client.loadbalancing.VoiceRegion;
import dev.arbjerg.lavalink.libraries.discord4j.D4JVoiceHandler;
import dev.arbjerg.lavalink.protocol.v4.PlayerState;
import dev.arbjerg.lavalink.protocol.v4.VoiceState;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class VoiceUpdateHandler {

    public VoiceUpdateHandler(GatewayDiscordClient gateway, LavalinkClient lavalinkClient, LinkContextRegistry linkContextRegistry, VoiceConnectionRegistry voiceConnectionRegistry, PlayerService playerService) {
        Mono<Void> voiceStateUpdate = gateway.on(VoiceStateUpdateEvent.class, event -> {
                    //TODO process event reactively
                    discord4j.core.object.VoiceState update = event.getCurrent();
                    if (!update.getUserId().equals(update.getClient().getSelfId()))
                        return Mono.empty();

                    long guildId = update.getGuildId().asLong();

                    if(update.getChannelId().isEmpty()) {
                        voiceConnectionRegistry.remove(guildId);
                    } else {
                        voiceConnectionRegistry.set(guildId);
                    }

                    Optional<LinkContext> linkContextOptional = linkContextRegistry.getCached(update.getGuildId().asLong());

                    //TODO LinkContext being undefined is not always incorrect
                    if(linkContextOptional.isEmpty())
                        return Mono.error(new VoiceUpdateHandlerException(new NullPointerException("LinkContext was undefined"), "LinkContext was expected to be cached", guildId));
                    LinkContext linkContext = linkContextOptional.get();

                    Link link = linkContext.getLink();

                    LavalinkPlayer player = link.getNode().getCachedPlayer(guildId);
                    if(player == null)
                        return Mono.error(new VoiceUpdateHandlerException(new NullPointerException("LavalinkPlayer was undefined"), "Player was expected to be cached", guildId));

                    PlayerState playerState = player.getState();

                    if (update.getChannelId().isEmpty() && playerState.getConnected()) {
                        link.setState$lavalink_client(LinkState.DISCONNECTED);
                        return playerService.destroyLinkContext(guildId);
                    } else {
                        link.setState$lavalink_client(LinkState.CONNECTED);
                        return Mono.empty();
                    }

                })
                .onErrorResume(RuntimeException.class, throwable -> {
                    log.error(throwable.getMessage());
                    return Mono.empty();
                }).then();

        Mono<Void> voiceServerUpdate = gateway.on(VoiceServerUpdateEvent.class, event -> {
                    VoiceState voiceState = new VoiceState(
                            event.getToken(),
                            Objects.requireNonNull(event.getEndpoint()),
                            gateway.getGatewayClient(event.getShardInfo().getIndex()).get().getSessionId());
                    Link link = lavalinkClient.getOrCreateLink(
                            event.getGuildId().asLong(),
                            VoiceRegion.fromEndpoint(Objects.requireNonNull(event.getEndpoint())));
                    log.info("voiceServerUpdate");
                    link.onVoiceServerUpdate(voiceState);
                    return Mono.empty();
                })
                .onErrorResume(RuntimeException.class, throwable -> {
                    log.error(throwable.getMessage());
                    return Mono.empty();
                }).then();

        Mono.when(voiceStateUpdate, voiceServerUpdate)
                .subscribe();

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
