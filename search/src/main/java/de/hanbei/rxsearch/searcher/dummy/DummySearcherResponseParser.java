package de.hanbei.rxsearch.searcher.dummy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.ResponseParser;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DummySearcherResponseParser implements ResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private String name;

    public DummySearcherResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<Offer> toSearchResults(Response response) {
        checkNotNull(response);

        List<Offer> results = new ArrayList<>();

        try {
            String responseBodyAsString = response.getResponseBody(Charsets.UTF_8.name());
            JsonNode jsonNode = mapper.readTree(responseBodyAsString);
            for (JsonNode offer : jsonNode) {
                String url = getFieldStringValue(offer, "url");
                String title = getFieldStringValue(offer, "title");
                String icon = getFieldStringValue(offer, "imageUrl");
                results.add(new Offer(title, name, url, icon));
            }
        } catch (IOException e) {
            return Observable.error(e);
        }
        return Observable.from(results);
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