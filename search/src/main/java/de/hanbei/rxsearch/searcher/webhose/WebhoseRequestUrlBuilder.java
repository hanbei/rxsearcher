package de.hanbei.rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class WebhoseRequestUrlBuilder implements RequestUrlBuilder {

    private final String key;

    public WebhoseRequestUrlBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));

        this.key = key;
    }

    @Override
    public String createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return "https://webhose.io/search?token=" + key + "&format=json&size=10&q=" + searchInput;
    }
}
