package rxsearch.searcher.github;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import rxsearch.model.Query;
import rxsearch.searcher.RequestBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class GithubRequestBuilder implements RequestBuilder {

    private final String repo;

    public GithubRequestBuilder(String repo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(repo));
        this.repo = repo;
    }

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return new Request.Builder().get()
                .url(HttpUrl.parse("https://api.github.com/search/code").newBuilder().addQueryParameter("q", query.getKeywords() + "+repo:" + repo).build()).build();
    }
}
