package de.hanbei.rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DuckDuckGoSearcher {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final AsyncHttpClient asyncHttpClient;
    private String name;

    public DuckDuckGoSearcher(String name, AsyncHttpClient asyncHttpClient) {
        this.name = name;
        this.asyncHttpClient = asyncHttpClient;
    }

    public String getName() {
        return name;
    }

    public Observable<SearchResult> search(String searchInput) {
        return asyncGet(searchInput).map(this::responseToString).
                flatMap(s -> Observable.from(toSearchResults(s)));
    }

    private Observable<Response> asyncGet(String searchInput) {
        return Observable.create(subscriber -> {
            asyncHttpClient.prepareGet(createRequestUrl(searchInput)).execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    subscriber.onError(t);
                }
            });
        });
    }

    private String createRequestUrl(String searchInput) {
        return "http://api.duckduckgo.com/?q=" + searchInput + "&format=json&t=hanbeirxsearch";
    }

    private String responseToString(Response response) {
        try {
            return response.getResponseBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<SearchResult> toSearchResults(String s) {
        try {

            JsonNode jsonNode = mapper.readTree(s);

            JsonNode relatedTopics = jsonNode.findValue("RelatedTopics");
            List<SearchResult> results = extractResultNodes(relatedTopics);
            return results;
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
