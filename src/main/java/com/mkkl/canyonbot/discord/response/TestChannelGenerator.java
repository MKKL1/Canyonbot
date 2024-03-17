/*
 * This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mkkl.canyonbot.discord.response;

import discord4j.common.LogUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.AudioChannel;
import discord4j.core.spec.AudioChannelJoinSpec;
import discord4j.core.spec.DefaultVoiceServerUpdateTask;
import discord4j.discordjson.json.gateway.VoiceStateUpdate;
import discord4j.gateway.GatewayClientGroup;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.json.ShardGatewayPayload;
import discord4j.voice.AudioProvider;
import discord4j.voice.AudioReceiver;
import discord4j.voice.LocalVoiceReceiveTaskFactory;
import discord4j.voice.LocalVoiceSendTaskFactory;
import discord4j.voice.VoiceChannelRetrieveTask;
import discord4j.voice.VoiceConnection;
import discord4j.voice.VoiceDisconnectTask;
import discord4j.voice.VoiceGatewayOptions;
import discord4j.voice.VoiceReceiveTaskFactory;
import discord4j.voice.VoiceSendTaskFactory;
import discord4j.voice.VoiceServerOptions;
import discord4j.voice.VoiceServerUpdateTask;
import discord4j.voice.VoiceStateUpdateTask;
import org.immutables.value.Value;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.retry.RetrySpec;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static discord4j.common.LogUtil.format;

@Value.Immutable(singleton = true)
interface TestChannelGenerator {

    static Mono<VoiceServerUpdateEvent> onVoiceServerUpdate(GatewayDiscordClient gateway, Snowflake guildId) {
        return gateway.getEventDispatcher()
                .on(VoiceServerUpdateEvent.class)
                .filter(vsu -> vsu.getGuildId().equals(guildId) && vsu.getEndpoint() != null)
                .next();
    }


    default Function<AudioChannel, Mono<Void>> asRequest() {
        return voiceChannel -> {
            final GatewayDiscordClient gateway = voiceChannel.getClient();
            final Snowflake guildId = voiceChannel.getGuildId();


            Mono<Void> newConnection = voiceChannel.sendConnectVoiceState(false, false)
                    .then(onVoiceServerUpdate(gateway, guildId))
                    .flatMap(voiceServer -> {
                        System.out.println("KURWAAAAAAAAAAAAAAAAAAA " + voiceServer);
                        return Mono.empty();
                    })
                    .then();

            return newConnection;
        };
    }
}
