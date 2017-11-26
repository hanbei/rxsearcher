package rxsearch.internal;

import okhttp3.HttpUrl;
import okhttp3.Request;
import rxsearch.searcher.SearcherRequest;

import java.util.Map;

public class SearchRequestTranslator {

    public Request translate(SearcherRequest request) {
        Request.Builder requestBuilder = null;
        switch (request.method()) {
            case POST:
                break;
            case PUT:
                break;
            case DELETE:
                requestBuilder = new Request.Builder().delete();
            default:
                requestBuilder = new Request.Builder().get();
                break;
        }
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder()
                .scheme(request.protocol())
                .host(request.host())
                .port(request.port())
                .addPathSegments(request.path());
        for (Map.Entry<String, String> queryParam : request.query().entries()) {
            httpUrlBuilder.addQueryParameter(queryParam.getKey(), queryParam.getValue());
        }
        return requestBuilder.url(httpUrlBuilder.build()).build();
    }

}
