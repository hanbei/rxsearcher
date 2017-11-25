package rxsearch.searcher;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Charsets;
import com.google.common.net.MediaType;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rxsearch.model.Hit;
import rxsearch.model.Query;

import java.io.IOException;
import java.nio.ByteBuffer;
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

    public Observable<Hit> search(Query query) {
        return asyncGet(query)
                .timeout(2, TimeUnit.SECONDS)
                .flatMap(responseParser::toSearchResults)
                .doOnError(t -> {
                    MetricRegistry searcherMetrics = getMetricRegistry();
                    searcherMetrics.counter(metricName(query.getCountry(), name, ERROR)).inc();
                });
    }

    private Observable<SearcherResponse> asyncGet(Query query) {
        return Observable.create(subscriber -> {
                    final Request request = requestBuilder.createRequest(query);
                    final String country = query.getCountry();

                    MetricRegistry searcherMetrics = getMetricRegistry();
                    Timer.Context timer = searcherMetrics.timer(metricName(country, name)).time();

                    Call httpCall = httpClient.newCall(request);
                    httpCall.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call request, Response response) {
                            try (ResponseBody body = response.body()) {
                                if (!response.isSuccessful()) {
                                    int statusCode = response.code();
                                    searcherMetrics.counter(metricName(country, name, ERROR, statusCode)).inc();
                                    subscriber.onError(new SearcherException(statusCode + " " + response.message()).searcher(getName()).query(query));
                                    response.close();
                                } else {
                                    if (!subscriber.isDisposed()) {
                                        okhttp3.MediaType contentType = body.contentType();
                                        subscriber.onNext(new SearcherResponse(
                                                response.code(),
                                                response.message(),
                                                MediaType.create(contentType.type(), contentType.subtype()).withCharset(contentType.charset(Charsets.UTF_8)),
                                                ByteBuffer.wrap(body.bytes()))
                                        );
                                        subscriber.onComplete();
                                    }
                                    searcherMetrics.counter(metricName(country, name, SUCCESS)).inc();
                                }
                            } catch (IOException e) {
                                if (!subscriber.isDisposed()) {
                                    subscriber.onError(new SearcherException(e).searcher(getName()).query(query));
                                }
                            } finally {
                                timer.stop();
                                timer.close();
                                response.close();
                            }
                        }

                        @Override
                        public void onFailure(Call request, IOException t) {
                            timer.stop();
                            timer.close();
                            subscriber.onError(new SearcherException(t).searcher(getName()).query(query));
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
