package rxsearch.searcher.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import rxsearch.model.Hit;
import rxsearch.searcher.ResponseParser;
import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubResponseParser implements ResponseParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String name;

    public GithubResponseParser(String name) {
        this.name = name;
    }

    @Override
    public Observable<Hit> toSearchResults(Response response) {
        checkNotNull(response);

        List<Hit> results = new ArrayList<>();

        try (ResponseBody body = response.body()) {
            String responseAsString = body.string();

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

        return Observable.fromIterable(results);
    }

    private Hit toSearchResult(JsonNode item) {
        String url = item.findValue("html_url").asText("");
        String title = item.findValue("name").asText("");
        String icon = "";

        return Hit.builder()
                .url(url)
                .title(title)
                .searcher(name).image(icon).build();
    }
}
