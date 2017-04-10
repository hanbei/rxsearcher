package de.hanbei.rxsearch.searcher;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSearcher implements Searcher {

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
                .flatMap(responseParser::toSearchResults);
    }

    private Observable<Response> asyncGet(Query query) {
        return Observable.create(subscriber -> {
                    final Request request = urlBuilder.createRequest(query);
                    MetricRegistry searcherMetrics = getMetricRegistry();
                    Timer.Context timer = searcherMetrics.timer(query.getCountry() + "." + name).time();
                    asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            if (response.getStatusCode() < 300) {
                                subscriber.onNext(response);
                                subscriber.onComplete();
                            } else {
                                subscriber.onError(new SearcherException(response.getStatusCode() + " " + response.getStatusText()).searcher(getName()).query(query));
                            }
                            timer.stop();
                            return response;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            subscriber.onError(new SearcherException(t).searcher(getName()).query(query));
                            timer.stop();
                        }
                    });
                }
        );
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{\"").append(getClass().getSimpleName()).append("\":{")
                .append("\"name\":\"").append(name).append('"')
                .append("}}");
        return sb.toString();
    }
}
