package rxsearch.searcher;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.joining;
import static rxsearch.searcher.Method.GET;

public class SearcherRequest {

    private final Method method;
    private final String protocol;
    private final String host;
    private final String path;
    private final int port;
    private Multimap<String, String> query;

    public SearcherRequest(Method method, String protocol, String host, int port, String path, Multimap<String, String> query) {
        this.method = method;
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.port = port;
        this.query = query;
    }

    public static SearcherRequestBuilder from(String url) throws MalformedURLException {
        return from(new URL(url));
    }

    public static SearcherRequestBuilder from(URL url) {
        return new SearcherRequestBuilder(url);
    }

    public static SearcherRequestBuilder from() {
        return new SearcherRequestBuilder();
    }

    private static SearcherRequestBuilder from(SearcherRequest request) {
        return new SearcherRequestBuilder(request);
    }

    public String protocol() {
        return protocol;
    }

    public String host() {
        return host;
    }

    public String path() {
        return path;
    }

    public Multimap<String, String> query() {
        return query;
    }

    public List<String> query(String name) {
        return ImmutableList.copyOf(query.get(name));
    }

    public int port() {
        return port;
    }

    public Method method() {
        return method;
    }

    public static class SearcherRequestBuilder {
        private final String protocol;
        private final String host;
        private final int port;
        private final List<String> path;
        private final Multimap<String, String> query;
        private final Method method;

        public SearcherRequestBuilder() {
            this(GET, "http", "localhost", 80, newArrayList(), MultimapBuilder.linkedHashKeys().arrayListValues().build());
        }

        public SearcherRequestBuilder(URL url) {
            this(GET, url.getProtocol(), url.getHost(), portOrDefault(url), newArrayList(url.getPath()), parseQuery(url.getQuery()));
        }

        private static Multimap<String, String> parseQuery(String query) {
            return Stream.of(query.split("&"))
                    .map(s -> s.split("="))
                    .collect(
                            () -> MultimapBuilder.linkedHashKeys().arrayListValues().build(),
                            (map, entry) -> map.put(entry[0], entry[1]),
                            Multimap::putAll

                    );
        }

        private static int portOrDefault(URL url) {
            return url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        }

        private static String removeBeginningSlash(String path) {
            if (path.startsWith("/")) {
                return path.substring(1);
            }
            return path;
        }

        private static String removeEndingSlash(String path) {
            if (path.endsWith("/")) {
                return path.substring(0, path.length() - 1);
            }
            return path;
        }


        public SearcherRequestBuilder(SearcherRequest request) {
            this(request.method(), request.protocol(), request.host(), request.port(), newArrayList(request.path()), request.query());
        }

        private SearcherRequestBuilder(Method method, String protocol, String host, int port, List<String> path, Multimap<String, String> query) {
            this.method = method;
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.path = path;
            this.query = query;
        }

        public SearcherRequest build() {
            return new SearcherRequest(method, protocol, host, port, buildPath(path), query);
        }

        private String buildPath(List<String> path) {
            String collect = path.stream()
                    .map(SearcherRequestBuilder::removeBeginningSlash)
                    .map(SearcherRequestBuilder::removeEndingSlash)
                    .filter(not(Strings::isNullOrEmpty))
                    .collect(joining("/", "/", ""));
            return collect;
        }

        public SearcherRequestBuilder method(Method method) {
            return new SearcherRequestBuilder(method, protocol, host, port, path, query);
        }

        public SearcherRequestBuilder scheme(String protocol) {
            return new SearcherRequestBuilder(method, protocol, host, port, path, query);
        }

        public SearcherRequestBuilder host(String host) {
            return new SearcherRequestBuilder(method, protocol, host, port, path, query);
        }

        public SearcherRequestBuilder path(String path) {
            this.path.add(path);
            return new SearcherRequestBuilder(method, protocol, host, port, this.path, query);
        }

        public SearcherRequestBuilder query(String name, String value) {
            query.put(name, value);
            return new SearcherRequestBuilder(method, protocol, host, port, path, query);
        }

        public SearcherRequestBuilder port(int port) {
            return new SearcherRequestBuilder(method, protocol, host, port, path, query);
        }
    }
}
