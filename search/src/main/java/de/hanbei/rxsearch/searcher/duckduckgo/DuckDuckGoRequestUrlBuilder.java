package de.hanbei.rxsearch.searcher.duckduckgo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class DuckDuckGoRequestUrlBuilder implements RequestUrlBuilder {

    @Override
    public String createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return "http://api.duckduckgo.com/?format=json&t=hanbeirxsearch&q=" + searchInput;
    }

}
