package com.mkkl.canyonbot.discord.utils.pagination;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageData {
    private final long page;
    private final long size;
    private final long perPage;
}
