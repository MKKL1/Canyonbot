package com.mkkl.canyonbot.commands.completion;

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.QueryBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandOptionCompletion { //TODO interface

    private final Directory directory = new ByteBuffersDirectory();
    private final IndexSearcher searcher;
    private final IndexReader reader;

    public CommandOptionCompletion(List<Document> documents) {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        try(IndexWriter writer = new IndexWriter(directory, config)) {
            for(Document document : documents)
                writer.addDocument(document);
            reader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<List<Document>> handleCommandInteraction(ApplicationCommandInteractionOption option) {
        String typing = option.getValue()
                .map(ApplicationCommandInteractionOptionValue::asString)
                .orElse("");
        if(typing.isEmpty()) return Mono.empty();
        Query query = new QueryBuilder(new StandardAnalyzer()).createBooleanQuery("name", typing);
        try {
            TopDocs topDocs = searcher.search(query, 10);
            List<Document> documents = new ArrayList<>();
            StoredFields storedFields = reader.storedFields();
            for (ScoreDoc hit : topDocs.scoreDocs) {
                documents.add(storedFields.document(hit.doc));
            }
            return Mono.just(documents);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
