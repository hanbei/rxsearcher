package de.hanbei.rxsearch.searcher.webhose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hanbei.rxsearch.model.Hit;
import de.hanbei.rxsearch.searcher.ResponseParser;
import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
    public Observable<Hit> toSearchResults(Response response) {
        checkNotNull(response);

        List<Hit> results = new ArrayList<>();
        try (ResponseBody body = response.body()) {
            String responseAsString = body.string();

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

    private Hit toSearchResult(JsonNode relatedTopic) {
        return Hit.builder()
                .url(getFieldStringValue(relatedTopic, "url"))
                .title(getFieldStringValue(relatedTopic, "title"))
                .searcher(name)
                .build();
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
