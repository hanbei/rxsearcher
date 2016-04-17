package de.hanbei.rxsearch.searcher;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import rx.Observable;

import java.io.IOException;
import java.util.List;

/**
 * Created by hanbei on 4/17/16.
 */
public abstract class AbstractSearcher implements Searcher {
    protected final AsyncHttpClient asyncHttpClient;
    protected String name;

    public AbstractSearcher(String name, AsyncHttpClient asyncHttpClient) {
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

    private String responseToString(Response response) {
        try {
            String responseBody = response.getResponseBody();
            return responseBody;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String createRequestUrl(String searchInput);

    protected abstract List<SearchResult> toSearchResults(String s);
}
