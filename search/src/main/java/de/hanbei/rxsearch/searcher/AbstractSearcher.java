package de.hanbei.rxsearch.searcher;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSearcher implements Searcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearcher.class);
    private final String name;
    private final RequestBuilder urlBuilder;
    private final ResponseParser responseParser;
    private final AsyncHttpClient asyncHttpClient;

    public AbstractSearcher(String name, RequestBuilder urlBuilder, ResponseParser responseParser, AsyncHttpClient asyncHttpClient) {
        this.name = name;
        this.urlBuilder = urlBuilder;
        this.responseParser = responseParser;
        this.asyncHttpClient = asyncHttpClient;
    }

    public String getName() {
        return name;
    }

    public Observable<Offer> search(Query query) {
        return asyncGet(query)
                .timeout(2, TimeUnit.SECONDS)
                .onErrorResumeNext(this::handleSearcherError)
                .flatMap(responseParser::toSearchResults)
                .onErrorResumeNext(this::handleParserError);
    }

    private Observable<Response> asyncGet(Query query) {
        return Observable.create(subscriber -> {
            asyncHttpClient.executeRequest(urlBuilder.createRequest(query), new AsyncCompletionHandler<Response>() {
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
        LOGGER.error(getName() + " experienced error: " + t.getMessage() + " - " + t);
        return Observable.empty();
    }

    private Observable<? extends Offer> handleParserError(Throwable t) {
        LOGGER.error(getName() + " experienced parsing error: " + t.getMessage() + " - " + t);
        return Observable.empty();
    }

}
