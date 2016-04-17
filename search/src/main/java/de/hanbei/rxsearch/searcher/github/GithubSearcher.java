package de.hanbei.rxsearch.searcher.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.AbstractSearcher;
import de.hanbei.rxsearch.searcher.Searcher;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanbei on 4/17/16.
 */
public class GithubSearcher extends AbstractSearcher {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String repo;


    public GithubSearcher(String name, String repo, AsyncHttpClient asyncHttpClient) {
        super(name, asyncHttpClient);
        this.repo = repo;
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "https://api.github.com/search/code?q=" + searchInput + "+repo:" + repo;
    }

    @Override
    protected List<SearchResult> toSearchResults(String s) {
        List<SearchResult> results = new ArrayList<>();

        try {
            JsonNode jsonNode = mapper.readTree(s);
            JsonNode items = jsonNode.findValue("items");
            if (items != null) {
                for (JsonNode item : items) {
                    results.add(toSearchResult(item));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    private SearchResult toSearchResult(JsonNode item) {
        String url = item.findValue("html_url").asText("");
        String title = item.findValue("name").asText("");
        String icon = "";
        return new SearchResult(url, title, getName(), icon);
    }
}
