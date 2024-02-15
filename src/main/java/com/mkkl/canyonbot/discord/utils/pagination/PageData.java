package com.mkkl.canyonbot.discord.utils.pagination;

import lombok.Data;

@Data
public class PageData {
    private final long page;
    private final long size;
    private final long perPage;
}
