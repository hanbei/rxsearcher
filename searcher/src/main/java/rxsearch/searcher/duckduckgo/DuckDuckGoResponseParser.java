package rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import rxsearch.model.Hit;
import rxsearch.searcher.ResponseParser;
import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DuckDuckGoResponseParser implements ResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;

    public DuckDuckGoResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<Hit> toSearchResults(Response response) {
        checkNotNull(response);

        try (ResponseBody body = response.body()) {
            String responseAsString = body.string();
            if (Strings.isNullOrEmpty(responseAsString)) {
                return Observable.empty();
            }

            JsonNode jsonNode = mapper.readTree(responseAsString);
            JsonNode relatedTopics = jsonNode.findValue("RelatedTopics");
            if (relatedTopics != null) {
                return Observable.fromIterable(extractResultNodes(relatedTopics));
            } else {
                return Observable.empty();
            }
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private List<Hit> extractResultNodes(JsonNode jsonNode) {
        List<Hit> results = new ArrayList<>();
        for (JsonNode relatedTopic : jsonNode) {
            if (relatedTopic.has("Topics")) {
                results.addAll(extractResultNodes(relatedTopic.get("Topics")));
            } else {
                results.add(toSearchResult(relatedTopic));
            }
        }
        return results;
    }

    private Hit toSearchResult(JsonNode relatedTopic) {
        String url = getFieldStringValue(relatedTopic, "FirstURL");
        String title = getFieldStringValue(relatedTopic, "Text");
        String icon = getIcon(relatedTopic);

        return Hit.builder()
                .url(url)
                .title(title)
                .searcher(name)
                .image(icon)
                .build();
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
