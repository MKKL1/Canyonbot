package com.mkkl.canyonbot.discord;

import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//The idead of this service is to provide a method for logging errors when there is no channel available to log message.
//For example when internal error in background task for specific guild fails and cannot be recovered
// it should have some way to notify end users about possible cause of failure without checking console logs
//TODO implement
@Service
public class LastResortErrorHandler {
    private Map<Long, TextChannel> guildTextChannelMap = new ConcurrentHashMap<>();

}
