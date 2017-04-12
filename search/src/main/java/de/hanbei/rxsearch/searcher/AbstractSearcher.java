package de.hanbei.rxsearch.searcher;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.asynchttpclient.HttpResponseStatus;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSearcher implements Searcher {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

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
                    final String country = query.getCountry();

                    MetricRegistry searcherMetrics = getMetricRegistry();
                    Timer.Context timer = searcherMetrics.timer(metricName(country, name)).time();

                    asyncHttpClient.executeRequest(request, new AsyncCompletionHandler<Response>() {
                        @Override
                        public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                            int statusCode = status.getStatusCode();
                            if (statusCode >= 300) {
                                searcherMetrics.counter(metricName(country, name, ERROR, statusCode)).inc();
                                subscriber.onError(new SearcherException(statusCode + " " + status.getStatusText()).searcher(getName()).query(query));
                            }
                            return STATE.CONTINUE;
                        }

                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            searcherMetrics.counter(metricName(country, name, SUCCESS)).inc();
                            subscriber.onNext(response);
                            subscriber.onComplete();
                            timer.stop();
                            return response;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            searcherMetrics.counter(metricName(country, "general", "error")).inc();
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
