package de.hanbei.rxsearch.searcher.webhose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import org.asynchttpclient.Response;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.ResponseParser;
import io.reactivex.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebhoseResponseParser implements ResponseParser {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;

    public WebhoseResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<Offer> toSearchResults(Response response) {
        checkNotNull(response);

        List<Offer> results = new ArrayList<>();
        try {
            String responseAsString = response.getResponseBody(Charsets.UTF_8);

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
        return Observable.fromIterable(results);
    }

    private Offer toSearchResult(JsonNode relatedTopic) {
        return Offer.builder()
                .url(getFieldStringValue(relatedTopic, "url"))
                .title(getFieldStringValue(relatedTopic, "title"))
                .price(0.0, "USD")
                .searcher(name).build();
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