package de.hanbei.rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.ResponseParser;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DuckDuckGoResponseParser implements ResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;

    public DuckDuckGoResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<SearchResult> toSearchResults(String responseAsString) {
        if (Strings.isNullOrEmpty(responseAsString)) {
            return Observable.empty();
        }
        try {
            JsonNode jsonNode = mapper.readTree(responseAsString);
            JsonNode relatedTopics = jsonNode.findValue("RelatedTopics");
            if (relatedTopics != null) {
                return Observable.from(extractResultNodes(relatedTopics));
            } else {
                return Observable.empty();
            }
        } catch (IOException e) {
            return Observable.error(e);
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
        return new SearchResult(url, title, name, icon);
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
