package de.hanbei.rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.AbstractSearcher;
import de.hanbei.rxsearch.searcher.Searcher;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuckDuckGoSearcher extends AbstractSearcher {

    private static final ObjectMapper mapper = new ObjectMapper();

    public DuckDuckGoSearcher(String name, AsyncHttpClient asyncHttpClient) {
        super(name, asyncHttpClient);
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "http://api.duckduckgo.com/?format=json&t=hanbeirxsearch&q=" + searchInput;
    }

    @Override
    protected List<SearchResult> toSearchResults(String s) {
        if (Strings.isNullOrEmpty(s)) {
            return Collections.emptyList();
        }
        try {
            JsonNode jsonNode = mapper.readTree(s);
            JsonNode relatedTopics = jsonNode.findValue("RelatedTopics");
            return extractResultNodes(relatedTopics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<SearchResult> extractResultNodes(JsonNode jsonNode) {
        List<SearchResult> results = new ArrayList<>();
        for (JsonNode relatedTopic : jsonNode) {
            if (relatedTopic.has("Topics")) {
                results.addAll(extractResultNodes(relatedTopic.get("Topics")));
            } else {
                results.add(toSearchResult(relatedTopic));
            }
        }
        return results;
    }

    private SearchResult toSearchResult(JsonNode relatedTopic) {
        String url = getFieldStringValue(relatedTopic, "FirstURL");
        String title = getFieldStringValue(relatedTopic, "Text");
        String icon = getIcon(relatedTopic);
        return new SearchResult(url, title, getName(), icon);
    }

    private String getIcon(JsonNode relatedTopic) {
        JsonNode icon = relatedTopic.findValue("Icon");
        if (icon != null) {
            JsonNode url = icon.findValue("URL");
            if (url != null) {
                return url.asText();
            }
        }
        return "";
    }

    private String getFieldStringValue(JsonNode relatedTopic, String fieldName) {
        String result = "";
        JsonNode node = relatedTopic.findValue(fieldName);
        if (node != null) {
            result = node.asText();
        }
        return result;
    }

}
