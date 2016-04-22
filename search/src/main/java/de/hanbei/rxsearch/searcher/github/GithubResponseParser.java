package de.hanbei.rxsearch.searcher.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.ResponseParser;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fschulz on 22.04.2016.
 */
public class GithubResponseParser implements ResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;

    public GithubResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<SearchResult> toSearchResults(String responseAsString) {
        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode jsonNode = mapper.readTree(responseAsString);
            JsonNode items = jsonNode.findValue("items");
            if (items == null) {
                return Observable.empty();
            }
            for (JsonNode item : items) {
                results.add(toSearchResult(item));
            }
        } catch (IOException e) {
            return Observable.error(e);
        }

        return Observable.from(results);
    }

    private SearchResult toSearchResult(JsonNode item) {
        String url = item.findValue("html_url").asText("");
        String title = item.findValue("name").asText("");
        String icon = "";
        return new SearchResult(url, title, name, icon);
    }
}
