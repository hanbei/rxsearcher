package de.hanbei.rxsearch.searcher;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import rx.Observable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSearcher implements Searcher {

    private final String name;
    private final RequestUrlBuilder urlBuilder;
    private final ResponseParser responseParser;
    private final AsyncHttpClient asyncHttpClient;

    public AbstractSearcher(String name, RequestUrlBuilder urlBuilder, ResponseParser responseParser, AsyncHttpClient asyncHttpClient) {
        this.name = name;
        this.urlBuilder = urlBuilder;
        this.responseParser = responseParser;
        this.asyncHttpClient = asyncHttpClient;
    }

    public String getName() {
        return name;
    }

    public Observable<SearchResult> search(String searchInput) {
        return asyncGet(searchInput)
                .timeout(2, TimeUnit.SECONDS)
                .onErrorResumeNext(this::handleSearcherError)
                .map(this::responseToString)
                .flatMap(responseParser::toSearchResults)
                .onErrorResumeNext(this::handleParserError);
    }

    private Observable<Response> asyncGet(String searchInput) {
        return Observable.create(subscriber -> {
            asyncHttpClient.prepareGet(urlBuilder.createRequestUrl(searchInput)).execute(new AsyncCompletionHandler<Response>() {
                @Override
                public Response onCompleted(Response response) throws Exception {
                    if (response.getStatusCode() < 300) {
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new RuntimeException(getName() + ":" + response.getStatusCode() + " " + response.getStatusText()));
                    }
                    return response;
                }

                @Override
                public void onThrowable(Throwable t) {
                    subscriber.onError(t);
                }
            });
        });
    }


    private Observable<? extends Response> handleSearcherError(Throwable t) {
        System.err.println(getName() + " experienced error: " + t.getMessage() + " - " + t);
        return Observable.empty();
    }

    private Observable<? extends SearchResult> handleParserError(Throwable t) {
        System.err.println(getName() + " experienced parsing error: " + t.getMessage() + " - " + t);
        return Observable.empty();
    }


    private String responseToString(Response response) {
        try {
            return response.getResponseBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
