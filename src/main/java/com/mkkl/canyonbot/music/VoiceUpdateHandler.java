package com.mkkl.canyonbot.music;

import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.client.loadbalancing.VoiceRegion;
import dev.arbjerg.lavalink.protocol.v4.PlayerState;
import dev.arbjerg.lavalink.protocol.v4.VoiceState;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
public class VoiceUpdateHandler {


    public static Mono<?> install(GatewayDiscordClient gatewayDiscordClient, LavalinkClient lavalinkClient) {
//        Mono<Void> voiceStateUpdate = gatewayDiscordClient.on(VoiceStateUpdateEvent.class)
//        .flatMap(event -> {
//            log.info("VoiceStateUpdateEvent " + event);
//            discord4j.core.object.VoiceState update = event.getCurrent();
//            if (!update.getUserId().equals(update.getClient().getSelfId()))
//                return Mono.empty();
//            Link link = lavalinkClient.getLinkIfCached(update.getGuildId().asLong());
//            assert link != null;
//            PlayerState playerState = Objects.requireNonNull(link.getNode()
//                            .getCachedPlayer(update.getGuildId().asLong())).getState();
//
//            if (update.getChannelId().isEmpty() && playerState.getConnected()) {
//                link.setState$lavalink_client(LinkState.DISCONNECTED);
//                return link.destroy();
//            } else {
//                link.setState$lavalink_client(LinkState.CONNECTED);
//                return Mono.empty();
//            }
//        }).then();

        Mono<Void> voiceServerUpdate = gatewayDiscordClient.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .next()
                .flatMap(event -> {
                    log.info("VoiceServerUpdateEvent " + event);
//                    VoiceState voiceState = new VoiceState(
//                            event.getToken(),
//                            Objects.requireNonNull(event.getEndpoint()),
//                            gatewayDiscordClient.getGatewayClient(event.getShardInfo().getIndex()).get().getSessionId());
//                    Link link = lavalinkClient.getOrCreateLink(
//                            event.getGuildId().asLong(),
//                            VoiceRegion.fromEndpoint(Objects.requireNonNull(event.getEndpoint())));
//                    log.info("link.onVoiceServerUpdate " + voiceState.toString());
//                    link.onVoiceServerUpdate(voiceState);
                    return Mono.empty();
                }).then();
        Mono<Void> voiceServerUpdate2 = gatewayDiscordClient.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .next()
                .flatMap(event -> {
                    log.info("VoiceServerUpdateEvent " + event);
//                    VoiceState voiceState = new VoiceState(
//                            event.getToken(),
//                            Objects.requireNonNull(event.getEndpoint()),
//                            gatewayDiscordClient.getGatewayClient(event.getShardInfo().getIndex()).get().getSessionId());
//                    Link link = lavalinkClient.getOrCreateLink(
//                            event.getGuildId().asLong(),
//                            VoiceRegion.fromEndpoint(Objects.requireNonNull(event.getEndpoint())));
//                    log.info("link.onVoiceServerUpdate " + voiceState.toString());
//                    link.onVoiceServerUpdate(voiceState);
                    return Mono.empty();
                }).then();
        Mono<Void> voiceServerUpdate3 = gatewayDiscordClient.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .next()
                .flatMap(event -> {
                    log.info("VoiceServerUpdateEvent " + event);
//                    VoiceState voiceState = new VoiceState(
//                            event.getToken(),
//                            Objects.requireNonNull(event.getEndpoint()),
//                            gatewayDiscordClient.getGatewayClient(event.getShardInfo().getIndex()).get().getSessionId());
//                    Link link = lavalinkClient.getOrCreateLink(
//                            event.getGuildId().asLong(),
//                            VoiceRegion.fromEndpoint(Objects.requireNonNull(event.getEndpoint())));
//                    log.info("link.onVoiceServerUpdate " + voiceState.toString());
//                    link.onVoiceServerUpdate(voiceState);
                    return Mono.empty();
                }).then();

        return voiceServerUpdate.and(voiceServerUpdate2).and(voiceServerUpdate3);
    }
}
