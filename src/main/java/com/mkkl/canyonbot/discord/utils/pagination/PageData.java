package com.mkkl.canyonbot.discord.utils.pagination;

import lombok.Data;

@Data
public class PageData {
    private final int page;
    private final int size;
    private final int perPage;
}
