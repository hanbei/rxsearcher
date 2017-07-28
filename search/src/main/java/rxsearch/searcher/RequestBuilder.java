package rxsearch.searcher;

import rxsearch.model.Query;
import okhttp3.Request;

import java.util.Base64;

public interface RequestBuilder {

    /**
     * Takes the search parameter and builds the searcher request url. Any other parameters must be set during
     * construction.
     *
     * @param query The search terms. Must not be null or empty.
     * @return The searcher url that can be requested.
     */
    Request createRequest(Query query);

    static String createBasicAuthHeader(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
