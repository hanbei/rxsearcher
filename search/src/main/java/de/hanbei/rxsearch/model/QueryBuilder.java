package de.hanbei.rxsearch.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class QueryBuilder {

    public interface KeywordStep {
        RequestIdStep keywords(String keywords);
    }

    public interface RequestIdStep {
        CountryStep requestId(String requestId);
    }

    public interface CountryStep {
        BuildStep country(String country);
    }

    public interface BuildStep {
        Query build();
    }

    static class Steps implements KeywordStep, RequestIdStep, CountryStep, BuildStep {

        private String keywords;
        private String requestId;
        private String country;

        @Override
        public RequestIdStep keywords(String keywords) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(keywords));
            this.keywords = keywords;
            return this;
        }

        @Override
        public CountryStep requestId(String requestId) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(requestId));
            this.requestId = requestId;
            return this;
        }

        @Override
        public BuildStep country(String country) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(country));
            this.country = country;
            return this;
        }

        @Override
        public Query build() {
            return new Query(keywords, requestId, country);
        }
    }

    private QueryBuilder() {
        // construction via buildee
    }
}
