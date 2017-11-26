package rxsearch.searcher;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
    private Multimap<String, String> header;

    public SearcherRequest(Method method, String protocol, String host, int port, String path, Multimap<String, String> query, Multimap<String, String> header) {
        this.method = method;
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.port = port;
        this.query = ImmutableListMultimap.copyOf(query);
        this.header = ImmutableListMultimap.copyOf(header);
    }

    public static Builder from(String url) {
        return from(URI.create(url));
    }

    public static Builder get(String url) {
        return from(url).method(GET);
    }

    public static Builder from(URI url) {
        return new Builder(url);
    }

    public static Builder from() {
        return new Builder();
    }

    private static Builder from(SearcherRequest request) {
        return new Builder(request);
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

    public Multimap<String, String> header() {
        return header;
    }

    public List<String> header(String name) {
        return ImmutableList.copyOf(header.get(name));
    }

    public int port() {
        return port;
    }

    public Method method() {
        return method;
    }

    public String url() {
        return protocol + "://" + host + optionalPort() + path() + buildQueryParameter();
    }

    private String optionalPort() {
        String result = "";
        if (protocol.equalsIgnoreCase("http")) {
            if (port != 80) {
                result = ":" + port;
            }
        } else if (protocol.equalsIgnoreCase("https")) {
            if (port != 443) {
                result = ":" + port;
            }
        } else if (protocol.equals("ftp")) {
            if (port != 22) {
                result = ":" + port;
            }
        }

        return result;
    }

    private String buildQueryParameter() {
        return query.entries().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(joining("&", "?", ""));
    }

    public static class Builder {
        private final Method method;
        private final String protocol;
        private final String host;
        private final int port;
        private final List<String> path;
        private final Multimap<String, String> query;
        private final Multimap<String, String> header;
        private final SignatureCalculator signatureCalculator;

        public Builder() {
            this(GET, "http", "localhost", 80, newArrayList(), LinkedHashMultimap.create(), LinkedHashMultimap.create(), null);
        }

        public Builder(URI uri) {
            this(GET, uri.getScheme(), uri.getHost(), portOrDefault(uri), newArrayList(uri.getPath()), parseQuery(uri.getQuery()), LinkedHashMultimap.create(), null);
        }

        private Builder(Method method, String protocol, String host, int port, List<String> path, Multimap<String, String> query, Multimap<String, String> header, SignatureCalculator signatureCalculator) {
            this.method = method;
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.path = path;
            this.query = query;
            this.header = header;
            this.signatureCalculator = signatureCalculator;
        }

        public Builder(SearcherRequest request) {
            this(request.method(), request.protocol(), request.host(), request.port(), newArrayList(request.path()), request.query(), request.header(), null);
        }

        private static Multimap<String, String> parseQuery(String query) {
            if (query == null) {
                return MultimapBuilder.linkedHashKeys().arrayListValues().build();
            }
            return Stream.of(query.split("&"))
                    .map(s -> s.split("="))
                    .collect(
                            () -> MultimapBuilder.linkedHashKeys().arrayListValues().build(),
                            (map, entry) -> map.put(entry[0], entry[1]),
                            Multimap::putAll

                    );
        }

        public SearcherRequest build() {
            if (signatureCalculator != null) {
                signatureCalculator.calculateAndAddSignature(
                        new SearcherRequest(method, protocol, host, port, buildPath(path), query, header), this);
            }
            return new SearcherRequest(method, protocol, host, port, buildPath(path), query, header);
        }

        public Builder method(Method method) {
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder scheme(String protocol) {
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder host(String host) {
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder path(String path) {
            this.path.add(path);
            return new Builder(method, protocol, host, port, this.path, query, header, signatureCalculator);
        }

        public Builder query(String name, String value) {
            query.put(name, value);
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder header(String name, String value) {
            header.put(name, value);
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder port(int port) {
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
        }

        public Builder signatureCalculator(SignatureCalculator signatureCalculator) {
            return new Builder(method, protocol, host, port, path, query, header, signatureCalculator);
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

        private String buildPath(List<String> path) {
            return path.stream()
                    .map(Builder::removeBeginningSlash)
                    .map(Builder::removeEndingSlash)
                    .filter(((Predicate<String>) Strings::isNullOrEmpty).negate())
                    .collect(joining("/", "/", ""));
        }

        private static int portOrDefault(URI uri) {
            if (uri.getPort() != -1) {
                return uri.getPort();
            }
            switch (uri.getScheme()) {
                case "acap":
                    return 674;
                case "afp":
                    return 548;
                case "dict":
                    return 2628;
                case "dns":
                    return 53;
                case "ftp":
                    return 21;
                case "git":
                    return 9418;
                case "gopher":
                    return 70;
                case "http":
                    return 80;
                case "https":
                    return 443;
                case "imap":
                    return 143;
                case "ipp":
                    return 631;
                case "ipps":
                    return 631;
                case "irc":
                    return 194;
                case "ircs":
                    return 6697;
                case "ldap":
                    return 389;
                case "ldaps":
                    return 636;
                case "mms":
                    return 1755;
                case "msrp":
                    return 2855;
                case "mtqp":
                    return 1038;
                case "nfs":
                    return 111;
                case "nntp":
                    return 119;
                case "nntps":
                    return 563;
                case "pop":
                    return 110;
                case "prospero":
                    return 1525;
                case "redis":
                    return 6379;
                case "rsync":
                    return 873;
                case "rtsp":
                    return 554;
                case "rtsps":
                    return 322;
                case "rtspu":
                    return 5005;
                case "sftp":
                    return 22;
                case "smb":
                    return 445;
                case "snmp":
                    return 161;
                case "ssh":
                    return 22;
                case "svn":
                    return 3690;
                case "telnet":
                    return 23;
                case "ventrilo":
                    return 3784;
                case "vnc":
                    return 5900;
                case "wais":
                    return 210;
                case "ws":
                    return 80;
                case "wss":
                    return 443;
                default:
                    return -1;
            }
        }
    }
}
