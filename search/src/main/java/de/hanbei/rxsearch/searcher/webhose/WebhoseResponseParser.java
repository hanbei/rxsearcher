package de.hanbei.rxsearch.searcher.webhose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.ResponseParser;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fschulz on 22.04.2016.
 */
public class WebhoseResponseParser implements ResponseParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    private String name;

    public WebhoseResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<SearchResult> toSearchResults(Response response) {
        checkNotNull(response);

        List<SearchResult> results = new ArrayList<>();
        try {
            String responseAsString = response.getResponseBody(Charsets.UTF_8.name());

            JsonNode jsonNode = mapper.readTree(responseAsString);
            JsonNode posts = jsonNode.findValue("posts");
            if (posts == null) {
                return Observable.empty();
            }
            for (JsonNode node : posts) {
                results.add(toSearchResult(node));
            }
        } catch (IOException e) {
            return Observable.error(e);
        }
        return Observable.from(results);
    }

    private SearchResult toSearchResult(JsonNode relatedTopic) {
        String url = getFieldStringValue(relatedTopic, "url");
        String title = getFieldStringValue(relatedTopic, "title");
        return new SearchResult(url, title, name);
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
