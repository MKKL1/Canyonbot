package com.mkkl.canyonbot.discord.utils.pagination;

import discord4j.core.spec.EmbedCreateSpec;
import lombok.AllArgsConstructor;

import java.util.function.Function;

public class PaginationController<T extends EmbedCreateSpec> {
    private long currentPage;
    private final long sizePerPage;
    private final long pages;
    private final Function<PageData, T> pageConstructor;

    public PaginationController(long currentPage, long sizePerPage, long pages, Function<PageData, T> pageConstructor) {
        this.currentPage = currentPage;
        this.sizePerPage = sizePerPage;
        this.pages = pages;
        this.pageConstructor = pageConstructor;
    }

    public T next() {
        currentPage++;
        return createPage();
    }

    public T prev() {
        currentPage--;
        return createPage();
    }

    private void checkRange() {
        if(currentPage < 0) currentPage = 0;
        else if (currentPage >= pages) currentPage = pages-1;
    }

    private T createPage() {
        return pageConstructor.apply(new PageData(currentPage, pages, sizePerPage));
    }
}
