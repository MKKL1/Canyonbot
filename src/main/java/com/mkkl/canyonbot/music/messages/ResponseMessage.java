package com.mkkl.canyonbot.music.messages;

import com.mkkl.canyonbot.music.messages.generators.ResponseMessageData;
import com.mkkl.canyonbot.music.messages.generators.ResponseMessageDataGenerator;

public interface ResponseMessage {
    ResponseMessageData getMessage();
}
