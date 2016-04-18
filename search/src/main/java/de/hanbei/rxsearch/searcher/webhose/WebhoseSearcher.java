package de.hanbei.rxsearch.searcher.webhose;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebhoseSearcher extends AbstractSearcher {

    private final String key;

    public WebhoseSearcher(String name, String key, AsyncHttpClient asyncHttpClient) {
        super(name, asyncHttpClient);
        this.key = key;
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "https://webhose.io/search?token=" + key + "&format=json&q=" + searchInput;
    }

    @Override
    protected List<SearchResult> toSearchResults(String s) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(s));

        List<SearchResult> results = new ArrayList<>();
        try {
            JsonNode jsonNode = mapper.readTree(s);
            JsonNode posts = jsonNode.findValue("posts");
            for (JsonNode node : posts) {
                results.add(toSearchResult(node));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    private SearchResult toSearchResult(JsonNode relatedTopic) {
        String url = getFieldStringValue(relatedTopic, "url");
        String title = getFieldStringValue(relatedTopic, "title");
        return new SearchResult(url, title, getName());
    }

    private String getFieldStringValue(JsonNode node, String fieldName) {
        String result = "";
        JsonNode sub_node = node.findValue(fieldName);
        if (sub_node != null) {
            result = sub_node.asText();
        }
        return result;
    }
}
