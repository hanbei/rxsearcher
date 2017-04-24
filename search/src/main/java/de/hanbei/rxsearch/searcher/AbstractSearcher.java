package de.hanbei.rxsearch.searcher;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSearcher implements Searcher {

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    private final String name;
    private final RequestBuilder requestBuilder;
    private final ResponseParser responseParser;
    private final OkHttpClient httpClient;

    public AbstractSearcher(String name, RequestBuilder urlBuilder, ResponseParser responseParser, OkHttpClient httpClient) {
        this.name = name;
        this.requestBuilder = urlBuilder;
        this.responseParser = responseParser;
        this.httpClient = httpClient;
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
                    final Request request = requestBuilder.createRequest(query);
                    final String country = query.getCountry();

                    MetricRegistry searcherMetrics = getMetricRegistry();
                    Timer.Context timer = searcherMetrics.timer(metricName(country, name)).time();

                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call request, Response response) {
                            int statusCode = response.code();
                            if (statusCode >= 300) {
                                searcherMetrics.counter(metricName(country, name, ERROR, statusCode)).inc();
                                subscriber.onError(new SearcherException(statusCode + " " + response.message()).searcher(getName()).query(query));
                                timer.stop();
                                response.close();
                            } else {
                                searcherMetrics.counter(metricName(country, name, SUCCESS)).inc();
                                subscriber.onNext(response);
                                subscriber.onComplete();
                                timer.stop();
                            }
                        }

                        @Override
                        public void onFailure(Call request, IOException t) {
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
