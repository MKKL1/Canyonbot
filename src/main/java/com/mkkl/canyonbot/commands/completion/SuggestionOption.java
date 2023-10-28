package com.mkkl.canyonbot.commands.completion;

public interface SuggestionOption {
    long getWeight();
    String getName();
    String getDescription();
    String[] getContexts();
}
