package com.mkkl.canyonbot.commands.completion;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OptionSuggestionIterator implements InputIterator {

    private final Iterator<SuggestionOption> entityIterator;
    private SuggestionOption currentItem;

    public OptionSuggestionIterator(Iterator<SuggestionOption> entityIterator) {
        this.entityIterator = entityIterator;
    }

    @Override
    public long weight() {
        return currentItem.getWeight();
    }

    @Override
    public boolean hasContexts() {
        return true;
    }

    @Override
    public boolean hasPayloads() {
        return false;
    }

    @Override
    public BytesRef next() {
        if (entityIterator.hasNext()) {
            currentItem = entityIterator.next();
                return new BytesRef(currentItem.getName().getBytes(StandardCharsets.UTF_8));
        } else {
            return null;
        }
    }

    @Override
    public BytesRef payload() {
        return null;
    }

    @Override
    public Set<BytesRef> contexts() {
        final Set<BytesRef> contexts = new HashSet<>();
        for (final String context : currentItem.getContexts()) {
            contexts.add(new BytesRef(context.getBytes(StandardCharsets.UTF_8)));
        }
        return contexts;
    }
}
